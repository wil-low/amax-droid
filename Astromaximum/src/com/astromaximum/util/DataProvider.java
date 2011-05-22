package com.astromaximum.util;

import java.util.Calendar;
import java.util.Enumeration;
import java.util.Vector;

import com.astromaximum.android.EphDataOpenHelper;
import com.astromaximum.android.PreferenceUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.util.Log;

public class DataProvider {
	private static DataProvider mInstance;
	private final String STATE_KEY_YEAR = "Year";
	private final String STATE_KEY_MONTH = "Month";
	private final String STATE_KEY_DAY = "Day";
	private final String TAG = "EventProvider";

	public static final int RANGE_DAY = 0;
	public static final int RANGE_WEEK = 1;
	public static final int RANGE_MONTH = 2;
	public static final int RANGE_LAST = 2;

	private int mYear;
	private int mMonth;
	private int mDay;
	
    private Location mCurrentLocation = null;
	
	private Vector<Vector<Event>> mEventCache;
	private EphDataOpenHelper mDbHelper;
	private Context mContext;
	
	private DataProvider(Context context) {
		mContext = context;
		mDbHelper = EphDataOpenHelper.getInstance(mContext);
		mEventCache = new Vector<Vector<Event>>();
		for (int i = 0; i < RANGE_LAST; ++i)
			mEventCache.add(new Vector<Event>());
	}

	public static DataProvider getInstance(Context context) {
		if (mInstance == null)
			mInstance = new DataProvider(context);
		return mInstance;
	}

	public static DataProvider getInstance() {
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

	public void saveState() {
    	Log.d(TAG, "saveInstanceState");
	    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
	    editor.putInt(STATE_KEY_YEAR, mYear);
	    editor.putInt(STATE_KEY_MONTH, mMonth);
	    editor.putInt(STATE_KEY_DAY, mDay);
    }
    
    public void restoreState() {
    	Log.d(TAG, "restoreInstanceState");
    	Calendar c = DataFile.getUtcCalendar();
	    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
    	mYear = sharedPref.getInt(STATE_KEY_YEAR, c.get(Calendar.YEAR));
    	mMonth = sharedPref.getInt(STATE_KEY_MONTH, c.get(Calendar.MONTH));
    	mDay = sharedPref.getInt(STATE_KEY_DAY, c.get(Calendar.DAY_OF_MONTH));
    	final long locationId = sharedPref.getLong(PreferenceUtils.KEY_LOCATION_ID, 0);
    	mCurrentLocation = mDbHelper.getLocation(mYear, locationId);
		Log.d(TAG, "Received locationId " + locationId + ": " + mYear);
    }

    public void setDate (int year, int month, int day) {
		mYear = year;
		mMonth = month;
		mDay = day;
	}

	public void gatherEvents(int rangeType) {
		String tag = null;
		TimeRange timeRange = null;
		switch (rangeType) {
		case RANGE_DAY:
			tag = "day";
			timeRange = TimeRange.getDayRange(mYear, mMonth, mDay);
			break;
		}
    	readEvents(timeRange, mEventCache.get(rangeType), rangeType, tag);
	}

	private void readEvents(TimeRange timeRange, Vector<Event> events, int rangeType, String tag) {
 		events.clear();
     	if (mCurrentLocation != null) { 
	    	Cursor cursor = mDbHelper.getEventsOnPeriod(
	    			timeRange.getStartTimeMillis(),
	    			timeRange.getEndTimeMillis(),
	    			mCurrentLocation.mTimeZoneId, rangeType);
	    	if (!cursor.moveToFirst()) {
	    		Log.e(TAG, tag + ": No events");
	    	}
	    	else {
	    		Log.d(TAG, "gather " + tag + ": " + mCurrentLocation.mTimeZoneId +
	    				", count " + cursor.getCount());
	    		do {
	    			Event event = EphDataOpenHelper.createEventFromCursor(cursor);
	    			System.out.println(event.toString());
	    			events.add(event);
	    		} while (cursor.moveToNext());
	    	}
	    	cursor.close();
     	}
     	else {
     		Log.e(TAG, tag + ": No current location");
     	}
	}
	
	public void dispatchEvents(int rangeType, EventConsumer consumer) {
	     for (Enumeration<Event> e = mEventCache.get(rangeType).elements(); e.hasMoreElements() ;) {
	    	 Event event = e.nextElement();
	         consumer.addEvent(event);
	     }		
	}
}
