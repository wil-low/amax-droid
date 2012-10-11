package com.astromaximum.android.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.astromaximum.android.R;
import com.astromaximum.util.Event;

public class SummaryAdapter extends ArrayAdapter<SummaryItem> {

	public SummaryAdapter(Context context, SummaryItem[] arr) {
		super(context, R.layout.event_list_item, arr);
	}

	public View getView (int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.event_list_item, null);
        }
        SummaryItem si = getItem(position);
        if (!si.mEvents.isEmpty()) {
	        Event e = si.mEvents.get(0);
	        TextView tv = (TextView) v;
			tv.setText(e.toString());
        	tv.setEnabled(true);
        }
        else {
	        TextView tv = (TextView) v;
        	tv.setText(si.mKey);
        	tv.setEnabled(false);
        }
		return v;
	}
}
