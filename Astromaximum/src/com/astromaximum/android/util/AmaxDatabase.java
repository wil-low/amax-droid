package com.astromaximum.android.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.astromaximum.util.CommonDataFile;
import com.astromaximum.util.LocationsDataFile;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class AmaxDatabase extends SQLiteAssetHelper {
	private static final String DATABASE_NAME = "amax";
	private static final int DATABASE_VERSION = 1;
	private static AmaxDatabase mInstance;
	private SQLiteDatabase mDB;

	public static AmaxDatabase getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new AmaxDatabase(context);
		}
		return mInstance;
	}

	private AmaxDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// 1st is period id, 2nd - key
	String[] getCommonIds(long id) {
		mDB = getReadableDatabase();
		Cursor cursor = mDB.rawQuery(
				"select year, start_month, month_count, key from commons where _id = "
						+ id, null);
		String[] result = new String[2];
		if (cursor.moveToFirst()) {
			result[0] = String.format("%04d%02d%02d", cursor.getInt(0),
					cursor.getInt(1), cursor.getInt(2));
			result[1] = cursor.getString(3);
		}
		cursor.close();
		mDB.close();
		return result;
	}

	public Cursor getCurrentPeriodAndCity(long periodId, String locationId) {
		mDB = getReadableDatabase();
		String q = "select commons._id as _id, year, start_month, month_count, commons.key, name from commons, cities where commons._id = "
				+ periodId + " and cities.key = '" + locationId + "'";
		Cursor cursor = mDB.rawQuery(q, null);
		return cursor;
	}

	public Cursor getAvailablePeriods(long exceptId) {
		mDB = getReadableDatabase();
		Cursor cursor = mDB
				.rawQuery(
						"select _id, year, start_month, month_count, key from commons where _id <> "
								+ exceptId
								+ " order by year, start_month, month_count",
						null);
		return cursor;
	}

	public Cursor getCurrentLocation(String locationKey) {
		mDB = getReadableDatabase();
		Cursor cursor = mDB.rawQuery(
				"select _id, name, state, country from cities where key = '"
						+ locationKey + "'", null);
		return cursor;
	}

	public Cursor getSortedLocations() {
		mDB = getReadableDatabase();
		Cursor cursor = mDB
				.rawQuery(
						"select _id, name, state, country, key from cities order by name, state, country",
						null);
		return cursor;
	}

	public Cursor getCitiesForPeriod(long commonId) {
		mDB = getReadableDatabase();
		Cursor cursor = mDB.rawQuery(
				"select lo._id as _id, ci.name, ci.state, ci.country, ci.key "
						+ "from cities ci, locations lo, commons co "
						+ "where ci._id = lo.city_id and co._id = " + commonId
						+ " order by ci.name, ci.state, ci.country", null);
		return cursor;
	}

	public long addCity(LocationsDataFile ldf, String locationKey) {
		mDB = getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("name", ldf.mCity);
		cv.put("state", ldf.mState);
		cv.put("country", ldf.mCountry);
		cv.put("key", locationKey);
		try {
			return mDB.insertOrThrow("cities", null, cv);
		} catch (SQLException e) {
			Cursor cursor = getCurrentLocation(locationKey);
			if (cursor.moveToFirst())
				return cursor.getLong(0);
		}
		return -1;
	}

	public void addLocation(long periodId, long cityId) {
		mDB = getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("common_id", periodId);
		cv.put("city_id", cityId);
		try {
			mDB.insertOrThrow("locations", null, cv);
		} catch (SQLException e) {
		}
	}

	public long addPeriod(CommonDataFile cdf, String key) {
		mDB = getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("year", cdf.mStartYear);
		cv.put("start_month", cdf.mStartMonth);
		cv.put("month_count", cdf.mMonthCount);
		cv.put("key", key);
		try {
			return mDB.insertOrThrow("commons", null, cv);
		} catch (SQLException e) {
		}
		return getPeriodByKey(key);
	}

	public long getPeriodByKey(String periodKey) {
		mDB = getReadableDatabase();
		String q = "select _id from commons where key = '" + periodKey + "'";
		Cursor cursor = mDB.rawQuery(q, null);
		if (cursor.moveToFirst())
			return cursor.getLong(0);
		return -1;
	}
}
