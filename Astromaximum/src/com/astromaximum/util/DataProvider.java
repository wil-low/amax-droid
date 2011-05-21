package com.astromaximum.util;

import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import com.astromaximum.android.EphDataOpenHelper;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

public class DataProvider {
	private static DataProvider mInstance;
	private final String STATE_KEY_YEAR = "Year";
	private final String STATE_KEY_MONTH = "Month";
	private final String STATE_KEY_DAY = "Day";
	private final String TAG = "EventProvider";

	private int mYear;
	private int mMonth;
	private int mDay;
	
    private Location mCurrentLocation = null;
    private Cursor mEventCursor;
	
	private Vector<Event> events;
	private Location location;
	
	private DataProvider() {
		// TODO Auto-generated constructor stub
	}

	public static DataProvider getInstance() {
		if (mInstance == null)
			mInstance = new DataProvider();
		return mInstance;
	}

	public int getYear() {
		return mYear;
	}

	public int getMonth() {
		return mMonth;
	}

	public int getDay() {
		return mDay;
	}

	public void saveState (Bundle outState) {
    	Log.d(TAG, "saveInstanceState");
    	outState.putInt(STATE_KEY_YEAR, mYear);
    	outState.putInt(STATE_KEY_MONTH, mMonth);
    	outState.putInt(STATE_KEY_DAY, mDay);
    }
    
    public void restoreState (Bundle savedState) {
    	Log.d(TAG, "restoreInstanceState");
    	Calendar c = DataFile.getUtcCalendar();
    	mYear = savedState.getInt(STATE_KEY_YEAR, c.get(Calendar.YEAR));
    	mMonth = savedState.getInt(STATE_KEY_MONTH, c.get(Calendar.MONTH));
    	mDay = savedState.getInt(STATE_KEY_DAY, c.get(Calendar.DAY_OF_MONTH));
    }

    public void setDate (int year, int month, int day) {
		mYear = year;
		mMonth = month;
		mDay = day;
	}

    public void gatherDayEvents (Date date) {
		
	}
}
