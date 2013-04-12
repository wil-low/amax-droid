package com.astromaximum.android;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import net.simonvt.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.astromaximum.android.util.AmaxDatabase;
import com.astromaximum.android.util.DataProvider;
import com.astromaximum.android.util.Event;
import com.astromaximum.android.util.InterpretationProvider;
import com.astromaximum.android.util.MyLog;
import com.astromaximum.android.view.SummaryAdapter;
import com.astromaximum.android.view.ViewHolder;
import com.astromaximum.util.CommonDataFile;
import com.astromaximum.util.LocationsDataFile;

public class MainActivity extends SherlockActivity {
	static final int DATE_DIALOG_ID = 0;

	private final String TAG = "MainActivity";
	private ListView mEventList;
	private RelativeLayout mNoPeriodLayout;
	private Button mBuyPeriod;

	private DataProvider mDataProvider;
	private String mTitleDate;
	private Context mContext;
	private boolean mUseVolumeButtons;
	private boolean mIsListVisible = true;
	private AQuery mAQuery;
	private AmaxDatabase mDB;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyLog.setLevel(0);

		MyLog.d(TAG, "OnCreate");
		mContext = this;
		mAQuery = new AQuery(mContext);
		mDB = AmaxDatabase.getInstance(mContext);

		Event.setContext(mContext);
		ViewHolder.initialize(mContext);

		mDataProvider = DataProvider.getInstance(this);
		InterpretationProvider.getInstance(this);

		setContentView(R.layout.activity_main);

		mEventList = (ListView) findViewById(R.id.ListViewEvents);
		mNoPeriodLayout = (RelativeLayout) findViewById(R.id.NoPeriodLayout);
		mBuyPeriod = (Button) mNoPeriodLayout.findViewById(R.id.btnBuyPeriod);

		mBuyPeriod.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				downloadPeriod("20130201", "akd6vtir95bs1kow");
			}
		});
		getSupportActionBar().setDisplayShowHomeEnabled(false);
		updateDisplay(mDataProvider.hasPeriod());
	}

	// the callback received when the user "sets" the date in the dialog
	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(net.simonvt.widget.DatePicker view, int year,
				int monthOfYear, int dayOfMonth) {
			DataProvider.getInstance().setDate(year, monthOfYear, dayOfMonth);
			updateDisplay(mDataProvider.hasPeriod());
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getSupportMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case R.id.menu_prev: {
			previousDate();
			break;
		}
		case R.id.menu_next: {
			nextDate();
			break;
		}
		case R.id.menu_today: {
			mDataProvider.setTodayDate();
			updateDisplay(mDataProvider.hasPeriod());
			break;
		}
		case R.id.menu_data: {
			Intent intent = new Intent(mContext, CitySelectActivity.class);
			startActivity(intent);
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
			DatePickerDialog dlg = new DatePickerDialog(this, mDateSetListener,
					mDataProvider.getYear(), mDataProvider.getMonth(),
					mDataProvider.getDay());
			// dlg.getDatePicker().setMinDate(mDataProvider.getStartJD() +
			// SubDataProcessor.MSECINDAY / 2);
			// dlg.getDatePicker().setMaxDate(mDataProvider.getFinalJD() -
			// SubDataProcessor.MSECINDAY / 2);
			dlg.setTitle(R.string.pick_date);
			dlg.setButton(Dialog.BUTTON_POSITIVE,
					mContext.getText(android.R.string.ok), dlg);
			dlg.setButton(Dialog.BUTTON_NEGATIVE,
					mContext.getText(android.R.string.cancel),
					(OnClickListener) null);
			return dlg;
		default:
			return null;
		}
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
		updateDisplay(mDataProvider.hasPeriod());
		MyLog.d(TAG, "OnRestart");
	}

	private void updateDisplay(boolean hasPeriod) {
		if (hasPeriod) {
			mEventList.setVisibility(View.VISIBLE);
			mNoPeriodLayout.setVisibility(View.INVISIBLE);
			mDataProvider.prepareCalculation();
			mDataProvider.calculateAll();
			SummaryAdapter adapter = new SummaryAdapter(this,
					mDataProvider.mEventCache, mDataProvider.getCustomTime(),
					mDataProvider.getCurrentTime());
			mEventList.setAdapter(adapter);
		} else {
			mEventList.setVisibility(View.INVISIBLE);
			mNoPeriodLayout.setVisibility(View.VISIBLE);
			mBuyPeriod.setTag(String.format("%04d%02d%02d", mDataProvider.getYear(), mDataProvider.getMonth(), 1));
			mBuyPeriod.setText(String.format(
					mContext.getResources().getString(R.string.buy_period),
					mDataProvider.getYear(), mDataProvider.getMonth() + 1));
		}
		updateTitle();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case PreferenceUtils.ID_PREFERENCE:
			updateTitle();
			break;
		}
	}

	private void updateTitle() {
		mTitleDate = mDataProvider.getCurrentDateString();
		getSupportActionBar().setTitle(mTitleDate);
		getSupportActionBar().setSubtitle(
				mDataProvider.getHighlightTimeString() + ", "
						+ mDataProvider.getLocationName());
	}

	private void previousDate() {
		MyLog.d(TAG, "previousDate");
		updateDisplay(mDataProvider.changeDate(-1));
	}

	private void nextDate() {
		MyLog.d(TAG, "nextDate");
		updateDisplay(mDataProvider.changeDate(1));
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
	
	private void downloadPeriod(final String periodStr, final String periodKey) {
		ProgressDialog dialog = new ProgressDialog(this);

		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		dialog.setCancelable(true);
		dialog.setInverseBackgroundForced(false);
		dialog.setCanceledOnTouchOutside(true);
		dialog.setTitle("Downloading " + periodStr + "...");
		
		String url = "http://astromaximum.com/data/?buy=" + periodKey;
		Map<String, Object> params = new HashMap<String, Object>();
        params.put("k", "44b62ab3e3165298849ac71428eca191");
        
		mAQuery.progress(dialog).ajax(url, params, InputStream.class, new AjaxCallback<InputStream>() {
			public void callback(String url, InputStream is, AjaxStatus status) {
				if (is != null) {
					byte[] keyBuffer = new byte[16];
					try {
						if (is.read(keyBuffer) == 16) {
							String receivedKey = new String(keyBuffer);
							GZIPInputStream zis = new GZIPInputStream(is);
							FileOutputStream fos = mContext.openFileOutput(periodStr, Context.MODE_PRIVATE);
							byte[] buffer = new byte[1024];
							int count;
							while ((count = zis.read(buffer)) > 0)
								fos.write(buffer, 0, count);
							fos.close();
							FileInputStream fis = mContext.openFileInput(periodStr);
							CommonDataFile cdf = new CommonDataFile(fis, false);
							fis.close();
							long periodId = mDB.addPeriod(cdf, receivedKey);
							PreferenceUtils.setCommonId(mContext, periodId);
							Toast.makeText(mAQuery.getContext(),
									"Downloaded:" + periodStr, Toast.LENGTH_LONG)
									.show();
							onRestart();
						}
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