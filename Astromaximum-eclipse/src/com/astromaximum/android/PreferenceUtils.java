package com.astromaximum.android;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.astromaximum.android.util.StartPageItem;

public final class PreferenceUtils {
	public static final int ID_PREFERENCE = 1;
	private static final String KEY_PERIOD_ID = "period_id";
	public static final String KEY_CITY_KEY = "city_key";
	public static final String KEY_START_TIME = "start_time";
	public static final String KEY_CUSTOM_HOUR = "custom_hour";
	public static final String KEY_CUSTOM_MINUTE = "custom_minute";
	public static final String LISTKEY_EVENT_KEY = "com.astromaximum.android.eventKey";
	public static final String LISTKEY_INTERPRETER_TEXT = "com.astromaximum.android.interpreterCode";
	public static final String LISTKEY_INTERPRETER_EVENT = "com.astromaximum.android.event";
	public static final String PERIOD_STRING_KEY = "com.astromaximum.android.periodString";
	public static final String MODE_KEY = "com.astromaximum.android.mode";
	
	public static final String COUNTRY_ID_KEY = "com.astromaximum.android.countryId";
	public static final String STATE_ID_KEY = "com.astromaximum.android.stateId";
	public static final String CITY_ID_KEY = "com.astromaximum.android.cityId";
	public static final String COUNTRY_NAME_KEY = "com.astromaximum.android.countryName";
	public static final String STATE_NAME_KEY = "com.astromaximum.android.stateName";
	public static final String CITY_NAME_KEY = "com.astromaximum.android.cityName";

	public static final String KEY_USE_CUSTOM_TIME = "use_custom_time";
	public static final String KEY_CUSTOM_TIME = "custom_time";
	public static final String KEY_START_PAGE_LAYOUT = "start_page_layout";
	public static final String KEY_USE_VOLUME_BUTTONS = "use_volume_buttons";
	public static final String KEY_DOWNLOAD_MORE = "download_more";
	static final String KEY_STARTPAGE_ITEM_INDEX = "startpage_item_index";
	static final String KEY_STARTPAGE_ITEM_ENABLED = "startpage_item_enabled";
	private static final String TAG = "PreferenceUtils";

	public static long getPeriodId(Context context) {
		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sharedPref.getLong(PreferenceUtils.KEY_PERIOD_ID, 0);
	}

	public static void setPeriodId(Context context, long periodId) {
		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putLong(PreferenceUtils.KEY_PERIOD_ID, periodId);
		editor.commit();
	}

	public static String getCityKey(Context context) {
		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sharedPref.getString(PreferenceUtils.KEY_CITY_KEY, null);
	}

	public static void setCityKey(Context context, String cityKey) {
		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString(PreferenceUtils.KEY_CITY_KEY, cityKey);
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
