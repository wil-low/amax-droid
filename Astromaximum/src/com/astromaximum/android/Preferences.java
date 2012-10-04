package com.astromaximum.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

public class Preferences extends PreferenceActivity {
	private final String TAG = "Preferences";
	private ListPreference mLocationsPreference;
	public static final String STATE_KEY_LOC_PREFIX = "LF_";
	public static final int MAX_LOCATION_COUNT = 10;
	public static final String KEY_LOCATION_ID = "locations";
	public static final int ID_PREFERENCE = 0;
	private String[] mLocationFiles = new String[MAX_LOCATION_COUNT];

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);

		mLocationsPreference = (ListPreference) findPreference("locations");
		populateCitiesList();

		mLocationsPreference
				.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						setCurrentCity((ListPreference) preference,
								(String) newValue);
						return true;
					}
				});

	}

	private void populateCitiesList() {
		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(this);
		String[] locationArray = new String[MAX_LOCATION_COUNT];
		String[] entryValues = new String[MAX_LOCATION_COUNT];
		for (int index = 0; index < MAX_LOCATION_COUNT; ++index) {
			String file = sharedPref.getString(STATE_KEY_LOC_PREFIX + index, "");
			locationArray[index] = file;
			entryValues[index] = Integer.toString(index);
		}
		mLocationsPreference.setEntries(locationArray);
		mLocationsPreference.setEntryValues(entryValues);
	}

	private void setCurrentCity(ListPreference preference, String value) {
		if (value != null) {
			preference.setSummary(preference.getEntries()[preference
					.findIndexOfValue(value)]);
			SharedPreferences.Editor editor = PreferenceManager
					.getDefaultSharedPreferences(this).edit();
			editor.putInt(KEY_LOCATION_ID, Integer.valueOf(value).intValue());
			editor.commit();
			Log.d(TAG, "Saved " + value);
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "OnResume");
		String locationId = Integer.toString(getLocationId(this));
		try {
			setCurrentCity(mLocationsPreference, locationId);
			mLocationsPreference.setValue(locationId);
		} catch (ArrayIndexOutOfBoundsException ex) {
			Log.d(TAG, "locationId " + locationId + " is out of bounds");
		}
	}

	public static int getLocationId(Context context) {
	    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
	    return sharedPref.getInt(KEY_LOCATION_ID, 0);
	}
}
