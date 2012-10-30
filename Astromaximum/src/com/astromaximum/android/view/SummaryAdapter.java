package com.astromaximum.android.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class SummaryAdapter extends ArrayAdapter<SummaryItem> {
	private long mNow;

	public SummaryAdapter(Context context, SummaryItem[] arr, long now) {
		super(context, 0, arr);
		mNow = now;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		SummaryItem si = getItem(position);
		if (v == null) {
			v = ViewHolder.makeView(si);
		}
		ViewHolder holder = (ViewHolder) v.getTag();
		holder.calculateActiveEvent(si, mNow);
		holder.fillLayout(si);
		return v;
	}
}
