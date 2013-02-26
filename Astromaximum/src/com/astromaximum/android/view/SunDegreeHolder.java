package com.astromaximum.android.view;

import com.astromaximum.android.R;
import com.astromaximum.android.util.AstroFont;
import com.astromaximum.android.util.Event;

public class SunDegreeHolder extends ViewHolder {

	public SunDegreeHolder(SummaryItem si) {
		super(si, R.layout.item_sun_degree, LAYOUT_FLAG_TEXT0
				| LAYOUT_FLAG_ZODIAC | LAYOUT_FLAG_TEXT1 | LAYOUT_FLAG_PLANET0
				| LAYOUT_FLAG_INFO);
	}

	@Override
	public void fillLayout() {
		Event e = getActiveEvent();
		if (e != null) {
			mText0.setText(e.normalizedRangeString());
			mText1.setText((e.getDegree() % 30 + 1) + "\u00b0");
			mZodiac.setText(AstroFont.getSymbol(AstroFont.TYPE_ZODIAC,
					e.getDegree() / 30));
			mPlanet0.setText(AstroFont.getSymbol(AstroFont.TYPE_PLANET,
					e.mPlanet0));
			setColorByEventMode(mText0, e);
		} else {
			mText0.setText("");
			mText1.setText("");
			mZodiac.setText("");
			mPlanet0.setText("");
		}
		updateInfoButton(mSummaryItem);
	}
}
