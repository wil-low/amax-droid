package com.astromaximum.android;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.astromaximum.android.util.StartPageItem;

public final class PreferenceUtils {
	public static final int ID_PREFERENCE = 1;
	private static final String KEY_COMMON_ID = "common_id";
	public static final String KEY_LOCATION_ID = "location_id";
	public static final String KEY_START_TIME = "start_time";
	public static final String KEY_CUSTOM_HOUR = "custom_hour";
	public static final String KEY_CUSTOM_MINUTE = "custom_minute";
	public static final String LISTKEY_EVENT_KEY = "com.astromaximum.android.eventKey";
	public static final String LISTKEY_INTERPRETER_TEXT = "com.astromaximum.android.interpreterCode";
	public static final String LISTKEY_INTERPRETER_EVENT = "com.astromaximum.android.event";
	public static final String PERIOD_STRING_KEY = "com.astromaximum.android.periodString";
	public static final String PERIOD_KEY_KEY = "com.astromaximum.android.periodKey";
	public static final String KEY_USE_CUSTOM_TIME = "use_custom_time";
	public static final String KEY_CUSTOM_TIME = "custom_time";
	public static final String KEY_START_PAGE_LAYOUT = "start_page_layout";
	public static final String KEY_USE_VOLUME_BUTTONS = "use_volume_buttons";
	public static final String KEY_DOWNLOAD_MORE = "download_more";
	static final String KEY_STARTPAGE_ITEM_INDEX = "startpage_item_index";
	static final String KEY_STARTPAGE_ITEM_ENABLED = "startpage_item_enabled";
	private static final String TAG = "PreferenceUtils";

	public static long getCommonId(Context context) {
		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sharedPref.getLong(PreferenceUtils.KEY_COMMON_ID, 0);
	}

	public static void setCommonId(Context context, long id) {
		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putLong(PreferenceUtils.KEY_COMMON_ID, id);
		editor.commit();
	}

	public static String getLocationId(Context context) {
		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sharedPref.getString(PreferenceUtils.KEY_LOCATION_ID, null);
	}

	public static void setLocationId(Context context, String id) {
		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString(PreferenceUtils.KEY_LOCATION_ID, id);
		editor.commit();
	}

	public static boolean getUseCustomTime(Context context) {
		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sharedPref
				.getBoolean(PreferenceUtils.KEY_USE_CUSTOM_TIME, false);
	}

	public static boolean getUseVolumeButtons(Context context) {
		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sharedPref
				.getBoolean(PreferenceUtils.KEY_USE_VOLUME_BUTTONS, false);
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
