package com.astromaximum.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Calendar;

class CommonFilter extends SubDataProcessor {
	final BaseEvent[] mEvents = new BaseEvent[3000];
	CommonDataFile mCommonDataFile;
	private int mYear;
	private Calendar mCalendar = Calendar.getInstance();
	private long mStartTime, mEndTime, mStartJD, mFinalJD;
	private int mStartMonth;

	static final int[] EVENT_TYPES = { BaseEvent.EV_SIGN_ENTER,
			BaseEvent.EV_ASP_EXACT, BaseEvent.EV_TITHI,
			BaseEvent.EV_DEGREE_PASS, BaseEvent.EV_RETROGRADE,
			BaseEvent.EV_VOC, BaseEvent.EV_VIA_COMBUSTA, };

	CommonFilter(int year, String inputFile) {
		mYear = year;
		try {
			FileInputStream fis = new FileInputStream(inputFile);
			mCommonDataFile = new CommonDataFile(fis);
			mCalendar.set(mCommonDataFile.mStartYear,
					mCommonDataFile.mStartMonth, mCommonDataFile.mStartDay, 0,
					0, 0);
			mStartJD = mCalendar.getTime().getTime();
			mFinalJD = mStartJD + mCommonDataFile.mDayCount * MSECINDAY;
			System.out.println("Common " + mCommonDataFile.mStartYear + " "
					+ mCommonDataFile.mDayCount);
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

		SubDataInfo info = new SubDataInfo();

		String tempPath = "/tmp/common_" + mYear;
		File path = new File(tempPath);
		path.mkdir();
		File[] tempFiles = path.listFiles();
		for (File tempFile : tempFiles)
			tempFile.delete();
		File out = new File(outFile);
		out.delete();
		
		for (int evtype : EVENT_TYPES) {
			for (int planet = -1; planet <= BaseEvent.SE_PLUTO; ++planet) {
				int eventCount = read(mCommonDataFile.mData, evtype,
						planet, true, mStartTime, mEndTime, mFinalJD, info);
				if (eventCount > 0) {
					System.out.println("dumpToFile: "
							+ BaseEvent.EVENT_TYPE_STR[evtype] + ", "
							+ planet + " = " + eventCount + "; "
							+ "total events=" + info.mTotalCount
							+ " flags=" + info.mFlags);
					info.mFlags &= ~(EF_CUMUL_DATE_B | EF_CUMUL_DATE_W);
					
					info.mFlags |= EF_CUMUL_DATE_B;
					
					if (!writeToTempFile(tempPath, info, mEvents, eventCount)) {
						info.mFlags &= ~EF_CUMUL_DATE_B;
						info.mFlags |= EF_CUMUL_DATE_W;
						if (!writeToTempFile(tempPath, info, mEvents, eventCount)) {
							info.mFlags &= ~EF_CUMUL_DATE_W;
							writeToTempFile(tempPath, info, mEvents, eventCount);
						}
					}
					System.out.println("Written with flags " + info.mFlags + "\n");
				}
			}
		}
		joinDatafiles(tempPath, outFile);

	}

	private void joinDatafiles(String tempPath, String outFile) {
		int diffDays = (int) ((mEndTime - mStartTime) / MSECINDAY);
		try {
			RandomAccessFile raf = new RandomAccessFile(outFile, "rw");
			raf.setLength(0);
			raf.writeShort(mYear);
			raf.writeByte(mStartMonth + 1);
			raf.writeByte(1);  // day of month
			raf.writeShort(0); // custom data length
			raf.writeShort(diffDays);
			File[] tempFiles = new File(tempPath).listFiles();
			
			for (File tempFile : tempFiles) {
				raf.writeByte(0xFE);
				FileInputStream fis = new FileInputStream(tempFile);
				byte[] buffer = new byte[(int) tempFile.length()];
				fis.read(buffer);
				fis.close();
				raf.write(buffer);
			}
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
