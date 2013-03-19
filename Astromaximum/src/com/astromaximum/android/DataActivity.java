package com.astromaximum.android;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.astromaximum.android.util.AmaxDatabase;
import com.astromaximum.android.util.DataProvider;
import com.astromaximum.android.util.Event;
import com.astromaximum.android.util.InterpretationProvider;
import com.astromaximum.android.util.MyLog;

public class DataActivity extends SherlockActivity {
	protected static final String TAG = "DataActivity";
	private AmaxDatabase mDB;
	private ListView mCurrentPeriod, mAvailPeriodList;
	private DataProvider mDataProvider;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_data);

		mCurrentPeriod = (ListView) findViewById(R.id.currentPeriod);
		mCurrentPeriod
		.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				++position;
			}

		});

		mAvailPeriodList = (ListView) findViewById(R.id.availPeriodList);
		mAvailPeriodList
		.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				++position;
			}

		});

		mDataProvider = DataProvider.getInstance(getApplicationContext());
		mDB = AmaxDatabase.getInstance(getApplicationContext());

		getSupportActionBar().setTitle(R.string.data);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

	@Override
	protected void onResume() {
		super.onResume();
		MyLog.d(TAG, "OnResume");
		Cursor cursor = mDB.getPeriods(mDataProvider.getCommonId());
		CursorAdapter adapter = new PeriodCursorAdapter(this, cursor);
		// Bind to our new adapter.
		mAvailPeriodList.setAdapter(adapter);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MyLog.d(TAG, "OnPause");
	}

	class PeriodCursorAdapter extends CursorAdapter {
		public PeriodCursorAdapter(Context context, Cursor c) {
			super(context, c);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			final TextView tv = (TextView) view
					.findViewById(android.R.id.text1);
			tv.setText(makePeriodCaption(cursor));
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			final View view = LayoutInflater.from(context).inflate(
					android.R.layout.simple_list_item_1, parent, false);
			return view;
		}

	}

	public CharSequence makePeriodCaption(Cursor cursor) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(cursor.getInt(1), cursor.getInt(2), 1);
		String text = (String) DateFormat.format("yyyy, MMM ", calendar);
		int monthCount = cursor.getInt(3) - 1;
		if (monthCount > 1) {
			calendar.add(Calendar.MONTH, monthCount);
			text += DateFormat.format("- MMM", calendar);
		}
		return text;
	}

}
