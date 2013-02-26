package com.astromaximum.android.view;

import android.view.View;

import com.astromaximum.android.util.Event;

public class RetrogradeScrollHolder extends ScrollableHolder {

	public RetrogradeScrollHolder(SummaryItem si) {
		super(si);
	}

	@Override
	protected View makeChildHolder(Event e) {
		ViewHolder holder = new RetrogradeHolder(new SummaryItem(mSummaryItem.mKey, e));
		View v = mInflater.inflate(holder.mLayoutId, null);
		holder.mActiveEvent = e;
		holder.initLayout(v);
		holder.fillLayout();
		v.setTag(holder);
		return v;
	}
}
