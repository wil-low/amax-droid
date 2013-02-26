package com.astromaximum.android.view;

import android.view.View;

import com.astromaximum.android.R;
import com.astromaximum.android.util.AstroFont;
import com.astromaximum.android.util.Event;

public class RetrogradeHolder extends ViewHolder {
	public RetrogradeHolder(SummaryItem si) {
		super(si, R.layout.item_retrograde, LAYOUT_FLAG_PLANET0
				| LAYOUT_FLAG_TEXT0);
	}

	@Override
	public void fillLayout() {
		Event e = getActiveEvent();
		if (e != null) {
			mPlanet0.setText(AstroFont.getSymbol(AstroFont.TYPE_PLANET,
					e.mPlanet0) + AstroFont.getSymbol(AstroFont.TYPE_RETROGRADE, 0));
			if (mIsSummaryMode) {
				mText0.setVisibility(View.GONE);
			} else {
				mText0.setText(Event.long2String(e.mDate[0],
						Event.mMonthAbbrDayDateFormat, false) + "\n" + 
						Event.long2String(e.mDate[1],
								Event.mMonthAbbrDayDateFormat, false));
				setColorByEventMode(mText0, e);
			}
		}
	}
}
