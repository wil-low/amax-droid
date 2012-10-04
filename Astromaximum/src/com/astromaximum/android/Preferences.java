package com.astromaximum.android;

import java.util.Map;
import java.util.TreeMap;

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

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);

		mLocationsPreference = (ListPreference) findPreference(PreferenceUtils.KEY_LOCATION_ID);
		populateCitiesList();

		mLocationsPreference
				.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						setListSummary((ListPreference) preference,
								PreferenceUtils.KEY_LOCATION_ID,
								(String) newValue);
						return true;
					}
				});
	}

	private void populateCitiesList() {
		TreeMap<String, String> treeMap = PreferenceUtils
				.getSortedLocations(this);
		int size = treeMap.size();
		String[] locationArray = new String[size];
		String[] entryValues = new String[size];
		int index = 0;
		for (Map.Entry<String, String> entry : treeMap.entrySet()) {
			locationArray[index] = entry.getKey();
			entryValues[index] = entry.getValue();
			++index;
		}
		mLocationsPreference.setEntries(locationArray);
		mLocationsPreference.setEntryValues(entryValues);
	}

	private void setListSummary(ListPreference preference, String key,
			String value) {
		if (value != null) {
			preference.setSummary(preference.getEntries()[preference
					.findIndexOfValue(value)]);
			SharedPreferences.Editor editor = PreferenceManager
					.getDefaultSharedPreferences(this).edit();
			editor.putString(key, value);
			editor.commit();
			Log.d(TAG, "Saved " + value);
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "OnResume");
		String locationId = PreferenceUtils.getLocationId(this);
		try {
			setListSummary(mLocationsPreference,
					PreferenceUtils.KEY_LOCATION_ID, locationId);
			mLocationsPreference.setValue(locationId);
		} catch (ArrayIndexOutOfBoundsException ex) {
			Log.d(TAG, "locationId " + locationId + " is out of bounds");
		}
	}
}
