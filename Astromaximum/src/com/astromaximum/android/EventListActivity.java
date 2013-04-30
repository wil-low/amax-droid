package com.astromaximum.android;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.actionbarsherlock.view.MenuItem;
import com.astromaximum.android.util.Event;
import com.astromaximum.android.util.InterpretationProvider;
import com.astromaximum.android.util.MyLog;
import com.astromaximum.android.view.EventListAdapter;
import com.astromaximum.android.view.SummaryItem;

public class EventListActivity extends BaseEventListActivity {
	private int mKey;
	private String mKeyDescription;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_event_list,
				"EventListActivity", R.menu.event_list);
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

		mKey = getIntent().getIntExtra(PreferenceUtils.LISTKEY_EVENT_KEY,
				Event.EV_LAST);
		mKeyDescription = getKeyDescription(mKey);
		updateDisplay();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case R.id.menu_prev:
			previousDate();
			break;
		case R.id.menu_next:
			nextDate();
			break;
		}
		return true;
	}

	@Override
	protected void updateEventList() {
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

		long customTime = mDataProvider.getCustomTime();
		long currentTime = mDataProvider.getCurrentTime();
		EventListAdapter adapter = new EventListAdapter(this, item.mEvents,
				mKey, customTime, currentTime);
		mEventList.setAdapter(adapter);
		int pos = item.getActiveEventPosition(customTime, currentTime);
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
		case Event.EV_RETROGRADE:
			id = R.string.si_retrograde;
			break;
		}
		return mContext.getResources().getString(id);
	}

	@Override
	protected void updateTitle() {
		getSupportActionBar().setTitle(mDataProvider.getCurrentDateString());
		getSupportActionBar().setSubtitle(mKeyDescription);
	}
}
