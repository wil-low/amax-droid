package com.astromaximum.android.view;

import com.astromaximum.android.R;
import com.astromaximum.util.AstroFont;
import com.astromaximum.util.Event;

public class MoonSignHolder extends ViewHolder {

	public MoonSignHolder(SummaryItem si) {
		mLayoutId = R.layout.item_moon_sign;
		mFlags = LAYOUT_FLAG_TEXT0 | LAYOUT_FLAG_PLANET0 | LAYOUT_FLAG_ZODIAC | LAYOUT_FLAG_INFO;
	}

	@Override
	public void fillLayout(SummaryItem si) {
		Event e = si.getActiveEvent();
		if (e != null) {
			mText0.setText(Event.long2String(e.getDate0(), 1, true) + " - "
					+ Event.long2String(e.getDate1(), 1, true));
			mZodiac.setText(AstroFont.getSymbol(AstroFont.TYPE_ZODIAC, e.getDegree() / 30));
			mPlanet0.setText(AstroFont.getSymbol(AstroFont.TYPE_PLANET, e.getPlanet0()));
		} else {
			mText0.setText("");
			mZodiac.setText("");
			mPlanet0.setText("");
		}
		updateInfoButton(e);
	}
}
