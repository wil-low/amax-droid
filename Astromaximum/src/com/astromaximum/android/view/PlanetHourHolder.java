package com.astromaximum.android.view;

import com.astromaximum.android.R;
import com.astromaximum.util.AstroFont;
import com.astromaximum.util.Event;

public class PlanetHourHolder extends ViewHolder {

	public PlanetHourHolder(SummaryItem si) {
		mLayoutId = R.layout.item_planet_hour;
		mFlags = LAYOUT_FLAG_TEXT0 | LAYOUT_FLAG_PLANET0 | LAYOUT_FLAG_INFO;
	}

	@Override
	public void fillLayout(SummaryItem si) {
		Event ev = si.getActiveEvent();
		if (ev != null) {
			mPlanet0.setText(AstroFont.getSymbol(AstroFont.TYPE_PLANET,
					ev.mPlanet0));
			Event e = si.normalizeCopy(ev);
			mText0.setText(Event.long2String(e.mDate[0], 1, false) + " - "
					+ Event.long2String(e.mDate[1], 1, true));
		} else {
			mPlanet0.setText("");
			mText0.setText("");
		}
		updateInfoButton(ev);
	}
}
