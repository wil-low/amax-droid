package com.astromaximum.android;

import net.simonvt.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.astromaximum.android.util.DataProvider;
import com.astromaximum.android.util.Event;
import com.astromaximum.android.util.InterpretationProvider;
import com.astromaximum.android.util.MyLog;
import com.astromaximum.android.view.SummaryAdapter;
import com.astromaximum.android.view.ViewHolder;

public class MainActivity extends SherlockActivity {
	static final int DATE_DIALOG_ID = 0;

	private final String TAG = "MainActivity";
	private ListView mEventList = null;

	private DataProvider mDataProvider;
	private String mTitleDate;
	private Context mContext;
	private boolean mUseVolumeButtons;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyLog.setLevel(0);
		
		MyLog.d(TAG, "OnCreate");
		mContext = this;
		
		Event.setContext(mContext);
		ViewHolder.initialize(mContext);

		mDataProvider = DataProvider.getInstance(this);
		InterpretationProvider.getInstance(this);

		setContentView(R.layout.activity_main);

		mEventList = (ListView) findViewById(R.id.ListViewEvents);

		getSupportActionBar().setDisplayShowHomeEnabled(false);
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
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getSupportMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

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
			dlg.getDatePicker().setMinDate(mDataProvider.getStartJD());
			dlg.getDatePicker().setMaxDate(mDataProvider.getFinalJD() - 60 * 1000);
			dlg.setTitle(R.string.pick_date);
	        dlg.setButton(Dialog.BUTTON_POSITIVE, mContext.getText(android.R.string.ok), dlg);
	        dlg.setButton(Dialog.BUTTON_NEGATIVE, mContext.getText(android.R.string.cancel), (OnClickListener) null);
			return dlg;
		default:
			return null;
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		MyLog.d(TAG, "OnPause");
		mDataProvider.saveState();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mUseVolumeButtons = PreferenceUtils.getUseVolumeButtons(mContext);
		MyLog.d(TAG, "OnResume");
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		mDataProvider.restoreState();
		updateDisplay();
		MyLog.d(TAG, "OnRestart");
	}

	private void updateDisplay() {
		mDataProvider.prepareCalculation();
		mDataProvider.calculateAll();
		SummaryAdapter adapter = new SummaryAdapter(this, mDataProvider.mEventCache,
				mDataProvider.getCustomTime(), mDataProvider.getCurrentTime());
		mEventList.setAdapter(adapter);
		updateTitle();
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

	private String getVersionedTitle() {
		PackageInfo pInfo;
		try {
			pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			return getString(R.string.app_name) + " " + pInfo.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return getString(R.string.app_name);
	}

	private void updateTitle() {
		mTitleDate = mDataProvider.getCurrentDateString();
		getSupportActionBar().setTitle(mTitleDate);
		getSupportActionBar().setSubtitle(mDataProvider.getHighlightTimeString() + ", " + mDataProvider.getLocationName());
	}

	private void previousDate() {
		MyLog.d(TAG, "previousDate");
		if (mDataProvider.changeDate(-1))
			updateDisplay();
	}

	private void nextDate() {
		MyLog.d(TAG, "nextDate");
		if (mDataProvider.changeDate(1))
			updateDisplay();
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		switch (event.getKeyCode()) {
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			if (!mUseVolumeButtons)
				break;
			if (event.getAction() == KeyEvent.ACTION_DOWN)
				previousDate();
			return true;
		case KeyEvent.KEYCODE_VOLUME_UP:
			if (!mUseVolumeButtons)
				break;
			if (event.getAction() == KeyEvent.ACTION_DOWN)
				nextDate();
			return true;
		}
		return super.dispatchKeyEvent(event);
	}
}