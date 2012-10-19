package com.astromaximum.android.view;

import com.astromaximum.android.R;
import com.astromaximum.util.Event;

public class AspectHolder extends ViewHolder {
	Event mEvent;

	public AspectHolder(Event e) {
		mLayoutId = R.layout.item_aspect;
		mFlags = LAYOUT_FLAG_PLANET0 | LAYOUT_FLAG_ASPECT | LAYOUT_FLAG_PLANET1;
		mEvent = e;
	}

	@Override
	public void fillLayout(SummaryItem si) {
		setImage(mPlanet0, R.drawable.p13 + mEvent.getPlanet0());
		setImage(mAspect,
				R.drawable.a00 + Event.ASPECT_MAP.get(mEvent.getDegree()));
		setImage(mPlanet1, R.drawable.p13 + mEvent.getPlanet1());
	}
}
