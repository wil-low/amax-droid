package com.astromaximum.android.view;

import android.view.View;

import com.astromaximum.util.Event;

public class AspectScrollHolder extends ScrollableHolder {

	public AspectScrollHolder(SummaryItem si) {
		super(si);
	}

	@Override
	protected View makeChildHolder(Event e) {
		ViewHolder holder = new AspectHolder(new SummaryItem(mSummaryItem.mKey, e));
		View v = mInflater.inflate(holder.mLayoutId, null);
		holder.mActiveEvent = e;
		holder.initLayout(v);
		holder.fillLayout();
		v.setTag(holder);
		return v;
	}
}
