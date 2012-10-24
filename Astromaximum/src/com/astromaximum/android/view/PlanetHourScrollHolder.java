package com.astromaximum.android.view;

import android.view.View;

import com.astromaximum.util.Event;

public class PlanetHourScrollHolder extends ScrollableHolder {

	public PlanetHourScrollHolder(SummaryItem si) {
		super(si);
	}

	@Override
	protected View makeChildHolder(Event e) {
		ViewHolder holder = new PlanetHourHolder(e);
		View v = mInflater.inflate(holder.mLayoutId, null);
		holder.initLayout(v);
		holder.fillLayout(null);
		v.setTag(e);

		return v;
	}
}
