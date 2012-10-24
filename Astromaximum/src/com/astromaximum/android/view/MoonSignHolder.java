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
			if (!si.isWholeDay(e)) {
				mText0.setText(Event.long2String(e.mDate[0], 1, false));
			} else {
				mText0.setText("");
			}
			mZodiac.setText(AstroFont.getSymbol(AstroFont.TYPE_ZODIAC, e.getDegree()));
			mPlanet0.setText(AstroFont.getSymbol(AstroFont.TYPE_PLANET, e.mPlanet0));
		} else {
			mText0.setText("");
			mZodiac.setText("");
			mPlanet0.setText("");
		}
		updateInfoButton(e);
	}
}
