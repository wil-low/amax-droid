package com.astromaximum.android;

import java.util.Map;
import java.util.TreeMap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;
import com.astromaximum.android.util.DataProvider;
import com.astromaximum.android.util.MyLog;

public class Preferences extends SherlockPreferenceActivity {
	private final String TAG = "Preferences";
	private ListPreference mLocationsPreference;
	private CheckBoxPreference mUseCustomTimePreference;
	@SuppressWarnings("unused")
	private DataProvider mDataProvider;
	private TimePreference mCustomTimePreference;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);

		getSupportActionBar().setTitle(R.string.prefs_title);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		mDataProvider = DataProvider.getInstance(this);

		mLocationsPreference = (ListPreference) findPreference(PreferenceUtils.KEY_LOCATION_ID);
		mUseCustomTimePreference = (CheckBoxPreference) findPreference(PreferenceUtils.KEY_USE_CUSTOM_TIME);
		mCustomTimePreference = (TimePreference) findPreference(PreferenceUtils.KEY_CUSTOM_TIME);
		Preference startPageLayout = (Preference) findPreference(PreferenceUtils.KEY_START_PAGE_LAYOUT);
		Preference downloadMore = (Preference) findPreference(PreferenceUtils.KEY_DOWNLOAD_MORE);

		final Context context = this;
		startPageLayout
				.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
					public boolean onPreferenceClick(Preference preference) {
						Intent intent = new Intent(context,
								StartPageLayoutActivity.class);
						startActivity(intent);
						return true;
					}
				});
		downloadMore
				.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
					public boolean onPreferenceClick(Preference preference) {
						Intent intent = new Intent(context,
								LocationListActivity.class);
						startActivity(intent);
						return true;
					}
				});
		
		populateCitiesList();

		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(this);
		mCustomTimePreference.updateTime(
				pref.getInt(PreferenceUtils.KEY_CUSTOM_HOUR, 0),
				pref.getInt(PreferenceUtils.KEY_CUSTOM_MINUTE, 0));

		updateCustomTime(mUseCustomTimePreference.isChecked());

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

		mUseCustomTimePreference
				.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						updateCustomTime(((Boolean) newValue).booleanValue());
						return true;
					}
				});

		mCustomTimePreference
				.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						updateCustomTime(mUseCustomTimePreference.isChecked());
						return true;
					}
				});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		}
		return true;
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
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
		MyLog.d(TAG, "OnResume");
		String locationId = PreferenceUtils.getLocationId(this);
		try {
			setListSummary(mLocationsPreference,
					PreferenceUtils.KEY_LOCATION_ID, locationId);
			mLocationsPreference.setValue(locationId);
		} catch (ArrayIndexOutOfBoundsException ex) {
			MyLog.d(TAG, "locationId " + locationId + " is out of bounds");
		}
	}

	private void updateCustomTime(boolean value) {
		if (value)
			mUseCustomTimePreference
					.setSummary(getString(R.string.use_custom_time) + " "
							+ mCustomTimePreference.getTimeString());
		else
			mUseCustomTimePreference.setSummary(R.string.use_current_time);
		mCustomTimePreference.setEnabled(value);
	}
}
