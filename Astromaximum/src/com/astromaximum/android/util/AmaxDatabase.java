package com.astromaximum.android.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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

	public void closeDatabase() {
		mDB.close();
	}
	
	// 1st is period id, 2nd - key
	String[] getCommonIds(int id) {
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

	public Cursor getPeriods(int exceptId) {
		mDB = getReadableDatabase();
		Cursor cursor = mDB
				.rawQuery(
						"select _id, year, start_month, month_count from commons where _id = "
								+ exceptId
								+ " order by year, start_month, month_count",
						null);
		return cursor;
	}
}
