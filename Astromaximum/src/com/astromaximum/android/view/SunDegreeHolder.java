package com.astromaximum.android.view;

import com.astromaximum.android.R;
import com.astromaximum.util.AstroFont;
import com.astromaximum.util.Event;

public class SunDegreeHolder extends ViewHolder {

	public SunDegreeHolder(SummaryItem si) {
		mLayoutId = R.layout.item_sun_degree;
		mFlags = LAYOUT_FLAG_TEXT0 | LAYOUT_FLAG_ZODIAC | LAYOUT_FLAG_TEXT1
				| LAYOUT_FLAG_PLANET0 | LAYOUT_FLAG_INFO;
	}

	@Override
	public void fillLayout(SummaryItem si) {
		Event e = si.getActiveEvent();
		if (e != null) {
			mText0.setText(Event.long2String(e.mDate[0], 1, false));
			mText1.setText((e.getDegree() % 30 + 1) + "°");
			mZodiac.setText(AstroFont.getSymbol(AstroFont.TYPE_ZODIAC, e.getDegree() / 30));
			mPlanet0.setText(AstroFont.getSymbol(AstroFont.TYPE_PLANET, e.mPlanet0));
		} else {
			mText0.setText("");
			mText1.setText("");
			mZodiac.setText("");
			mPlanet0.setText("");
		}
		updateInfoButton(e);
	}
}
