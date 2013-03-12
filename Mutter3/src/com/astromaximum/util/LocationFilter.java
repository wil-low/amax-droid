package com.astromaximum.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Calendar;
import java.util.Vector;

public class LocationFilter extends SubDataProcessor {
	final BaseEvent[] mEvents = new BaseEvent[3000];
	LocationsDataFile mLocationsDataFile;
	private int mYear;
	private Calendar mCalendar = Calendar.getInstance();
	private long mStartTime, mEndTime, mStartJD, mFinalJD;
	private int mStartMonth;
	private Vector<String> mInputFiles;

	static final int[] EVENT_TYPES = { BaseEvent.EV_RISE, BaseEvent.EV_SET };

	LocationFilter(int year, Vector<String> inputFiles) {
		mYear = year;
		mInputFiles = inputFiles;
	}

	@Override
	protected void addEvent(int idx, BaseEvent event) {
		mEvents[idx] = new BaseEvent(event);
	}

	public void dumpToFile(int startMonth, int monthCount, long delta, String outFile) {
		RandomAccessFile raf;
		try {
			raf = new RandomAccessFile(outFile, "rw");
			raf.writeShort(mYear);
			raf.writeShort(mInputFiles.size());
			long headerPos = raf.getFilePointer();
			for (int i = 0; i < mInputFiles.size(); ++i)
				raf.writeShort(0);  // fake data length
			int fileCounter = 0;
			for (String inputFile : mInputFiles) {
				FileInputStream fis = new FileInputStream(inputFile);
				mLocationsDataFile = new LocationsDataFile(fis);
				mCalendar.set(mLocationsDataFile.mStartYear,
						mLocationsDataFile.mStartMonth,
						mLocationsDataFile.mStartDay, 0, 0, 0);
				mStartJD = mCalendar.getTime().getTime();
				mFinalJD = mStartJD + mLocationsDataFile.mDayCount * MSECINDAY;
				System.out.println("Location " + mLocationsDataFile.mStartYear
						+ " " + mLocationsDataFile.mDayCount);
				mStartMonth = startMonth;
		
				mCalendar.set(mYear, mStartMonth, 1, 0, 0, 0);
				mStartTime = mCalendar.getTimeInMillis();
		
				mCalendar.add(Calendar.MONTH, monthCount);
				mEndTime = mCalendar.getTimeInMillis();
		
				SubDataInfo info = new SubDataInfo();
		
				String tempPath = "/tmp/locations_" + mYear;
				File path = new File(tempPath);
				path.mkdir();
				File[] tempFiles = path.listFiles();
				for (File tempFile : tempFiles)
					tempFile.delete();
				File mergeFile = new File(tempPath + "/" + fileCounter);
				mergeFile.delete();
				
				for (int evtype : EVENT_TYPES) {
					for (int planet = -1; planet <= BaseEvent.SE_PLUTO; ++planet) {
						int eventCount = read(mLocationsDataFile.mData, evtype,
								planet, false, mStartTime - delta, mEndTime + delta, mFinalJD, info);
						if (eventCount > 0) {
							info.mFlags &= ~(EF_CUMUL_DATE_B | EF_CUMUL_DATE_W);
							info.mFlags |= EF_CUMUL_DATE_B;
		
							System.out.println("dumpToFile: "
									+ BaseEvent.EVENT_TYPE_STR[evtype] + ", "
									+ planet + " = " + eventCount + "; "
									+ "total events=" + info.mTotalCount
									+ " flags=" + info.mFlags);
							if (!Mutter.writeToTempFile(tempPath, info, mEvents,
									eventCount)) {
								info.mFlags &= ~EF_CUMUL_DATE_B;
								info.mFlags |= EF_CUMUL_DATE_W;
								System.out.println("dumpToFile: "
										+ BaseEvent.EVENT_TYPE_STR[evtype] + ", "
										+ planet + " = " + eventCount + "; "
										+ "total events=" + info.mTotalCount
										+ " flags=" + info.mFlags);
								if (!Mutter.writeToTempFile(tempPath, info, mEvents,
										eventCount)) {
									info.mFlags &= ~EF_CUMUL_DATE_W;
									System.out.println("dumpToFile: "
											+ BaseEvent.EVENT_TYPE_STR[evtype] + ", "
											+ planet + " = " + eventCount + "; "
											+ "total events=" + info.mTotalCount
											+ " flags=" + info.mFlags);
									Mutter.writeToTempFile(tempPath, info, mEvents,
											eventCount);
								}
							}
						}
					}
				}
				long dataLen = joinTempfiles(tempPath, raf);
				long posEnd = raf.getFilePointer();
				raf.seek(headerPos);
				raf.writeShort((int) dataLen);
				headerPos = raf.getFilePointer();
				raf.seek(posEnd);
				System.out.println(inputFile);
				++fileCounter;
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	private long joinTempfiles(String tempPath, RandomAccessFile raf) {
		int diffDays = (int) ((mEndTime - mStartTime) / MSECINDAY + 0.5);
		long dataLen = 0;
		try {
			long dataPos = raf.getFilePointer();
			raf.writeBytes("S&WA");
			raf.writeByte(2); // version
			raf.writeShort(mYear);
			raf.writeByte(mStartMonth + 1);
			raf.writeByte(1); // day of month
			raf.writeShort(diffDays);
			raf.writeInt(mLocationsDataFile.mCityId);
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
		return dataLen;
	}
}
