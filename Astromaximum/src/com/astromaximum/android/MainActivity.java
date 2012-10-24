package com.astromaximum.android;

import java.util.Vector;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ListView;

import com.astromaximum.android.view.AstroTextView;
import com.astromaximum.android.view.SummaryAdapter;
import com.astromaximum.android.view.SummaryItem;
import com.astromaximum.android.view.ViewHolder;
import com.astromaximum.util.DataProvider;
import com.astromaximum.util.InterpretationProvider;

public class MainActivity extends Activity {
	static final int DATE_DIALOG_ID = 0;

	private final String TAG = "MainActivity";
	private ListView mEventList = null;

	private int REL_SWIPE_MIN_DISTANCE;
	private int REL_SWIPE_MAX_OFF_PATH;
	private int REL_SWIPE_THRESHOLD_VELOCITY;

	private DataProvider mDataProvider;
	private String mTitleDateFormat;
	private String mTitleDate;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "OnCreate");
		ViewHolder.setContext(this);

		AstroTextView.assignTypeface(Typeface.createFromAsset(getAssets(),
				"fonts/Astronom.ttf"));
		mTitleDateFormat = getResources().getString(R.string.title_date_format);

		// As paiego pointed out, it's better to use density-aware measurements.
		DisplayMetrics dm = getResources().getDisplayMetrics();
		REL_SWIPE_MIN_DISTANCE = (int) (120.0f * dm.densityDpi / 160.0f + 0.5);
		REL_SWIPE_MAX_OFF_PATH = (int) (250.0f * dm.densityDpi / 160.0f + 0.5);
		REL_SWIPE_THRESHOLD_VELOCITY = (int) (200.0f * dm.densityDpi / 160.0f + 0.5);

		mDataProvider = DataProvider.getInstance(this);
		InterpretationProvider.getInstance(this);

		setContentView(R.layout.main);

		mEventList = (ListView) findViewById(R.id.ListViewEvents);

		final GestureDetector gestureDetector = new GestureDetector(
				new MyGestureDetector());
		View.OnTouchListener gestureListener = new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				return gestureDetector.onTouchEvent(event);
			}
		};
		mEventList.setOnTouchListener(gestureListener);

		// setTitle(getVersionedTitle());
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
		SummaryAdapter adapter = new SummaryAdapter(this, arr);
		mEventList.setAdapter(adapter);
		updateTitle();
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
		mTitleDate = mDataProvider.getCurrentDateString(mTitleDateFormat);
		setTitle(mDataProvider.getLocationName() + " : " + mTitleDate);
	}

	private void previousDate() {
		Log.d(TAG, "previousDate");
		mDataProvider.changeDate(-1);
		updateDisplay();
		// Toast.makeText(this, "Left-to-right fling",
		// Toast.LENGTH_SHORT).show();
	}

	private void nextDate() {
		Log.d(TAG, "nextDate");
		mDataProvider.changeDate(1);
		updateDisplay();
		// Toast.makeText(this, "Right-to-left fling",
		// Toast.LENGTH_SHORT).show();
	}

	public void onEventItemClick(int position) {
		Log.d(TAG, "onItemClick " + position);
		SummaryItem si = (SummaryItem) mEventList.getItemAtPosition(position);
		if (!si.mEvents.isEmpty()) {
			Intent intent = new Intent(this, EventListActivity.class);
			intent.putExtra(SummaryItem.LISTKEY_EVENT_KEY, si.mKey);
			intent.putExtra(SummaryItem.LISTKEY_EVENT_DATE, mTitleDate);
			startActivity(intent);
		}
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		switch (event.getKeyCode()) {
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			if (event.getAction() == KeyEvent.ACTION_DOWN)
				previousDate();
			return true;
		case KeyEvent.KEYCODE_VOLUME_UP:
			if (event.getAction() == KeyEvent.ACTION_DOWN)
				nextDate();
			return true;
		}
		return super.dispatchKeyEvent(event);
	}

	class MyGestureDetector extends SimpleOnGestureListener {

		// Detect a single-click and call my own handler.
		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			int pos = mEventList
					.pointToPosition((int) e.getX(), (int) e.getY());
			onEventItemClick(pos);
			return false;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			if (e1 == null || e2 == null)
				return false;
			if (Math.abs(e1.getY() - e2.getY()) > REL_SWIPE_MAX_OFF_PATH)
				return false;
			if (e1.getX() - e2.getX() > REL_SWIPE_MIN_DISTANCE
					&& Math.abs(velocityX) > REL_SWIPE_THRESHOLD_VELOCITY) {
				nextDate();
			} else if (e2.getX() - e1.getX() > REL_SWIPE_MIN_DISTANCE
					&& Math.abs(velocityX) > REL_SWIPE_THRESHOLD_VELOCITY) {
				previousDate();
			}
			return false;
		}

	}
}