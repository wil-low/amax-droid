package com.astromaximum.android;

import java.util.Vector;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;

import com.astromaximum.android.view.SummaryAdapter;
import com.astromaximum.android.view.SummaryItem;
import com.astromaximum.util.DataProvider;

public class MainActivity extends Activity {
	static final int DATE_DIALOG_ID = 0;

	private Context mContext = null;
	private final String TAG = "MainActivity";
	private ListView mEventList = null;
	private Button mDateButton;

	private DataProvider mDataProvider;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "OnCreate");
		mContext = getApplicationContext();
		mDataProvider = DataProvider.getInstance(mContext);

		setContentView(R.layout.main);
		mDateButton = (Button) findViewById(R.id.buttonDate);
		mDateButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showDialog(DATE_DIALOG_ID);
			}
		});

		Button button = (Button) findViewById(R.id.buttonPrevDate);
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				mDataProvider.changeDate(-1);
				updateDisplay();
			}
		});

		button = (Button) findViewById(R.id.buttonNextDate);
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				mDataProvider.changeDate(1);
				updateDisplay();
			}
		});

		mEventList = (ListView) findViewById(R.id.ListViewEvents);

		mEventList
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						SummaryItem si = (SummaryItem) parent
								.getItemAtPosition(position);
						if (!si.mEvents.isEmpty()) {
							Intent intent = new Intent(mContext,
									EventListActivity.class);
							intent.putExtra(SummaryItem.LISTKEY_EVENT_KEY,
									si.mKey);
							intent.putExtra(SummaryItem.LISTKEY_EVENT_DATE,
									mDateButton.getText());
							startActivity(intent);
						}
					}

				});
		setTitle(getVersionedTitle());
		mDataProvider.setTodayDate();
	}

	// the callback received when the user "sets" the date in the dialog
	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			DataProvider.getInstance().setDate(year, monthOfYear, dayOfMonth);
			updateDisplay();
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
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
		}
		return true;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
			return new DatePickerDialog(this, mDateSetListener,
					mDataProvider.getYear(), mDataProvider.getMonth(),
					mDataProvider.getDay());
		default:
			return null;
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.d(TAG, "OnPause");
		mDataProvider.saveState();
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "OnResume");
		mDataProvider.restoreState();
		updateTitle();
		updateDisplay();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		Log.d(TAG, "OnRestart");
	}

	private void updateDisplay() {
		mDataProvider.gatherEvents(DataProvider.RANGE_DAY);
		Vector<SummaryItem> v = mDataProvider.get(DataProvider.RANGE_DAY);
		SummaryItem[] arr = (SummaryItem[]) v
				.toArray(new SummaryItem[v.size()]);
		SummaryAdapter adapter = new SummaryAdapter(mContext, arr);
		mEventList.setAdapter(adapter);
		updateDateButton();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, "onActivityResult " + requestCode + "=>" + resultCode);
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case PreferenceUtils.ID_PREFERENCE:
			updateTitle();
			break;
		}
	}

	private void updateDateButton() {
		mDateButton.setText(DateFormat.format("yyyy-MM-dd",
				DataProvider.mCalendar));
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
		setTitle(getVersionedTitle() + " - " + mDataProvider.getLocationName());
	}
}