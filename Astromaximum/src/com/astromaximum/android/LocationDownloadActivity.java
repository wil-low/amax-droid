package com.astromaximum.android;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SectionIndexer;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.astromaximum.android.util.DataProvider;
import com.astromaximum.android.util.Downloader;
import com.astromaximum.android.util.Downloader.Callback;
import com.astromaximum.android.util.MyLog;
import com.astromaximum.android.view.ViewHolder;
import com.woozzu.android.util.StringMatcher;
import com.woozzu.android.widget.IndexableListView;

public class LocationDownloadActivity extends SherlockActivity {
	private final String TAG = "LocationDownloadActivity";
	private IndexableListView mEventList;
	private DataProvider mDataProvider;
	private Context mContext;
	private AQuery mAQuery;
	private int mTitleId = 0;
	private int mMode = MODE_COUNTRIES;
	private String mCountryId, mStateId, mCityId, mPeriodString;
	private ArrayList<String> mIdentifierList = new ArrayList<String>();
	private ArrayList<String> mNameList = new ArrayList<String>();
	private Button mRetryButton;
	protected Callback mDownloadCallback;
	protected String mCityName;
	public final static int MODE_COUNTRIES = 0;
	public final static int MODE_STATES = 1;
	public final static int MODE_CITIES = 2;
	public final static int MODE_DOWNLOAD = 3;

