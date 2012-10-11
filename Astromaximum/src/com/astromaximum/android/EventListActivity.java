package com.astromaximum.android;

import java.util.Vector;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.astromaximum.android.view.SummaryItem;
import com.astromaximum.util.DataProvider;
import com.astromaximum.util.Event;

public class EventListActivity extends Activity {
	private final String TAG = "EventListActivity";
	private ListView mEventList;
	private DataProvider mDataProvider;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "OnCreate: ");
		setContentView(R.layout.event_list_activity);
		mEventList = (ListView) findViewById(R.id.event_list_view);
		mDataProvider = DataProvider.getInstance();
		String key = getIntent().getStringExtra(SummaryItem.LISTKEY_EVENT_KEY);
		setTitle(key + ": " + getIntent().getStringExtra(SummaryItem.LISTKEY_EVENT_DATE));
		Vector<SummaryItem> siv = mDataProvider.get(DataProvider.RANGE_DAY);
		Vector<Event> v = null;
		for (SummaryItem si : siv) {
			if (si.mKey.contentEquals(key)) {
				v = si.mEvents;
				break;
			}
		}
		Event[] arr = (Event[]) v.toArray(new Event[v.size()]);
		ArrayAdapter<Event> adapter = new ArrayAdapter<Event>(
				getApplicationContext(), R.layout.simple_event_item, arr);
		mEventList.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.event_list_activity, menu);
		return true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.d(TAG, "OnPause");
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "OnResume");
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		Log.d(TAG, "OnRestart");
	}
}
