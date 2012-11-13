package com.astromaximum.android.view;

import com.astromaximum.android.R;
import com.astromaximum.util.AstroFont;
import com.astromaximum.util.Event;

public class SunMoonDayHolder extends ViewHolder {

	public SunMoonDayHolder(SummaryItem si) {
		super(si, R.layout.item_sun_moon_day, LAYOUT_FLAG_TEXT0
				| LAYOUT_FLAG_TEXT1 | LAYOUT_FLAG_PLANET0 | LAYOUT_FLAG_INFO);
	}

	@Override
	public void fillLayout() {
		Event e = getActiveEvent();
		if (e != null) {
			mPlanet0.setText(AstroFont.getSymbol(AstroFont.TYPE_PLANET,
					e.mPlanet0));
			mText0.setText(e.normalizedRangeString());
			mText0.setTextColor(e == mActiveEvent ? mBlueMarkColor
					: mDefaultTextColor);
			int day = e.getDegree();
			if (day >= 360)
				day = 359 - day;
			mText1.setText(mContext.getResources().getString(R.string.day)
					+ " " + day);

		} else {
			mPlanet0.setText("");
			mText0.setText("");
			mText1.setText("");
		}
		updateInfoButton(mSummaryItem);
	}
}
