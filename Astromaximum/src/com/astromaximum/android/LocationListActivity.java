package com.astromaximum.android;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SectionIndexer;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Window;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.astromaximum.android.util.DataProvider;
import com.astromaximum.android.util.MyLog;
import com.astromaximum.android.view.ViewHolder;
import com.woozzu.android.util.StringMatcher;
import com.woozzu.android.widget.IndexableListView;

public class LocationListActivity extends SherlockActivity {
	private final String TAG = "LocationListActivity";
	private IndexableListView mEventList;
	private DataProvider mDataProvider;
	private Context mContext;
	private ProgressDialog mProgressDialog;
	private AQuery mAQuery;
	private String mTitle = "Download cities";
	private int mYear = 2012, mMode = MODE_COUNTRIES;
	private String mCountryId, mStateId, mCityId;
	private ArrayList<String> mIdentifierList = new ArrayList<String>();
	private ArrayList<String> mNameList = new ArrayList<String>();
	private Button mRetryButton;
	private final static int MODE_COUNTRIES = 0;
	private final static int MODE_STATES = 1;
	private final static int MODE_CITIES = 2;
	private final static int MODE_DOWNLOAD = 3;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		MyLog.d(TAG, "OnCreate: ");
		mContext = this;
		mDataProvider = DataProvider.getInstance(this);
		mAQuery = new AQuery(mContext);
		setContentView(R.layout.activity_location_list);
		ViewHolder.initialize(mContext);
		mEventList = (IndexableListView) findViewById(R.id.ListViewEvents);
		
		mRetryButton = (Button)findViewById(R.id.btn_retry);
		mRetryButton.setOnClickListener(
				new View.OnClickListener() {
					public void onClick(View view) {
						queryLocations();
					}
				});
		mRetryButton.setVisibility(View.GONE);
		
		mEventList
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						switch (mMode) {
						case MODE_COUNTRIES:
							mCountryId = mIdentifierList.get(position);
							break;
						case MODE_STATES:
							mStateId = mIdentifierList.get(position);
							break;
						case MODE_CITIES:
							mCityId = mIdentifierList.get(position);
							downloadCity(mDataProvider.mPeriodKey, mCityId, mNameList.get(position));
							return;
						case MODE_DOWNLOAD:
							mCityId = mIdentifierList.get(position);
						}
						++mMode;
						queryLocations();
					}
				});
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

	private void queryLocations() {
		String url = "http://astromaximum.com/mobi/html/dl.php?lang=en&ajax="
				+ mMode + "&cid=" + mCountryId + "&stateid=" + mStateId + "&y="
				+ mYear;
		switch (mMode) {
		case MODE_COUNTRIES:
			mTitle = "Countries";
			break;
		case MODE_STATES:
			mTitle = "States";
			break;
		case MODE_CITIES:
			mTitle = "Cities";
			break;
		case MODE_DOWNLOAD:
			mTitle = "City";
			return;
		}
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
						getSupportActionBar().setTitle(mTitle);
						mNameList.clear();
						mIdentifierList.clear();
						JSONArray arr0 = json.getJSONArray("content");
						for (int i = 0; i < arr0.length(); ++i) {
							JSONArray arr1 = arr0.getJSONArray(i);
							MyLog.d(TAG, arr1.toString());
							mIdentifierList.add(arr1.getString(mMode == MODE_CITIES ? 2 : 0));
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

	private void downloadCity(final String periodKey, final String cityId, final String cityName) {
		ProgressDialog dialog = new ProgressDialog(this);

		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		dialog.setCancelable(true);
		dialog.setInverseBackgroundForced(false);
		dialog.setCanceledOnTouchOutside(true);
		dialog.setTitle("Downloading " + cityName + "...");
		
		String url = "http://astromaximum.com/data/" + periodKey + "/" + cityId;
		mAQuery.progress(dialog).ajax(url, InputStream.class, new AjaxCallback<InputStream>() {
			public void callback(String url, InputStream is, AjaxStatus status) {
				if (is != null) {
					GZIPInputStream zis = null;
					try {
						zis = new GZIPInputStream(is);
						FileOutputStream fos = mContext.openFileOutput(mDataProvider.mPeriodStr + cityId, Context.MODE_PRIVATE);
						byte[] buffer = new byte[1024];
						int count;
						while ((count = zis.read(buffer)) > 0)
							fos.write(buffer, 0, count);
						fos.close();
						Toast.makeText(mAQuery.getContext(),
								"Downloaded:" + cityName, Toast.LENGTH_LONG)
								.show();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					// ajax error, show error code
					Toast.makeText(mAQuery.getContext(),
							"Error:" + status.getCode(), Toast.LENGTH_LONG)
							.show();
				}				
			}
		});
	}
}
