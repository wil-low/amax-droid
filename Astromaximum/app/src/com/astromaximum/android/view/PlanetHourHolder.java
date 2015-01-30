package com.astromaximum.android.view;

import com.astromaximum.android.R;
import com.astromaximum.android.util.AstroFont;
import com.astromaximum.android.util.Event;

public class PlanetHourHolder extends ViewHolder {

	public PlanetHourHolder(SummaryItem si) {
		super(si, R.layout.item_planet_hour, LAYOUT_FLAG_TEXT0
				| LAYOUT_FLAG_PLANET0);
	}

	@Override
	public void fillLayout() {
		Event e = getActiveEvent();
		if (e != null) {
			mPlanet0.setText(AstroFont.getSymbol(AstroFont.TYPE_PLANET,
					e.mPlanet0));
			mText0.setText(e.normalizedRangeString());
			setColorByEventMode(mText0, e);
		} else {
			mPlanet0.setText("");
			mText0.setText("");
		}
	}
}
