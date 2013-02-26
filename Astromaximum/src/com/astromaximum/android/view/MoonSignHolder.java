package com.astromaximum.android.view;

import com.astromaximum.android.R;
import com.astromaximum.android.util.AstroFont;
import com.astromaximum.android.util.Event;

public class MoonSignHolder extends ViewHolder {

	public MoonSignHolder(SummaryItem si) {
		super(si, R.layout.item_moon_sign, LAYOUT_FLAG_TEXT0
				| LAYOUT_FLAG_PLANET0 | LAYOUT_FLAG_ZODIAC | LAYOUT_FLAG_INFO);
	}

	@Override
	public void fillLayout() {
		Event e = getActiveEvent();
		if (e != null) {
			mText0.setText(e.normalizedRangeString());
			mZodiac.setText(AstroFont.getSymbol(AstroFont.TYPE_ZODIAC,
					e.getDegree()));
			mPlanet0.setText(AstroFont.getSymbol(AstroFont.TYPE_PLANET,
					e.mPlanet0));
			setColorByEventMode(mText0, e);
		} else {
			mText0.setText("");
			mZodiac.setText("");
			mPlanet0.setText("");
		}
		updateInfoButton(mSummaryItem);
	}
}
