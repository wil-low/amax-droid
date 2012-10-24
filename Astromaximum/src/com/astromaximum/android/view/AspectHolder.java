package com.astromaximum.android.view;

import com.astromaximum.android.R;
import com.astromaximum.util.AstroFont;
import com.astromaximum.util.Event;

public class AspectHolder extends ViewHolder {
	Event mEvent;

	public AspectHolder(Event e) {
		mLayoutId = R.layout.item_aspect;
		mFlags = LAYOUT_FLAG_PLANET0 | LAYOUT_FLAG_ASPECT | LAYOUT_FLAG_PLANET1 | LAYOUT_FLAG_TEXT0;
		mEvent = e;
	}

	@Override
	public void fillLayout(SummaryItem si) {
		mPlanet0.setText(AstroFont.getSymbol(AstroFont.TYPE_PLANET, mEvent.mPlanet0));
		mAspect.setText(AstroFont.getSymbol(AstroFont.TYPE_ASPECT, mEvent.getDegree()));
		mPlanet1.setText(AstroFont.getSymbol(AstroFont.TYPE_PLANET, mEvent.mPlanet1));
		mText0.setText(Event.long2String(mEvent.mDate[0], 1, false));
	}
}
