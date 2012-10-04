package com.astromaximum.android;

import java.util.Map;
import java.util.TreeMap;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public final class PreferenceUtils {
	public static final int ID_PREFERENCE = 1;
	public static final String KEY_LOCATION_ID = "current_location_id";
	public static final String PREF_LOCATION_LIST = "location_list";
	public static final String KEY_START_TIME = "start_time";

	public static String getLocationId(Context context) {
		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sharedPref.getString(PreferenceUtils.KEY_LOCATION_ID, "");
	}

	public static TreeMap<String, String> getSortedLocations(Context context) {
		SharedPreferences sharedPref = context.getSharedPreferences(
				PREF_LOCATION_LIST, 0);

		TreeMap<String, String> treeMap = new TreeMap<String, String>();
		for (Map.Entry<String, ?> entry : sharedPref.getAll().entrySet())
			treeMap.put((String) entry.getValue(), entry.getKey());
		return treeMap;
	}
}
