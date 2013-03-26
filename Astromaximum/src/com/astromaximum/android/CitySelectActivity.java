package com.astromaximum.android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.astromaximum.android.util.AmaxDatabase;
import com.astromaximum.android.util.DataProvider;
import com.astromaximum.android.util.MyLog;

public class CitySelectActivity extends SherlockActivity {
	protected static final String TAG = "CitySelectActivity";
	private AmaxDatabase mDB;
	private ListView mCityList;
	private DataProvider mDataProvider;
	private Context mContext;
	private String mLocationId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_city_select);
		mContext = this;

		String periodString = getIntent().getStringExtra(
				PreferenceUtils.PERIOD_STRING_KEY);

		mCityList = (ListView) findViewById(R.id.currentCityList);

		mDataProvider = DataProvider.getInstance(getApplicationContext());
		mDB = AmaxDatabase.getInstance(getApplicationContext());

		getSupportActionBar().setTitle(periodString);
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
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		if (v.getId() == R.id.layoutCity) {
			menu.setHeaderTitle("City key = ");
			String[] menuItems = getResources().getStringArray(
					R.array.menu_city);
			for (int i = 0; i < menuItems.length; i++) {
				menu.add(Menu.NONE, i, i, menuItems[i]);
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		MyLog.d(TAG, "OnResume");
		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(this);
		int commonId = sharedPref.getInt(PreferenceUtils.KEY_COMMON_ID, 0);
		mLocationId = PreferenceUtils.getLocationId(this);

		Cursor cursor = mDB.getCitiesForPeriod(commonId);
		CursorAdapter adapter = new CityCursorAdapter(this, cursor, mLocationId);
		mCityList.setAdapter(adapter);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MyLog.d(TAG, "OnPause");
	}

	class CityCursorAdapter extends CursorAdapter {
		private String mLocationId;

		public CityCursorAdapter(Context context, Cursor c, String locationId) {
			super(context, c);
			mLocationId = locationId;
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			TextView tv = (TextView) view.findViewById(R.id.textCity);
			tv.setText(cursor.getString(1));
			String summary = cursor.getString(3);
			if (!cursor.getString(2).equals("")) {
				summary += ", " + cursor.getString(2);
			}
			tv = (TextView) view.findViewById(R.id.textSummary);
			tv.setText(summary);
			String cityKey = cursor.getString(4);
			view.setTag(cityKey);
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			final View view = LayoutInflater.from(context).inflate(
					R.layout.item_data_city, parent, false);
			view.setOnClickListener(new View.OnClickListener() {

				public void onClick(View view) {
					mLocationId = (String) view.getTag();
					PreferenceUtils.setLocationId(mContext, mLocationId);
					finish();
				}
			});
			registerForContextMenu(view);
			return view;
		}

	}
}
