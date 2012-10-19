package com.astromaximum.android;

import java.util.Vector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
		final Context context = this;
		mEventList
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						Event ev = (Event) parent.getItemAtPosition(position);
						/*
						 * int duration = Toast.LENGTH_LONG; Toast toast =
						 * Toast.makeText(mContext, si.toString(), duration);
						 * toast.show();
						 */
						String text = InterpreterActivity.getInterpreterText(
								context, ev);
						if (text != null) {
							Intent intent = new Intent(getApplicationContext(),
									InterpreterActivity.class);
							intent.putExtra(
									SummaryItem.LISTKEY_INTERPRETER_TEXT, text);
							intent.putExtra(
									SummaryItem.LISTKEY_INTERPRETER_EVENT, ev);
							startActivity(intent);
						}
					}

				});

		mDataProvider = DataProvider.getInstance();
		int key = getIntent().getIntExtra(SummaryItem.LISTKEY_EVENT_KEY, Event.EV_LAST);
		setTitle(Event.EVENT_TYPE_STR[key] + ": "
				+ getIntent().getStringExtra(SummaryItem.LISTKEY_EVENT_DATE));
		Vector<SummaryItem> siv = mDataProvider.get(DataProvider.RANGE_DAY);
		Vector<Event> v = null;
		for (SummaryItem si : siv) {
			if (si.mKey == key) {
				v = si.mEvents;
				break;
			}
		}
		if (v == null)
			Log.e(TAG, "No events: key=" + key);
		Event[] arr = (Event[]) v.toArray(new Event[v.size()]);
		ArrayAdapter<Event> adapter = new ArrayAdapter<Event>(
				getApplicationContext(), R.layout.simple_event_item, arr);
		mEventList.setAdapter(adapter);
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
