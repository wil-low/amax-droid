package com.astromaximum.android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.astromaximum.android.util.DataProvider;
import com.astromaximum.android.util.Downloader;
import com.astromaximum.android.util.Event;
import com.astromaximum.android.util.MyLog;
import com.astromaximum.android.view.ViewHolder;

public class BaseEventListActivity extends SherlockActivity {
	protected String TAG = null;

	protected DataProvider mDataProvider;
	protected Context mContext;
	protected boolean mUseVolumeButtons;
	protected ListView mEventList;
	protected RelativeLayout mNoPeriodLayout;
	protected Button mMissingDataButton;
	protected TextView mMissingDataMessage;
	private int mMenuResId;

	public void onCreate(Bundle savedInstanceState, int layoutResId,
			String tag, int menuResId) {
		super.onCreate(savedInstanceState);
		TAG = tag;
		mMenuResId = menuResId;
		mContext = this;
		Event.setContext(mContext);
		ViewHolder.initialize(mContext);
		mDataProvider = DataProvider.getInstance(this);

		setContentView(layoutResId);

		mEventList = (ListView) findViewById(R.id.event_list_view);
		mNoPeriodLayout = (RelativeLayout) findViewById(R.id.NoPeriodLayout);
		mMissingDataMessage = (TextView) mNoPeriodLayout
				.findViewById(R.id.txtMissingData);
		mMissingDataButton = (Button) mNoPeriodLayout
				.findViewById(R.id.btnMissingData);
		getSupportActionBar().setDisplayShowHomeEnabled(false);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getSupportMenuInflater().inflate(mMenuResId, menu);
		return true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		MyLog.d(TAG, "OnPause");
		mDataProvider.saveState();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mUseVolumeButtons = PreferenceUtils.getUseVolumeButtons(mContext);
		MyLog.d(TAG, "OnResume");
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		mDataProvider.restoreState();
		updateDisplay();
		MyLog.d(TAG, "OnRestart");
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		switch (event.getKeyCode()) {
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			if (!mUseVolumeButtons)
				break;
			if (event.getAction() == KeyEvent.ACTION_DOWN)
				previousDate();
			return true;
		case KeyEvent.KEYCODE_VOLUME_UP:
			if (!mUseVolumeButtons)
				break;
			if (event.getAction() == KeyEvent.ACTION_DOWN)
				nextDate();
			return true;
		}
		return super.dispatchKeyEvent(event);
	}

	protected void previousDate() {
		MyLog.d(TAG, "previousDate");
		mDataProvider.changeDate(-1);
		updateDisplay();
	}

	protected void nextDate() {
		MyLog.d(TAG, "nextDate");
		mDataProvider.changeDate(1);
		updateDisplay();
	}

	protected void downloadPeriod(String periodStr) {
		Downloader.getInstance(mContext).downloadPeriod(periodStr,
				"vlr41lhxbh0f0mbr", new Downloader.Callback() {
					public void callback(boolean isSuccess) {
						if (isSuccess) {
							onPause();
							onRestart();
						}
					}
				});
	}

	protected void buyPeriod(String periodStr) {
		Intent intent = new Intent(mContext, PeriodBuyActivity.class);
		intent.putExtra(PreferenceUtils.PERIOD_STRING_KEY, periodStr);
		startActivity(intent);
	}

	final protected void updateDisplay() {
		if (mDataProvider.hasPeriod()) {
			if (mDataProvider.hasLocation()) {
				mEventList.setVisibility(View.VISIBLE);
				mNoPeriodLayout.setVisibility(View.INVISIBLE);
				updateEventList();
			} else {
				// No location
				mEventList.setVisibility(View.INVISIBLE);
				mNoPeriodLayout.setVisibility(View.VISIBLE);
				mMissingDataMessage.setText(R.string.no_location);

				mMissingDataButton.setTag(String.format("%04d%02d%02d",
						mDataProvider.getYear(), mDataProvider.getMonth(), 1));
				mMissingDataButton.setText(String.format(mContext
						.getResources().getString(R.string.download_location),
						mDataProvider.getCityName()));

				mMissingDataButton
						.setOnClickListener(new View.OnClickListener() {
							public void onClick(View v) {
								String cityKey = PreferenceUtils
										.getCityKey(mContext);
								Downloader.getInstance(mContext)
										.downloadLocation(mDataProvider,
												cityKey,
												mDataProvider.getCityName(),
												new Downloader.Callback() {
													public void callback(
															boolean isSuccess) {
														if (isSuccess) {
															onPause();
															onRestart();
														}
													}
												});
							}
						});
			}
		} else {
			// No period
			mEventList.setVisibility(View.INVISIBLE);
			mNoPeriodLayout.setVisibility(View.VISIBLE);
			mMissingDataMessage.setText(R.string.no_period);

			final String periodStr = String.format("%04d%02d%02d",
					mDataProvider.getYear(), mDataProvider.getMonth(), 1);
			mMissingDataButton.setText(String.format(mContext.getResources()
					.getString(R.string.buy_period), mDataProvider.getYear(),
					mDataProvider.getMonth() + 1));

			mMissingDataButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					// buyPeriod(periodKey);
					downloadPeriod(periodStr);
				}
			});
		}
		updateTitle();
	}

	protected void updateEventList() {
	}

	protected void updateTitle() {
	}
}
