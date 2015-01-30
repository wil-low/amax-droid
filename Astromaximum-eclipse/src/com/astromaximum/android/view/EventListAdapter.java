package com.astromaximum.android.view;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.astromaximum.android.util.Event;

public class EventListAdapter extends ArrayAdapter<Event> {
	private int mKey;
	private long mCustomTime;
	private long mCurrentTime;

	public EventListAdapter(Context context, ArrayList<Event> v, int key, long customTime, long currentTime) {
		super(context, 0, v);
		mKey = key;
		mCustomTime = customTime;
		mCurrentTime = currentTime;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		Event e = getItem(position);
		ViewHolder holder = null;
		if (v == null) {
			SummaryItem si = new SummaryItem(mKey, e);
			holder = ViewHolder.makeHolder(si, false);
			v = ViewHolder.mInflater.inflate(holder.mLayoutId, null);
			holder.initLayout(v);
			v.setTag(holder);
		} else {
			holder = (ViewHolder) v.getTag();
			holder.mSummaryItem.mEvents.set(0, e);
		}
		holder.calculateActiveEvent(mCustomTime, mCurrentTime);
		holder.fillLayout();
		return v;
	}
}
