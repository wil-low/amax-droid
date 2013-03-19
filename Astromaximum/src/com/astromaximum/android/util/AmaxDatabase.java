package com.astromaximum.android.util;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class AmaxDatabase extends SQLiteAssetHelper {
	private static final String DATABASE_NAME = "amax";
	private static final int DATABASE_VERSION = 1;
	private static final String TABLE_COMMONS = "commons";
	private static final String TABLE_CITIES = "cities";
	private static final String TABLE_LOCATIONS = "locations";
	private static AmaxDatabase mInstance;

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
	String[] getCommonIds(int id) {
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.rawQuery("select year, start_month, month_count, key from commons where _id = " + id, null);
		String[] result = new String[2];
		if (cursor.moveToFirst()) {
			result[0] = String.format("%04d%02d%02d", cursor.getInt(0),
					cursor.getInt(1), cursor.getInt(2));
			result[1] = cursor.getString(3);
		}
		cursor.close();
		db.close();
		return result;
	}
}
