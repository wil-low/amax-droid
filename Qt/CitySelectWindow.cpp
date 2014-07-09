package com.astromaximum.android;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
	private String mPeriodString;
	private long mPeriodId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_city_select);
		mContext = this;
		mPeriodString = getIntent().getStringExtra(
				PreferenceUtils.PERIOD_STRING_KEY);

		mCityList = (ListView) findViewById(R.id.cityList);

		mDataProvider = DataProvider.getInstance(getApplicationContext());
		mDB = AmaxDatabase.getInstance(getApplicationContext());

		mPeriodId = PreferenceUtils.getPeriodId(mContext);

		mLocationId = PreferenceUtils.getCityKey(this);
		Cursor cursor = mDB.getPeriodAndCity(mPeriodId, mLocationId);
		if (cursor.moveToFirst()) {
			mPeriodString = DataProvider.makePeriodCaption(cursor.getInt(1),
					cursor.getInt(2), cursor.getInt(3) - 1);
			getSupportActionBar().setTitle(mPeriodString);
		}
		cursor.close();
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
		case R.id.menu_add:
			Intent intent = LocationDownloadActivity.makeIntent(mContext,
					mPeriodString, LocationDownloadActivity.MODE_COUNTRIES,
					"0", "0", "0", null, null, null);
			startActivity(intent);
			break;
		}
		return true;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		if (v.getId() == R.id.layoutCity) {
			final String cityKey = (String) v.getTag(R.id.csa_city_key);
			menu.setHeaderTitle("City key = " + cityKey);
			final long locationId = Long.parseLong((String) v
					.getTag(R.id.csa_location_id));
			android.view.MenuItem item = menu.add(Menu.NONE, 0, 0,
					R.string.city_delete);
			item.setOnMenuItemClickListener(new android.view.MenuItem.OnMenuItemClickListener() {

				public boolean onMenuItemClick(android.view.MenuItem item) {
					return true;
				}
			});
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		MyLog.d(TAG, "OnResume");
		long commonId = PreferenceUtils.getPeriodId(mContext);

		Cursor cursor = mDB.getLocationsForPeriod(commonId);
		CursorAdapter adapter = new CityCursorAdapter(this, cursor, mLocationId);
		mCityList.setAdapter(adapter);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MyLog.d(TAG, "OnPause");
	}

	private void showEraseDialog(View v) {
		TextView tv = (TextView) v.findViewById(R.id.textCity);
		final long locationId = Long.parseLong((String) v
				.getTag(R.id.csa_location_id));
		final String cityKey = (String) v.getTag(R.id.csa_city_key);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setMessage("Erase " + tv.getText() + " ?");
		builder.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						mDB.deleteLocation(mPeriodId, locationId);
						String fileToDelete = mDataProvider.makeLocationFilename(cityKey);
						mContext.deleteFile(fileToDelete);
						onResume();
					}
				});
		builder.setNegativeButton(android.R.string.cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// User cancelled the dialog
					}
				});
		
		AlertDialog dialog = builder.create();
		dialog.show();
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
			final String cityKey = cursor.getString(4);
			view.setTag(R.id.csa_city_key, cityKey);
			view.setTag(R.id.csa_location_id, cursor.getString(0));

			final boolean isChecked = cityKey.equals(mLocationId);
			RadioButton rb = (RadioButton) view.findViewById(R.id.radioButton1);
			rb.setChecked(isChecked);

			view.setOnLongClickListener(new View.OnLongClickListener() {
				public boolean onLongClick(View v) {
					if (!isChecked)
						showEraseDialog(v);
					return true;
				}
			});
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			final View view = LayoutInflater.from(context).inflate(
					R.layout.item_data_city, parent, false);
			view.setOnClickListener(new View.OnClickListener() {

				public void onClick(View view) {
					mLocationId = (String) view.getTag(R.id.csa_city_key);
					PreferenceUtils.setCityKey(mContext, mLocationId);
					finish();
				}
			});
			// registerForContextMenu(view);
			return view;
		}

	}
}
