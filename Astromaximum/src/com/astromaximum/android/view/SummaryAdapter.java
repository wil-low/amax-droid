package com.astromaximum.android.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.astromaximum.android.R;
import com.astromaximum.util.Event;

public class SummaryAdapter extends ArrayAdapter<SummaryItem> {

	public SummaryAdapter(Context context, SummaryItem[] arr) {
		super(context, 0, arr);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		SummaryItem si = getItem(position);
		if (v == null) {
			v = ViewHolder.makeView(si);
		}
		ViewHolder holder = (ViewHolder) v.getTag();
		holder.fillLayout(si);
		return v;
	}
}
