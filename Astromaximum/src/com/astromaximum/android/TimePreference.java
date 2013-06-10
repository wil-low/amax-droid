package com.astromaximum.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;

/**
 * http://www.twodee.org/weblog/?p=1037 Custom preference for time selection.
 * Hour and minute are persistent and stored separately as ints in the
 * underlying shared preferences under keys KEY.hour and KEY.minute, where KEY
 * is the preference's key.
 */
public class TimePreference extends DialogPreference {

	/** The widget for picking a time */
	private TimePicker timePicker;

	private int mHour = 0;
	private int mMinute = 0;

	/**
	 * Creates a preference for choosing a time based on its XML declaration.
	 * 
	 * @param context
	 * @param attributes
	 */
	public TimePreference(Context context, AttributeSet attributes) {
		super(context, attributes);
		setPersistent(false);
		setDialogTitle(R.string.pref_custom_time);
	}

	public void updateTime(int hour, int minute) {
		mHour = hour;
		mMinute = minute;
		setTitle(getTimeString());
	}
	
	String getTimeString() {
		return String.format("%02d:%02d", mHour, mMinute);
	}

	/**
	 * Initialize time picker to currently stored time preferences.
	 * 
	 * @param view
	 *            The dialog preference's host view
	 */
	@Override
	public void onBindDialogView(View view) {
		super.onBindDialogView(view);
		timePicker = (TimePicker) view.findViewById(R.id.prefTimePicker);
		timePicker.setIs24HourView(DateFormat.is24HourFormat(timePicker
				.getContext()));
		timePicker.setCurrentHour(mHour);
		timePicker.setCurrentMinute(mMinute);
	}

	/**
	 * Handles closing of dialog. If user intended to save the settings,
	 * selected hour and minute are stored in the preferences with keys KEY.hour
	 * and KEY.minute, where KEY is the preference's KEY.
	 * 
	 * @param okToSave
	 *            True if user wanted to save settings, false otherwise
	 */
	@Override
	protected void onDialogClosed(boolean okToSave) {
		super.onDialogClosed(okToSave);
		if (okToSave) {
			timePicker.clearFocus();
			updateTime(timePicker.getCurrentHour(), timePicker.getCurrentMinute());
			SharedPreferences.Editor editor = getEditor();
			editor.putInt(PreferenceUtils.KEY_CUSTOM_HOUR, mHour);
			editor.putInt(PreferenceUtils.KEY_CUSTOM_MINUTE, mMinute);
			editor.commit();
			getOnPreferenceChangeListener().onPreferenceChange(this, null);
		}
	}
}