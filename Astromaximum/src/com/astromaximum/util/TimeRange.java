package com.astromaximum.util;

import java.util.Calendar;

public class TimeRange {
	private long mStartTimeMillis;
	private long mEndTimeMillis;
	
	private TimeRange(long startTime, long endTime) {
		mStartTimeMillis = startTime;
		mEndTimeMillis = endTime;
	}
	
	static TimeRange getDayRange(int year, int month, int day) {
     	DataFile.calendar.set(year, month, day, 0, 0, 0);
     	DataFile.calendar.set(Calendar.MILLISECOND, 0);
		long startTime = DataFile.calendar.getTimeInMillis();
	    long endTime = startTime + + DataFile.MSECINDAY;
		return new TimeRange(startTime, endTime);
	}
	
	static TimeRange getWeekRange(int year, int month, int day) {
		return new TimeRange(0, 0);
	}

	static TimeRange getMonthRange(int year, int month, int day) {
		return new TimeRange(0, 0);
	}
	
	public long getStartTimeMillis() {
		return mStartTimeMillis;
	}
	public long getEndTimeMillis() {
		return mEndTimeMillis;
	}
}
