package com.astromaximum.android.view;

import android.util.Log;

import com.astromaximum.android.R;
import com.astromaximum.util.AstroFont;
import com.astromaximum.util.Event;

public class PlanetHourHolder extends ViewHolder {
	Event mEvent;

	public PlanetHourHolder(Event e) {
		mLayoutId = R.layout.item_planet_hour;
		mFlags = LAYOUT_FLAG_PLANET0 | LAYOUT_FLAG_TEXT0;
		mEvent = e;
	}

	@Override
	public void fillLayout(SummaryItem si) {
		mPlanet0.setText(AstroFont.getSymbol(AstroFont.TYPE_PLANET,
				mEvent.mPlanet0));
		String str = Event.long2String(mEvent.mDate[0], 1, false);
		mText0.setText(str);
	}
}
