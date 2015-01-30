package com.astromaximum.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
/*
import com.android.vending.billing.util.IabHelper;
import com.android.vending.billing.util.IabResult;
import com.android.vending.billing.util.Inventory;
import com.android.vending.billing.util.Purchase;
*/
import com.astromaximum.android.util.AmaxDatabase;
import com.astromaximum.android.util.DataProvider;
import com.astromaximum.android.util.MyLog;

public class PeriodBuyActivity extends SherlockActivity {
	protected static final String TAG = "PeriodSelectActivity";
	private AmaxDatabase mDB;
	private ListView mPeriodList;
	private DataProvider mDataProvider;
	private Context mContext;
	//private IabHelper mHelper;
	private String mPeriodStr;
	private static final int SKU_YEAR = 20130301;
	private final Activity activity = this;
/*
	IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {

		public void onQueryInventoryFinished(IabResult result,
				Inventory inventory) {

			if (result.isFailure()) {
				MyLog.d(TAG,
						"onQueryInventoryFinished: failure "
								+ result.getMessage());
			} else {
				MyLog.d(TAG,
						"onQueryInventoryFinished: success "
								+ result.getMessage());
				// does the user have the premium upgrade?
				// mIsPremium = inventory.hasPurchase(SKU_PREMIUM);
				// update UI accordingly
			}
			mHelper.launchPurchaseFlow(activity, mPeriodStr, 10001,
					mPurchaseFinishedListener,
					"bGoa+V7g/yqDXvKRqq+JTFn4uQZbPiQJo4pf9RzJ");
		}
	};

	IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
		public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
			if (result.isFailure()) {
				Log.d(TAG, "Error purchasing: " + result);
				return;
			} else if (purchase.getSku().equals(mPeriodStr)) {
				Log.d(TAG, "Purchased: " + result);
				// consume the gas and update the UI
			//} else if (purchase.getSku().equals(SKU_PREMIUM)) {
				// give user access to premium content and update the UI
			}
		}
	};*/

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_period_buy);
		mContext = this;
		mPeriodList = (ListView) findViewById(R.id.periodList);

		mDataProvider = DataProvider.getInstance(getApplicationContext());
		mDB = AmaxDatabase.getInstance(getApplicationContext());

		String base64EncodedPublicKey = "ABCDE";
/*
		// compute your public key and store it in base64EncodedPublicKey
		mHelper = new IabHelper(this, base64EncodedPublicKey);

		mPeriodStr = getIntent().getStringExtra(
				PreferenceUtils.PERIOD_STRING_KEY);
		
		mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
			public void onIabSetupFinished(IabResult result) {
				if (!result.isSuccess()) {
					// Oh noes, there was a problem.
					MyLog.d(TAG, "Problem setting up In-app Billing: " + result);
				}
				// Hooray, IAB is fully set up!
				mHelper.queryInventoryAsync(mGotInventoryListener);
			}
		});
*/
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
		long periodId = PreferenceUtils.getPeriodId(mContext);
		String cityKey = PreferenceUtils.getCityKey(this);

		Cursor cursor = mDB.getPeriodAndCity(periodId, cityKey);
		CursorAdapter adapter = new PeriodCursorAdapter(this, cursor);
		mPeriodList.setAdapter(adapter);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MyLog.d(TAG, "OnPause");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
        /*
		if (mHelper != null)
			mHelper.dispose();
		mHelper = null;
		*/
	}

	class PeriodCursorAdapter extends CursorAdapter {
		public PeriodCursorAdapter(Context context, Cursor c) {
			super(context, c);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			TextView tv = (TextView) view.findViewById(R.id.textPeriod);
			tv.setText(DataProvider.makePeriodCaption(cursor.getInt(1),
					cursor.getInt(2), cursor.getInt(3) - 1));
			tv = (TextView) view.findViewById(R.id.textLocation);
			tv.setText(cursor.getString(5));
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			final View view = LayoutInflater.from(context).inflate(
					R.layout.item_data_current_period, parent, false);
			view.setOnClickListener(new View.OnClickListener() {

				public void onClick(View view) {
					Intent intent = new Intent(mContext,
							CitySelectActivity.class);
					TextView tv = (TextView) view.findViewById(R.id.textPeriod);
					intent.putExtra(PreferenceUtils.PERIOD_STRING_KEY,
							(String) tv.getText());
					startActivity(intent);
				}

			});
			return view;
		}

	}
}