	public static Intent makeIntent(Context context, String periodString,
			int mode, String countryId, String stateId, String cityId) {
		Intent intent = new Intent(context, LocationDownloadActivity.class);
		intent.putExtra(PreferenceUtils.PERIOD_STRING_KEY, periodString);
		intent.putExtra(PreferenceUtils.MODE_KEY, mode);
		intent.putExtra(PreferenceUtils.COUNTRY_ID_KEY, countryId);
		intent.putExtra(PreferenceUtils.STATE_ID_KEY, stateId);
		intent.putExtra(PreferenceUtils.CITY_ID_KEY, cityId);
		return intent;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		MyLog.d(TAG, "OnCreate: ");
		mContext = this;
		mPeriodString = getIntent().getStringExtra(
				PreferenceUtils.PERIOD_STRING_KEY);
		mMode = getIntent().getIntExtra(PreferenceUtils.MODE_KEY,
				MODE_COUNTRIES);
		mCountryId = getIntent().getStringExtra(PreferenceUtils.COUNTRY_ID_KEY);
		mStateId = getIntent().getStringExtra(PreferenceUtils.STATE_ID_KEY);
		mCityId = getIntent().getStringExtra(PreferenceUtils.CITY_ID_KEY);
		getSupportActionBar().setSubtitle(mPeriodString);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		mDataProvider = DataProvider.getInstance(this);
		mAQuery = new AQuery(mContext);
		setContentView(R.layout.activity_location_list);
		ViewHolder.initialize(mContext);
		mEventList = (IndexableListView) findViewById(R.id.ListViewEvents);

		mRetryButton = (Button) findViewById(R.id.btn_retry);
		mRetryButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				queryLocations();
			}
		});
		mRetryButton.setVisibility(View.GONE);

		mEventList
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						String countryId = mCountryId;
						String stateId = mStateId;
						switch (mMode) {
						case MODE_COUNTRIES:
							countryId = mIdentifierList.get(position);
							break;
						case MODE_STATES:
							stateId = mIdentifierList.get(position);
							break;
						case MODE_CITIES:
							mCityId = mIdentifierList.get(position);
							mCityName = mNameList.get(position);
							Downloader.getInstance(mContext).downloadCity(
									mDataProvider, mCityId, mCityName,
									mDownloadCallback);
							return;
						case MODE_DOWNLOAD:
							mCityId = mIdentifierList.get(position);
						}
						Intent intent = LocationDownloadActivity.makeIntent(
								mContext, mPeriodString, mMode + 1, countryId,
								stateId, mCityId);
						startActivity(intent);
					}
				});
		mDownloadCallback = new Downloader.Callback() {
			public void callback(boolean isSuccess) {
				if (isSuccess) {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							mContext);
					builder.setTitle(mCityName);
					builder.setMessage(R.string.make_current);
		            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
		                   public void onClick(DialogInterface dialog, int id) {
		                	   PreferenceUtils.setLocationId(mContext, mCityId);
		                	   mDataProvider.restoreState();
		                	   Intent intent = new Intent(mContext, MainActivity.class);
		                	   intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		                	   startActivity(intent);
		                   }
		               });
		            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
		                   public void onClick(DialogInterface dialog, int id) {
		                       // User cancelled the dialog
		                   }
		               });					
					AlertDialog dialog = builder.create();
					dialog.show();
				}
			}
		};
		mEventList.setFastScrollEnabled(true);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MyLog.d(TAG, "OnPause");
	}

	@Override
	protected void onResume() {
		super.onResume();
		queryLocations();
		MyLog.d(TAG, "OnResume");
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		MyLog.d(TAG, "OnRestart");
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

	private void queryLocations() {
		if (mCountryId == null)
			mCountryId = "0";
		if (mStateId == null)
			mStateId = "0";
		if (mCityId == null)
			mCityId = "0";

		String url = "http://astromaximum.com/mobi/html/dl.php?lang=en&ajax="
				+ mMode + "&cid=" + mCountryId + "&stateid=" + mStateId + "&y="
				+ mDataProvider.getYear();
		switch (mMode) {
		case MODE_COUNTRIES:
			mTitleId = R.string.country_list;
			break;
		case MODE_STATES:
			mTitleId = R.string.state_list;
			break;
		case MODE_CITIES:
			mTitleId = R.string.city_list;
			break;
		case MODE_DOWNLOAD:
			return;
		}
		MyLog.d(TAG, url);
		getSupportActionBar().setTitle(mTitleId);
		setSupportProgressBarIndeterminateVisibility(true);
		mAQuery.ajax(url, JSONObject.class, new AjaxCallback<JSONObject>() {
			@Override
			public void callback(String url, JSONObject json, AjaxStatus status) {
				setSupportProgressBarIndeterminateVisibility(false);
				if (json != null) {
					mRetryButton.setVisibility(View.GONE);
					try {
						// successful ajax call, show status code and json
						// content
						mNameList.clear();
						mIdentifierList.clear();
						JSONArray arr0 = json.getJSONArray("content");
						for (int i = 0; i < arr0.length(); ++i) {
							JSONArray arr1 = arr0.getJSONArray(i);
							MyLog.d(TAG, arr1.toString());
							mIdentifierList.add(arr1
									.getString(mMode == MODE_CITIES ? 2 : 0));
							if (mMode == MODE_STATES
									&& arr1.getString(0).equals("0"))
								mNameList.add(mContext.getResources()
										.getString(R.string.all_states));
							else
								mNameList.add(arr1.getString(1));
						}
						ContentAdapter adapter = new ContentAdapter(mContext,
								android.R.layout.simple_list_item_1, mNameList);
						mEventList.setAdapter(adapter);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					mRetryButton.setVisibility(View.VISIBLE);
					// ajax error, show error code
					Toast.makeText(mAQuery.getContext(),
							"Error:" + status.getCode(), Toast.LENGTH_SHORT)
							.show();
				}
			}
		});
	}

	private class ContentAdapter extends ArrayAdapter<String> implements
			SectionIndexer {

		private String mSections = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

		public ContentAdapter(Context context, int textViewResourceId,
				List<String> objects) {
			super(context, textViewResourceId, objects);
		}

		public int getPositionForSection(int section) {
			// If there is no item for current section, previous section will be
			// selected
			for (int i = section; i >= 0; i--) {
				for (int j = 0; j < getCount(); j++) {
					if (i == 0) {
						// For numeric section
						for (int k = 0; k <= 9; k++) {
							if (StringMatcher.match(
									String.valueOf(getItem(j).charAt(0)),
									String.valueOf(k)))
								return j;
						}
					} else {
						if (StringMatcher.match(
								String.valueOf(getItem(j).charAt(0)),
								String.valueOf(mSections.charAt(i))))
							return j;
					}
				}
			}
			return 0;
		}

		public int getSectionForPosition(int position) {
			return 0;
		}

		public Object[] getSections() {
			String[] sections = new String[mSections.length()];
			for (int i = 0; i < mSections.length(); i++)
				sections[i] = String.valueOf(mSections.charAt(i));
			return sections;
		}
	}
}
