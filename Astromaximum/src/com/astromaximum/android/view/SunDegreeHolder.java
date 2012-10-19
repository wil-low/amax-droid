package com.astromaximum.android.view;

import com.astromaximum.android.R;
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
			mText0.setText(Event.long2String(e.getDate0(), 1, true) + " - "
					+ Event.long2String(e.getDate1(), 1, true));
			mText1.setText((e.getDegree() % 30 + 1) + "°");
			setImage(mZodiac, R.drawable.z00 + e.getDegree() / 30);
			setImage(mPlanet0, R.drawable.p13 + e.getPlanet0());
		} else {
			mText0.setText("");
			mText1.setText("");
			clearImage(mZodiac);
			clearImage(mPlanet0);
		}
		updateInfoButton(e);
	}
}
