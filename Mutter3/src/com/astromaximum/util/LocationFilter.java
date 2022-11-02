package com.astromaximum.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.Vector;

public class LocationFilter extends SubDataProcessor {

    final BaseEvent[] mEvents = new BaseEvent[3000];
    LocationsDataFile mLocationsDataFile;
    private int mYear;
    private Calendar mCalendar;
    private long mStartTime, mEndTime, mFinalJD;
    private int mStartMonth, mMonthCount;
    private Vector<String> mInputFiles;

    static final int[] EVENT_TYPES = {
        BaseEvent.EV_RISE,
        BaseEvent.EV_SET,
        BaseEvent.EV_ASTRORISE,
        BaseEvent.EV_ASTROSET,
        BaseEvent.EV_NAVROZ
    };

    LocationFilter(int year, Vector<String> inputFiles) {
        mYear = year;
        mInputFiles = inputFiles;
    }

    @Override
    protected void addEvent(int idx, BaseEvent event) {
        mEvents[idx] = new BaseEvent(event);
    }

    public void dumpToFile(int startMonth, int monthCount, long delta,
            String outFile, String csvFile, boolean makeBundle) {
        RandomAccessFile raf;
        mStartMonth = startMonth;
        mMonthCount = monthCount;
        long headerPos = 0;
        try {
            FileWriter fstream = new FileWriter(csvFile);
            BufferedWriter csvOut = new BufferedWriter(fstream);
            System.out.println("\nOpen output file " + outFile + ", mInputFiles.size()=" + mInputFiles.size());
            raf = new RandomAccessFile(outFile, "rw");
            raf.setLength(0);
            System.out.println("raf.len=" + raf.length());
            if (makeBundle) {
                raf.writeShort(mYear);
                raf.writeShort(mInputFiles.size());
                headerPos = raf.getFilePointer();
                for (int i = 0; i < mInputFiles.size(); ++i) {
                    raf.writeShort(0); // fake data length
                }
                System.out.println("raf.len=" + raf.length());
            }
            int fileCounter = 0;
            for (String inputFile : mInputFiles) {
                System.out.println("\nOpen input file " + inputFile);
                FileInputStream fis = new FileInputStream(inputFile);
                mLocationsDataFile = new LocationsDataFile(fis);
                csvOut.write(mLocationsDataFile.mCity + ";"
                        + mLocationsDataFile.mState + ";"
                        + mLocationsDataFile.mCountry + ";"
                        + mLocationsDataFile.mTimezone + ";"
                        + String.format("%08x", mLocationsDataFile.mCityKey) + "\n");

                mCalendar = new GregorianCalendar(
                        TimeZone.getTimeZone(mLocationsDataFile.mTimezone));

                mCalendar.set(mYear, mStartMonth, 1, 0, 0, 0);
                mCalendar.set(Calendar.MILLISECOND, 0);
                mStartTime = mCalendar.getTimeInMillis();

                mCalendar.add(Calendar.MONTH, mMonthCount);
                mEndTime = mCalendar.getTimeInMillis();
                mFinalJD = mEndTime;

                SubDataInfo info = new SubDataInfo();

                String tempPath = "/tmp/locations_" + mYear;
                File path = new File(tempPath);
                path.mkdir();
                File[] tempFiles = path.listFiles();
                for (File tempFile : tempFiles) {
                    tempFile.delete();
                }
                File mergeFile = new File(tempPath + "/" + fileCounter);
                mergeFile.delete();

                for (int evtype : EVENT_TYPES) {
                    for (int planet = -1; planet <= BaseEvent.SE_PLUTO; ++planet) {
                        long start = evtype == BaseEvent.EV_NAVROZ ? 0 : mStartTime - delta;
                        int eventCount = read(mLocationsDataFile.mData, evtype,
                                planet, false, start, mEndTime
                                + delta, mFinalJD, info);
                        if (eventCount > 0) {
                            /*if (evtype == BaseEvent.EV_NAVROZ) {
                             System.out.println("NAVROZ count=" + eventCount);
                             for (int i = 0; i < eventCount; ++i) {
                             System.out.println(mEvents[i].toString());
                             }
                             System.out.println("NAVROZ end");
                             }*/
                            info.mFlags &= ~(EF_CUMUL_DATE_B | EF_CUMUL_DATE_W);
                            info.mFlags |= EF_CUMUL_DATE_B;

                            System.out.println("dumpToFile: "
                                    + BaseEvent.EVENT_TYPE_STR[evtype] + ", "
                                    + planet + ": count " + eventCount + "; "
                                    + "total events=" + info.mTotalCount
                                    + " flags=" + info.mFlags);
                            if (!Mutter.writeToTempFile(tempPath, info,
                                    mEvents, eventCount)) {
                                info.mFlags &= ~EF_CUMUL_DATE_B;
                                info.mFlags |= EF_CUMUL_DATE_W;
                                System.out.println("dumpToFile: "
                                        + BaseEvent.EVENT_TYPE_STR[evtype]
                                        + ", " + planet + " = " + eventCount
                                        + "; " + "total events="
                                        + info.mTotalCount + " flags="
                                        + info.mFlags);
                                if (!Mutter.writeToTempFile(tempPath, info,
                                        mEvents, eventCount)) {
                                    info.mFlags &= ~EF_CUMUL_DATE_W;
                                    System.out.println("dumpToFile: "
                                            + BaseEvent.EVENT_TYPE_STR[evtype]
                                            + ", " + planet + " = "
                                            + eventCount + "; "
                                            + "total events="
                                            + info.mTotalCount + " flags="
                                            + info.mFlags);
                                    Mutter.writeToTempFile(tempPath, info,
                                            mEvents, eventCount);
                                }
                            }
                        }
                    }
                }
                long dataLen = joinTempfiles(tempPath, raf);
                if (makeBundle) {
                    long posEnd = raf.getFilePointer();
                    raf.seek(headerPos);
                    raf.writeShort((int) dataLen);
                    headerPos = raf.getFilePointer();
                    raf.seek(posEnd);
                    System.out.println("raf.seek=" + posEnd + ", len=" + raf.length());
                }
                ++fileCounter;
            }
            csvOut.close();
            System.out.println("raf.getFilePointer()=" + raf.getFilePointer() + ", len=" + raf.length());
            raf.close();
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    private long joinTempfiles(String tempPath, RandomAccessFile raf) {
        long dataLen = 0;
        try {
            int year = mYear;
            int month = mStartMonth + 1;
            if (month <= 0) {
                year -= 1;
                month = 12 + month;
            }
            int day = 1;
                
            System.out.println("joinTempfiles mCity=" + mLocationsDataFile.mCity + ", year=" + year + ", month=" + month + ", day=" + day + ", mMonthCount=" + mMonthCount + ", tempPath=" + tempPath); 
            long dataPos = raf.getFilePointer();
            raf.writeBytes("S&WA");
            raf.writeByte(3); // version
            raf.writeShort(year);
            raf.writeByte(month);
            raf.writeByte(day); // day of month
            raf.writeByte(mMonthCount);
            raf.writeInt(mLocationsDataFile.mCityKey);
            raf.writeShort(mLocationsDataFile.mCoords[0]);
            raf.writeShort(mLocationsDataFile.mCoords[1]);
            raf.writeShort(mLocationsDataFile.mCoords[2]);
            raf.writeUTF(mLocationsDataFile.mCity);
            raf.writeUTF(mLocationsDataFile.mState);
            raf.writeUTF(mLocationsDataFile.mCountry);
            raf.writeUTF(mLocationsDataFile.mTimezone);
            raf.writeShort(0); // custom data length
            raf.writeByte(0); // transition count

            File[] tempFiles = new File(tempPath).listFiles();

            for (File tempFile : tempFiles) {
                raf.writeByte(1);
                FileInputStream fis = new FileInputStream(tempFile);
                byte[] buffer = new byte[(int) tempFile.length()];
                fis.read(buffer);
                fis.close();
                raf.write(buffer);
            }
            dataLen = raf.getFilePointer() - dataPos;
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("dataLen=" + dataLen); 
        return dataLen;
    }
}
