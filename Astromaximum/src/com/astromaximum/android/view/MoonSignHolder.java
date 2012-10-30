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
		if (mActiveEvent != null) {
			mText0.setText(Event.long2String(mActiveEvent.mDate[0], 1, false) + " - "
					+ Event.long2String(mActiveEvent.mDate[1], 1, true));
			mZodiac.setText(AstroFont.getSymbol(AstroFont.TYPE_ZODIAC, mActiveEvent.getDegree()));
			mPlanet0.setText(AstroFont.getSymbol(AstroFont.TYPE_PLANET, mActiveEvent.mPlanet0));
		} else {
			mText0.setText("");
			mZodiac.setText("");
			mPlanet0.setText("");
		}
		updateInfoButton(si);
	}
}
