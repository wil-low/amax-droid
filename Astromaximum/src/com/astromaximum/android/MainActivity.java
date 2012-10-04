package com.astromaximum.android;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;

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

		Button compute = (Button) findViewById(R.id.buttonCompute);
		compute.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				compute();
			}
		});

		mEventList = (ListView) findViewById(R.id.ListViewEvents);

	}

	void compute() {
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
			Intent intent = new Intent(this, SummaryActivity.class);
			startActivity(intent);
			break;
		}
		case R.id.menu_options: {
			Intent intent = new Intent(this, Preferences.class);
			startActivityForResult(intent, Preferences.ID_PREFERENCE);
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
		updateDisplay();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		Log.d(TAG, "OnRestart");
	}

	private void updateDisplay() {
		mDataProvider.gatherEvents(DataProvider.RANGE_DAY);
		ArrayAdapter<Object> adapter = new ArrayAdapter<Object>(mContext, R.layout.simple_event_item, mDataProvider.get(DataProvider.RANGE_DAY));
		mEventList.setAdapter(adapter);
		updateDateButton();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, "onActivityResult " + requestCode + "=>" + resultCode);
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case Preferences.ID_PREFERENCE:
			break;
		}
	}

    private void updateDateButton() {
    	mDateButton.setText(DateFormat.format("yyyy-MM-dd", DataProvider.mCalendar));
    }

}