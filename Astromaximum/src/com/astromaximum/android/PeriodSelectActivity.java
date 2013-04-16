package com.astromaximum.android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.astromaximum.android.util.AmaxDatabase;
import com.astromaximum.android.util.DataProvider;
import com.astromaximum.android.util.MyLog;

public class PeriodSelectActivity extends SherlockActivity {
	protected static final String TAG = "PeriodSelectActivity";
	private AmaxDatabase mDB;
	private ListView mCurrentPeriodList, mAvailPeriodList;
	private DataProvider mDataProvider;
	private Context mContext;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_period_select);
		mContext = this;
		mCurrentPeriodList = (ListView) findViewById(R.id.currentPeriodList);

		mAvailPeriodList = (ListView) findViewById(R.id.availPeriodList);

		mDataProvider = DataProvider.getInstance(getApplicationContext());
		mDB = AmaxDatabase.getInstance(getApplicationContext());

		getSupportActionBar().setTitle(R.string.data);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getSupportMenuInflater().inflate(R.menu.data, menu);
		return true;
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
		long commonId = PreferenceUtils.getPeriodId(mContext);
		String locationId = PreferenceUtils.getCityKey(this);

		Cursor cursor = mDB.getPeriodAndCity(commonId, locationId);
		CursorAdapter adapter = new CurrentPeriodCursorAdapter(this, cursor);
		mCurrentPeriodList.setAdapter(adapter);

		cursor = mDB.getAvailablePeriods(mDataProvider.getCommonId());
		adapter = new AvailablePeriodCursorAdapter(this, cursor);
		mAvailPeriodList.setAdapter(adapter);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MyLog.d(TAG, "OnPause");
	}

	class CurrentPeriodCursorAdapter extends CursorAdapter {
		public CurrentPeriodCursorAdapter(Context context, Cursor c) {
			super(context, c);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			TextView tv = (TextView) view.findViewById(R.id.textCommon);
			tv.setText(DataProvider.makePeriodCaption(cursor.getInt(1), cursor.getInt(2),
					cursor.getInt(3) - 1));
			tv = (TextView) view.findViewById(R.id.textLocation);
			tv.setText(cursor.getString(5));
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			final View view = LayoutInflater.from(context).inflate(
					R.layout.item_data_current_period, parent, false);
			view
			.setOnClickListener(new View.OnClickListener() {

				public void onClick(View view) {
					Intent intent = new Intent(mContext, CitySelectActivity.class);
					TextView tv = (TextView) view.findViewById(R.id.textCommon);
					intent.putExtra(PreferenceUtils.PERIOD_STRING_KEY, (String) tv.getText());
					startActivity(intent);
				}

			});
			return view;
		}

	}

	class AvailablePeriodCursorAdapter extends CursorAdapter {
		public AvailablePeriodCursorAdapter(Context context, Cursor c) {
			super(context, c);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			final TextView tv = (TextView) view
					.findViewById(android.R.id.text1);
			tv.setText(DataProvider.makePeriodCaption(cursor.getInt(1), cursor.getInt(2),
					cursor.getInt(3) - 1));
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			final View view = LayoutInflater.from(context).inflate(
					android.R.layout.simple_list_item_1, parent, false);
			return view;
		}

	}
}
