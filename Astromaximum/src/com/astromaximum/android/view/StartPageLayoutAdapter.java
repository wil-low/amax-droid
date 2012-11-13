package com.astromaximum.android.view;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.astromaximum.android.R;
import com.astromaximum.util.StartPageItem;

public class StartPageLayoutAdapter extends ArrayAdapter<StartPageItem> {

	private static final String TAG = "StartPageLayoutAdapter";

	public StartPageLayoutAdapter(Context context, ArrayList<StartPageItem> v) {
		super(context, 0, v);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		StartPageItem item = getItem(position);
		StartPageLayoutHolder holder = null;
		if (v == null) {
			holder = new StartPageLayoutHolder();
			v = ViewHolder.mInflater.inflate(R.layout.item_start_page_layout, null);
			holder.initLayout(v);
			v.setTag(holder);
		} else {
			holder = (StartPageLayoutHolder) v.getTag();
		}
		holder.fillLayout(item);
		return v;
	}
}
