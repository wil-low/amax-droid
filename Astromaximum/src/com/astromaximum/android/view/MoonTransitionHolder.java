package com.astromaximum.android.view;

import android.util.Log;
import android.view.View;

import com.astromaximum.android.R;
import com.astromaximum.util.AstroFont;
import com.astromaximum.util.Event;

public class MoonTransitionHolder extends ViewHolder {

	public MoonTransitionHolder(SummaryItem si) {
		mLayoutId = R.layout.item_moon_transition;
		mFlags = LAYOUT_FLAG_PLANET0 | LAYOUT_FLAG_PLANET1 | LAYOUT_FLAG_TEXT0
				| LAYOUT_FLAG_TEXT1 | LAYOUT_FLAG_INFO;
	}

	@Override
	public void fillLayout(SummaryItem si) {
		Event e = null;
		for (Event evt : si.mEvents) {
			if (evt.mEvtype == Event.EV_MOON_MOVE) {
				e = evt;
				break;
			}
		}
		if (e != null) {
			Log.d("TAG", e.toString());
			mPlanet0.setText(AstroFont.getSymbol(AstroFont.TYPE_PLANET,
					e.mPlanet0));
			if (e.mPlanet1 == -1) { // VOC
				mPlanet1.setVisibility(View.GONE);
				mText1.setVisibility(View.VISIBLE);
			} else {
				mPlanet1.setText(AstroFont.getSymbol(AstroFont.TYPE_PLANET,
						e.mPlanet1));
				mPlanet1.setVisibility(View.VISIBLE);
				mText1.setVisibility(View.GONE);
			}
			mText0.setText(Event.long2String(e.mDate[0], 1, false) + " - "
					+ Event.long2String(e.mDate[1], 1, true));
		} else {
			mPlanet0.setText("");
			mPlanet1.setText("");
			mText0.setText("");
			mText1.setText("");
			mPlanet1.setVisibility(View.GONE);
			mText1.setVisibility(View.GONE);
		}
		updateInfoButton(e);
	}

}
