package com.astromaximum.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Calendar;

public class LocationFilter extends SubDataProcessor {
	final BaseEvent[] mEvents = new BaseEvent[3000];
	LocationsDataFile mLocationsDataFile;
	private int mYear;
	private Calendar mCalendar = Calendar.getInstance();
	private long mStartTime, mEndTime, mStartJD, mFinalJD;
	private int mStartMonth;

	static final int[] EVENT_TYPES = { BaseEvent.EV_RISE, BaseEvent.EV_SET };

	LocationFilter(int year, String inputFile) {
		mYear = year;
		System.out.println(inputFile);
		try {
			FileInputStream fis = new FileInputStream(inputFile);
			mLocationsDataFile = new LocationsDataFile(fis);
			mCalendar.set(mLocationsDataFile.mStartYear,
					mLocationsDataFile.mStartMonth,
					mLocationsDataFile.mStartDay, 0, 0, 0);
			mStartJD = mCalendar.getTime().getTime();
			mFinalJD = mStartJD + mLocationsDataFile.mDayCount * MSECINDAY;
			System.out.println("Location " + mLocationsDataFile.mStartYear
					+ " " + mLocationsDataFile.mDayCount);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void addEvent(int idx, BaseEvent event) {
		mEvents[idx] = new BaseEvent(event);
	}

	public void dumpToFile(int startMonth, int monthCount, String outFile) {
		mStartMonth = startMonth;

		mCalendar.set(mYear, mStartMonth, 1, 0, 0, 0);
		mStartTime = mCalendar.getTimeInMillis();

		mCalendar.add(Calendar.MONTH, monthCount);
		mCalendar.add(Calendar.DAY_OF_MONTH, -1);
		mEndTime = mCalendar.getTimeInMillis();

		try {
			FileOutputStream fos = new FileOutputStream(outFile);
			SubDataInfo info = new SubDataInfo();

			String tempPath = "/tmp/common_" + mYear;
			File path = new File(tempPath);
			path.mkdir();
			File[] tempFiles = path.listFiles();
			for (File tempFile : tempFiles) {
				tempFile.delete();
			}

			for (int evtype : EVENT_TYPES) {
				for (int planet = -1; planet <= BaseEvent.SE_PLUTO; ++planet) {
					int eventCount = read(mLocationsDataFile.mData, evtype,
							planet, false, mStartTime, mEndTime, mFinalJD, info);
					if (eventCount > 0) {
						info.mFlags &= ~(EF_CUMUL_DATE_B | EF_CUMUL_DATE_W);
						info.mFlags |= EF_CUMUL_DATE_B;

						System.out.println("dumpToFile: "
								+ BaseEvent.EVENT_TYPE_STR[evtype] + ", "
								+ planet + " = " + eventCount + "; "
								+ "total events=" + info.mTotalCount
								+ " flags=" + info.mFlags);
						if (!writeToTempFile(tempPath, info, mEvents,
								eventCount)) {
							info.mFlags &= ~EF_CUMUL_DATE_B;
							info.mFlags |= EF_CUMUL_DATE_W;
							System.out.println("dumpToFile: "
									+ BaseEvent.EVENT_TYPE_STR[evtype] + ", "
									+ planet + " = " + eventCount + "; "
									+ "total events=" + info.mTotalCount
									+ " flags=" + info.mFlags);
							if (!writeToTempFile(tempPath, info, mEvents,
									eventCount)) {
								info.mFlags &= ~EF_CUMUL_DATE_W;
								System.out.println("dumpToFile: "
										+ BaseEvent.EVENT_TYPE_STR[evtype] + ", "
										+ planet + " = " + eventCount + "; "
										+ "total events=" + info.mTotalCount
										+ " flags=" + info.mFlags);
								writeToTempFile(tempPath, info, mEvents,
										eventCount);
							}
						}
					}
				}
			}
			joinDatafiles(tempPath, outFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void joinDatafiles(String tempPath, String outFile) {
		int diffDays = (int) ((mEndTime - mStartTime) / (24 * 60 * 60 * 1000));
		try {
			RandomAccessFile raf = new RandomAccessFile(outFile, "rw");
			raf.writeShort(0);  // fake year
			raf.writeShort(0);  // fake record count
			raf.writeShort(0);  // fake location data
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
			long dataLen = raf.getFilePointer() - dataPos;
			raf.seek(0);
			raf.writeShort(mYear);
			raf.writeShort(1);
			raf.writeShort((int) dataLen);
			raf.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
