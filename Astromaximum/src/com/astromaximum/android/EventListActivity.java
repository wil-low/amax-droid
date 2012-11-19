package com.astromaximum.android;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.astromaximum.android.view.EventListAdapter;
import com.astromaximum.android.view.SummaryItem;
import com.astromaximum.android.view.ViewHolder;
import com.astromaximum.util.DataProvider;
import com.astromaximum.util.Event;
import com.astromaximum.util.InterpretationProvider;
import com.astromaximum.util.MyLog;

public class EventListActivity extends Activity {
	private final String TAG = "EventListActivity";
	private ListView mEventList;
	private DataProvider mDataProvider;
	private Context mContext;
	private int mKey;
	private String mKeyDescription;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyLog.d(TAG, "OnCreate: ");
		mContext = this;
		setContentView(R.layout.event_list_activity);
		ViewHolder.initialize(mContext);
		mEventList = (ListView) findViewById(R.id.event_list_view);
		mEventList
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						Event ev = (Event) parent.getItemAtPosition(position);
						String text = InterpretationProvider.getInstance()
								.getText(ev);
						if (text != null) {
							Intent intent = new Intent(mContext,
									InterpreterActivity.class);
							intent.putExtra(
									PreferenceUtils.LISTKEY_INTERPRETER_TEXT,
									text);
							intent.putExtra(
									PreferenceUtils.LISTKEY_INTERPRETER_EVENT,
									ev);
							startActivity(intent);
						}
					}

				});

		mDataProvider = DataProvider.getInstance(getApplicationContext());

		mKey = getIntent().getIntExtra(PreferenceUtils.LISTKEY_EVENT_KEY,
				Event.EV_LAST);
		mKeyDescription = getKeyDescription(mKey);
		updateDisplay();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mDataProvider.saveState();
		MyLog.d(TAG, "OnPause");
	}

	@Override
	protected void onResume() {
		super.onResume();
		MyLog.d(TAG, "OnResume");
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		MyLog.d(TAG, "OnRestart");
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

	private void previousDate() {
		mDataProvider.changeDate(-1);
		updateDisplay();
	}

	private void nextDate() {
		mDataProvider.changeDate(1);
		updateDisplay();
	}

	private void updateDisplay() {
		setTitle(mKeyDescription + ": " + mDataProvider.getCurrentDateString());
		SummaryItem item = null;
		if (mDataProvider.mEventCache.isEmpty()) {
			mDataProvider.prepareCalculation();
			mDataProvider.calculate(mKey);
		}

		for (SummaryItem si : mDataProvider.mEventCache) {
			if (si.mKey == mKey) {
				item = si;
				break;
			}
		}

		if (item.mEvents == null)
			MyLog.e(TAG, "No events: key=" + mKey);

		long highlightTime = mDataProvider.getHighlightTime();
		EventListAdapter adapter = new EventListAdapter(this, item.mEvents,
				mKey, highlightTime);
		mEventList.setAdapter(adapter);
		int pos = item.getActiveEventPosition(highlightTime);
		if (pos != -1) {
			mEventList.setSelection(pos);
			MyLog.d(TAG, "Selection pos " + pos);
		}
	}

	private String getKeyDescription(int key) {
		int id = 0;
		switch (key) {
		case Event.EV_VOC:
			id = R.string.si_key_voc;
			break;
		case Event.EV_VIA_COMBUSTA:
			id = R.string.si_key_vc;
			break;
		case Event.EV_SUN_DEGREE:
			id = R.string.si_key_sun_degree;
			break;
		case Event.EV_MOON_SIGN:
			id = R.string.si_key_moon_sign;
			break;
		case Event.EV_PLANET_HOUR:
			id = R.string.si_key_planet_hour;
			break;
		case Event.EV_MOON_MOVE:
			id = R.string.si_key_moon_move;
			break;
		case Event.EV_TITHI:
			id = R.string.si_key_tithi;
			break;
		case Event.EV_ASP_EXACT:
			id = R.string.si_aspect;
			break;
		}
		return mContext.getResources().getString(id);
	}
}
