package com.astromaximum.android.view;

import android.view.View;
import android.widget.TextView;

import com.astromaximum.util.Event;

public class AspectScrollHolder extends ScrollableHolder {

	public AspectScrollHolder(SummaryItem si) {
		super(si);
	}

	@Override
	protected View makeChildHolder(Event e) {
//		TextView v = new TextView(mContext);
//		v.setText("OOK");
		
		ViewHolder holder = new AspectHolder(e);
		View v = mInflater.inflate(holder.mLayoutId, null);
		holder.initLayout(v);
		holder.fillLayout(null);
		v.setTag(e);

		return v;
	}
}
