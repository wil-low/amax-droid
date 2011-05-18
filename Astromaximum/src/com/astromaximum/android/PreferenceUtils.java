package com.astromaximum.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public final class PreferenceUtils {
	public static final String PREFERENCE_NAME = "AmaxPrefs";
	public static final int ID_PREFERENCE = 1;
	public static final String KEY_LOCATION_ID = "locations";
	
	public static long getLocationId(Context context) {
	    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
	    return sharedPref.getLong(PreferenceUtils.KEY_LOCATION_ID, 0);
	}
}
