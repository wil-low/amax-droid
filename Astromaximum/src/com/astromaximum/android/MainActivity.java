package com.astromaximum.android;

import net.simonvt.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;

import com.actionbarsherlock.view.MenuItem;
import com.astromaximum.android.util.DataProvider;
import com.astromaximum.android.util.InterpretationProvider;
import com.astromaximum.android.util.MyLog;
import com.astromaximum.android.view.SummaryAdapter;

public class MainActivity extends BaseEventListActivity {
	static final int DATE_DIALOG_ID = 0;

	private String mTitleDate;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_main,
				"MainActivity", R.menu.main);
		MyLog.setLevel(0);
		MyLog.d(TAG, "OnCreate");
		InterpretationProvider.getInstance(this);
		updateDisplay();
	}

	// the callback received when the user "sets" the date in the dialog
	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(net.simonvt.widget.DatePicker view, int year,
				int monthOfYear, int dayOfMonth) {
			DataProvider.getInstance().setDate(year, monthOfYear, dayOfMonth);
			updateDisplay();
		}
	};

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case R.id.menu_prev: {
			previousDate();
			break;
		}
		case R.id.menu_next: {
			nextDate();
			break;
		}
		case R.id.menu_today: {
			mDataProvider.setTodayDate();
			updateDisplay();
			break;
		}
		case R.id.menu_data: {
			Intent intent = new Intent(mContext, CitySelectActivity.class);
			startActivity(intent);
			break;
		}
		case R.id.menu_options: {
			Intent intent = new Intent(this, Preferences.class);
			startActivityForResult(intent, PreferenceUtils.ID_PREFERENCE);
			break;
		}
		case R.id.menu_pick_date:
			showDialog(DATE_DIALOG_ID);
			break;
		}
		return true;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
			DatePickerDialog dlg = new DatePickerDialog(this, mDateSetListener,
					mDataProvider.getYear(), mDataProvider.getMonth(),
					mDataProvider.getDay());
			// dlg.getDatePicker().setMinDate(mDataProvider.getStartJD() +
			// SubDataProcessor.MSECINDAY / 2);
			// dlg.getDatePicker().setMaxDate(mDataProvider.getFinalJD() -
			// SubDataProcessor.MSECINDAY / 2);
			dlg.setTitle(R.string.pick_date);
			dlg.setButton(Dialog.BUTTON_POSITIVE,
					mContext.getText(android.R.string.ok), dlg);
			dlg.setButton(Dialog.BUTTON_NEGATIVE,
					mContext.getText(android.R.string.cancel),
					(OnClickListener) null);
			return dlg;
		default:
			return null;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case PreferenceUtils.ID_PREFERENCE:
			updateTitle();
			break;
		}
	}

	@Override
	protected void updateTitle() {
		mTitleDate = mDataProvider.getCurrentDateString();
		getSupportActionBar().setTitle(mTitleDate);
		getSupportActionBar().setSubtitle(
				mDataProvider.getHighlightTimeString() + ", "
						+ mDataProvider.getCityName());
	}

	@Override
	protected void updateEventList() {
		mDataProvider.prepareCalculation();
		mDataProvider.calculateAll();
		SummaryAdapter adapter = new SummaryAdapter(this,
				mDataProvider.mEventCache, mDataProvider.getCustomTime(),
				mDataProvider.getCurrentTime());
		mEventList.setAdapter(adapter);
	}
}