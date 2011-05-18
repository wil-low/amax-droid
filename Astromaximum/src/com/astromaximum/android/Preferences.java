package com.astromaximum.android;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

public class Preferences extends PreferenceActivity {
	private EphDataOpenHelper mDbHelper;
	private final String TAG = "Preferences";
	private ListPreference mLocationsPreference;

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
		mDbHelper = EphDataOpenHelper.getInstance();
		Cursor cursor = mDbHelper.getLocations();
		int rowCount = cursor.getCount();
		CharSequence[] locationArray = new CharSequence[rowCount];
		CharSequence[] entryValues = new CharSequence[rowCount];
		if (cursor.moveToFirst()) {
			int index = 0;
			do {
				entryValues[index] = cursor.getString(0);
				locationArray[index] = cursor.getString(1);
				++index;
			} while (cursor.moveToNext());
		}
		cursor.close();
		mLocationsPreference.setEntries(locationArray);
		mLocationsPreference.setEntryValues(entryValues);
	}

	private void setCurrentCity(ListPreference preference, String value) {
		if (value != null) {
			preference.setSummary(preference.getEntries()[preference
					.findIndexOfValue(value)]);
			SharedPreferences.Editor editor = PreferenceManager
					.getDefaultSharedPreferences(this).edit();
			editor.putLong(PreferenceUtils.KEY_LOCATION_ID, Long.valueOf(value)
					.longValue());
			editor.commit();
			Log.d(TAG, "Saved " + value);
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "OnResume");
		String locationId = Long.toString(PreferenceUtils.getLocationId(this));
		try {
			setCurrentCity(mLocationsPreference, locationId);
			mLocationsPreference.setValue(locationId);
		}
		catch (ArrayIndexOutOfBoundsException ex) {
			Log.d(TAG, "locationId " + locationId + " is out of bounds");
		}
	}
}
