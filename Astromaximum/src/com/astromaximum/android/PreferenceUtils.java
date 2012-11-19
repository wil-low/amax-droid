package com.astromaximum.android;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.astromaximum.util.MyLog;
import com.astromaximum.util.StartPageItem;

public final class PreferenceUtils {
	public static final int ID_PREFERENCE = 1;
	public static final String KEY_LOCATION_ID = "current_location_id";
	public static final String PREF_LOCATION_LIST = "location_list";
	public static final String KEY_START_TIME = "start_time";
	public static final String KEY_CUSTOM_HOUR = "custom_hour";
	public static final String KEY_CUSTOM_MINUTE = "custom_minute";
	public static final String LISTKEY_EVENT_KEY = "com.astromaximum.android.eventKey";
	public static final String LISTKEY_INTERPRETER_TEXT = "com.astromaximum.android.interpreterCode";
	public static final String LISTKEY_INTERPRETER_EVENT = "com.astromaximum.android.event";
	public static final String KEY_USE_CUSTOM_TIME = "use_custom_time";
	public static final String KEY_CUSTOM_TIME = "custom_time";
	public static final String KEY_START_PAGE_LAYOUT = "start_page_layout";
	static final String KEY_STARTPAGE_ITEM_INDEX = "startpage_item_index";
	static final String KEY_STARTPAGE_ITEM_ENABLED = "startpage_item_enabled";
	private static final String TAG = "PreferenceUtils";

	public static String getLocationId(Context context) {
		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sharedPref.getString(PreferenceUtils.KEY_LOCATION_ID, "");
	}

	public static boolean getUseCustomTime(Context context) {
		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sharedPref
				.getBoolean(PreferenceUtils.KEY_USE_CUSTOM_TIME, false);
	}

	public static TreeMap<String, String> getSortedLocations(Context context) {
		SharedPreferences sharedPref = context.getSharedPreferences(
				PREF_LOCATION_LIST, 0);

		TreeMap<String, String> treeMap = new TreeMap<String, String>();
		for (Map.Entry<String, ?> entry : sharedPref.getAll().entrySet())
			treeMap.put((String) entry.getValue(), entry.getKey());
		return treeMap;
	}

	public static ArrayList<StartPageItem> getStartPageLayout(Context context) {
		ArrayList<StartPageItem> result = new ArrayList<StartPageItem>();
		String[] captions = context.getResources().getStringArray(
				R.array.startpage_items);
		for (int i = 0; i < captions.length; ++i)
			result.add(null);

		SharedPreferences settings = PreferenceManager
				.getDefaultSharedPreferences(context);
		for (int i = 0; i < captions.length; ++i) {
			int index = settings.getInt(KEY_STARTPAGE_ITEM_INDEX + i, i);
			boolean isEnabled = settings.getBoolean(KEY_STARTPAGE_ITEM_ENABLED
					+ i, true);
			StartPageItem item = new StartPageItem(captions[i], i, isEnabled);
			result.set(index, item);
		}
		return result;
	}
}
