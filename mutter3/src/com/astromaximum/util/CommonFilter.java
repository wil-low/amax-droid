package com.astromaximum.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

class CommonFilter extends SubDataReader {
	final BaseEvent[] mEvents = new BaseEvent[100];
	CommonDataFile mCommonDataFile;
	
	CommonFilter(int year, String inputFile) {
		FileInputStream fis;
		try {
			fis = new FileInputStream(inputFile);
			mCommonDataFile = new CommonDataFile(fis);
			System.out.println("Common " + mCommonDataFile.mStartYear + " " + mCommonDataFile.mDayCount);
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
		
	}
}
