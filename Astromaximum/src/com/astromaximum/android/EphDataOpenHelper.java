package com.astromaximum.android;

import com.astromaximum.util.CommonDataFile;
import com.astromaximum.util.DataFile;
import com.astromaximum.util.DataFileEventConsumer;
import com.astromaximum.util.Event;
import com.astromaximum.util.Location;
import com.astromaximum.util.LocationsDataFile;

import java.io.IOException;
import java.io.InputStream;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class EphDataOpenHelper extends SQLiteOpenHelper implements DataFileEventConsumer {
	private static EphDataOpenHelper mInstance = null;
    private static final String DATABASE_NAME = "ephdata.db";
    private static final int DATABASE_VERSION = 1;
    
    private static final String COMMON_EVENTS_TABLE_NAME = "common_events";
    private static final String LOCATIONS_TABLE_NAME = "locations";
    private static final String TIMEZONES_TABLE_NAME = "timezones";
    private static final String LOCATION_EVENTS_TABLE_NAME = "location_events";
    
    static final String KEY_ID = "_id";
    static final String KEY_YEAR = "year";
    static final String KEY_EVENT_TYPE = "evtype";
    static final String KEY_DATE0 = "date0";
    static final String KEY_DATE1 = "date1";
    static final String KEY_DEGREE = "degree";
    static final String KEY_PLANET0 = "planet0";
    static final String KEY_PLANET1 = "planet1";
    static final String KEY_NAME = "name";
    static final String KEY_LOCATION_ID = "location_id";
    static final String KEY_TIMEZONE_ID = "timezone_id";
    static final String KEY_TZ_OFFSET = "tz_offset";
    static final String KEY_IS_SOUTHERN = "is_southern";
    static final String KEY_DST_START = "dst_start";
    static final String KEY_DST_END = "dst_end";
    
    private static final String COMMON_EVENTS_TABLE_CREATE =
    	"CREATE TABLE " + COMMON_EVENTS_TABLE_NAME + "(" +
    		KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
    		KEY_YEAR + " INTEGER, " +
    		KEY_EVENT_TYPE + " INTEGER, " +
        	KEY_DATE0 + " INTEGER, " +
        	KEY_DATE1 + " INTEGER, " +
        	KEY_DEGREE + " INTEGER, " +
        	KEY_PLANET0 + " INTEGER, " +
        	KEY_PLANET1 + " INTEGER)";

    private static final String LOCATIONS_TABLE_CREATE =
    	"CREATE TABLE " + LOCATIONS_TABLE_NAME + "(" +
    		KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
    		KEY_NAME + " STRING)";
    
    private static final String TIMEZONES_TABLE_CREATE =
    	"CREATE TABLE " + TIMEZONES_TABLE_NAME + "(" +
    		KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
    		KEY_YEAR + " INTEGER, " +
    		KEY_LOCATION_ID + " INTEGER, " +
    		KEY_TZ_OFFSET + " INTEGER, " +
    		KEY_IS_SOUTHERN + " INTEGER, " +
    		KEY_DST_START + " INTEGER, " +
    		KEY_DST_END + " INTEGER)";

    private static final String LOCATION_EVENTS_TABLE_CREATE =
    	"CREATE TABLE " + LOCATION_EVENTS_TABLE_NAME + "(" +
    		KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
    		KEY_YEAR + " INTEGER, " +
    		KEY_TIMEZONE_ID + " INTEGER, " +
    		KEY_EVENT_TYPE + " INTEGER, " +
        	KEY_DATE0 + " INTEGER, " +
        	KEY_DATE1 + " INTEGER, " +
        	KEY_DEGREE + " INTEGER, " +
        	KEY_PLANET0 + " INTEGER, " +
        	KEY_PLANET1 + " INTEGER)";

    private static final String TABLE_RECORD_COUNT_QUERY =
    	"SELECT COUNT(*) FROM ";
    
	private static final String SELECT_TIMEZONE_QUERY =
		"SELECT T." + KEY_ID + ", " + KEY_YEAR + ", " +
		KEY_LOCATION_ID + ", " + KEY_TZ_OFFSET + ", " + 
		KEY_IS_SOUTHERN + ", " + KEY_DST_START + ", " +
		KEY_DST_END + ", " + KEY_NAME + " FROM " +
		TIMEZONES_TABLE_NAME + " T, " + LOCATIONS_TABLE_NAME + " L " +
		" WHERE " + KEY_YEAR + "=? AND " + KEY_LOCATION_ID + "=? AND L." + KEY_ID + "=" + KEY_LOCATION_ID;
   
    private SQLiteDatabase mDB = null;
    private String TAG = "EphDataOpenHelper";
    EphDataOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(COMMON_EVENTS_TABLE_CREATE);
		Log.d(TAG, COMMON_EVENTS_TABLE_CREATE);
		db.execSQL(LOCATIONS_TABLE_CREATE);
		Log.d(TAG, LOCATIONS_TABLE_CREATE);
		db.execSQL(TIMEZONES_TABLE_CREATE);
		Log.d(TAG, TIMEZONES_TABLE_CREATE);
		db.execSQL(LOCATION_EVENTS_TABLE_CREATE);
		Log.d(TAG, LOCATION_EVENTS_TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (newVersion == 1) {
			db.execSQL("DROP TABLE " + COMMON_EVENTS_TABLE_NAME);
			db.execSQL(COMMON_EVENTS_TABLE_CREATE);
			int recordsDeleted = db.delete(COMMON_EVENTS_TABLE_NAME, "1", null);
			Log.i(TAG, "Deleted " + Integer.toString(recordsDeleted) + " records from " + COMMON_EVENTS_TABLE_NAME);
			Log.i(TAG, "onUpgrade");
		}
	}
	
	public boolean isEmpty(boolean isCommon) {
		Cursor cursor = getWritableDatabase().rawQuery(TABLE_RECORD_COUNT_QUERY + 
			(isCommon ? COMMON_EVENTS_TABLE_NAME : LOCATION_EVENTS_TABLE_NAME), null);
		cursor.moveToFirst();
		int rowCount = cursor.getInt(0);
		cursor.close();
		Log.i(TAG, "rowCount: " + rowCount);
		return rowCount == 0;
	}
	
	void convertDataFile(InputStream stream, boolean isCommon) throws IOException {
		Log.i(TAG, "converting DB");
		mDB = getWritableDatabase();
		DataFile datafile;
		if (isCommon)
			datafile = new CommonDataFile(stream);
		else
			datafile = new LocationsDataFile(stream);
		mDB.beginTransaction();
		try {
//			int recordsDeleted = mDB.delete(COMMON_TABLE_NAME, "1", null);
//			Log.i(TAG, "Deleted " + Integer.toString(recordsDeleted) + " records from " + COMMON_TABLE_NAME);
			int eventCount = datafile.readSubData(this);
			Log.i(TAG, "Added " + eventCount + " records");
			mDB.setTransactionSuccessful();
		}
		catch (Exception ex) {
			Log.e(TAG, ex.getMessage());
		}
		finally {
			mDB.endTransaction();
		}
		mDB.close();
		mDB = null;
		isEmpty (isCommon);
		Log.i(TAG, "converting DB done");
	}

	public void addLocation(Location location) {
		// check if location exists
		Cursor cursor = mDB.query(LOCATIONS_TABLE_NAME,
				new String[] {KEY_ID, KEY_NAME},
				KEY_NAME + "=?",
				new String[] {location.mName},
				null, null, null);
		if (cursor.moveToFirst()) { // record exists
			location.mLocationId = cursor.getInt(0);
		}
		else {
			ContentValues values = new ContentValues();
			values.put(KEY_NAME, location.mName);
			location.mLocationId = mDB.insertOrThrow(LOCATIONS_TABLE_NAME, null, values);
			
			// delete all records having this locationId
			mDB.delete(TIMEZONES_TABLE_NAME, KEY_LOCATION_ID + "=?",
					new String[] {Long.toString(location.mLocationId)});
		}
		cursor.close();

		// insert timezone record
		ContentValues values = new ContentValues();
		values.put(KEY_LOCATION_ID, location.mLocationId);
		values.put(KEY_YEAR, location.mYear);
		values.put(KEY_TZ_OFFSET, location.mTzOffset);
		values.put(KEY_IS_SOUTHERN, location.mIsSouthern);
		values.put(KEY_DST_START, location.mDstStart);
		values.put(KEY_DST_END, location.mDstEnd);
		location.mTimeZoneId = mDB.insertOrThrow(TIMEZONES_TABLE_NAME, null, values);
	}

	public void addEvent(int year, Event event) {
		ContentValues values = new ContentValues();
		values.put(KEY_YEAR, year);
		values.put(KEY_EVENT_TYPE, event.getEvtype());
		values.put(KEY_DATE0, event.getDate0());
		values.put(KEY_DATE1, event.getDate1());
		values.put(KEY_DEGREE, event.getFullDegree());
		values.put(KEY_PLANET0, event.getPlanet0());
		values.put(KEY_PLANET1, event.getPlanet1());
		mDB.insertOrThrow(COMMON_EVENTS_TABLE_NAME, null, values);
	}

	public void addEvent(int year, Event event, long timeZoneId) {
		ContentValues values = new ContentValues();
		values.put(KEY_YEAR, year);
		values.put(KEY_TIMEZONE_ID, timeZoneId);
		values.put(KEY_EVENT_TYPE, event.getEvtype());
		values.put(KEY_DATE0, event.getDate0());
		values.put(KEY_DATE1, event.getDate1());
		values.put(KEY_DEGREE, event.getFullDegree());
		values.put(KEY_PLANET0, event.getPlanet0());
		values.put(KEY_PLANET1, event.getPlanet1());
		mDB.insertOrThrow(LOCATION_EVENTS_TABLE_NAME, null, values);
	}

//======================
/*
 * 	SELECT KEY_ID, KEY_EVENT_TYPE 
		KEY_DATE0, KEY_DATE1, KEY_PLANET0, 
		KEY_PLANET1, KEY_DEGREE FROM 
		COMMON_EVENTS WHERE KEY_DATE0 + " BETWEEN ? and ? 
		UNION ALL 
		SELECT KEY_ID KEY_EVENT_TYPE 
		KEY_DATE0 KEY_DATE1 KEY_PLANET0 
		KEY_PLANET1, KEY_DEGREE FROM 
		LOCATION_EVENTS WHERE KEY_DATE0 BETWEEN ? and ? " +
		AND KEY_TIMEZONE_ID=? ORDER BY KEY_DATE0;

 */
	private static final String SELECT_EVENTS_QUERY =
		"SELECT " + KEY_ID + ", " + KEY_EVENT_TYPE + ", " + 
		KEY_DATE0 + ", " + KEY_DATE1 + ", " + KEY_PLANET0 + ", " + 
		KEY_PLANET1 +  ", " + KEY_DEGREE + " FROM " + 
		COMMON_EVENTS_TABLE_NAME + " WHERE " + KEY_DATE0 + " BETWEEN ? and ? " + 
		"UNION ALL " + 
		"SELECT " + KEY_ID + ", " + KEY_EVENT_TYPE + ", " + 
		KEY_DATE0 + ", " + KEY_DATE1 + ", " + KEY_PLANET0 + ", " + 
		KEY_PLANET1 +  ", " + KEY_DEGREE + " FROM " + 
		LOCATION_EVENTS_TABLE_NAME + " WHERE " + KEY_DATE0 + " BETWEEN ? and ? " +
		"AND " + KEY_TIMEZONE_ID + "=? " +
		"ORDER BY " + KEY_DATE0;

	public static Event createEventFromCursor(Cursor cursor) {
		Event event = new Event(
				cursor.getInt(1),  // KEY_EVENT_TYPE
				cursor.getLong(2), // KEY_DATE0
				cursor.getLong(3), // KEY_DATE1
				cursor.getInt(4),  // KEY_PLANET0
				cursor.getInt(5),  // KEY_PLANET1
				cursor.getInt(6)   // KEY_DEGREE
				);
		return event;
	}

	public Cursor getEventsOnPeriod(long startPeriod, long endPeriod, long timeZoneId, int rangeType) {
		// two pairs of args because of union 
		String[] args = {
				Long.toString(startPeriod), 
				Long.toString(endPeriod),
				Long.toString(startPeriod), 
				Long.toString(endPeriod),
				Long.toString(timeZoneId)
				};
		Log.d(TAG, "getEvents: " + args[0] + " - " + args[1]);
		return getReadableDatabase().rawQuery(SELECT_EVENTS_QUERY, args);
	}
//======================
	
	public Cursor getLocations() {
		Log.d(TAG, "getLocations: ");
		return getReadableDatabase().query(LOCATIONS_TABLE_NAME, 
			new String[] {KEY_ID, KEY_NAME}, null, null, null, null, KEY_NAME);
	}

	public Location getLocation(int year, long locationId) {
		Log.d(TAG, "Location: " + year + " - " + locationId);
		String[] args = {Integer.toString(year), Long.toString(locationId)};
		Cursor cursor = getReadableDatabase().rawQuery(SELECT_TIMEZONE_QUERY, args);
		Location location = null;
		if (cursor.moveToFirst()) {
			location = new Location();
			location.mTimeZoneId = cursor.getInt(0);
			location.mYear = cursor.getShort(1);
			location.mLocationId = cursor.getInt(2);
			location.mTzOffset = cursor.getInt(3);
			location.mIsSouthern = cursor.getInt(4) != 0;
			location.mDstStart = cursor.getInt(5);
			location.mDstEnd = cursor.getInt(6);
			location.mName = cursor.getString(7);
		}
		cursor.close();
		return location;
	}
	
	public static EphDataOpenHelper getInstance(Context context) {
		if (mInstance == null && context != null)
			mInstance = new EphDataOpenHelper(context);
		return mInstance;
	}

	public static EphDataOpenHelper getInstance() {
		return mInstance;
	}
}
