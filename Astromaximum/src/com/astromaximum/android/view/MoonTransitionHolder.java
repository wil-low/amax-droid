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
		if (mActiveEvent != null) {
			Log.d("TAG", mActiveEvent.toString());
			mPlanet0.setText(AstroFont.getSymbol(AstroFont.TYPE_PLANET,
					mActiveEvent.mPlanet0));
			if (mActiveEvent.mPlanet1 == -1) { // VOC
				mPlanet1.setVisibility(View.GONE);
				mText1.setVisibility(View.VISIBLE);
			} else {
				mPlanet1.setText(AstroFont.getSymbol(AstroFont.TYPE_PLANET,
						mActiveEvent.mPlanet1));
				mPlanet1.setVisibility(View.VISIBLE);
				mText1.setVisibility(View.GONE);
			}
			mText0.setText(Event.long2String(mActiveEvent.mDate[0], 1, false) + " - "
					+ Event.long2String(mActiveEvent.mDate[1], 1, true));
		} else {
			mPlanet0.setText("");
			mPlanet1.setText("");
			mText0.setText("");
			mText1.setText("");
			mPlanet1.setVisibility(View.GONE);
			mText1.setVisibility(View.GONE);
		}
		updateInfoButton(si);
	}

	@Override
	public void calculateActiveEvent(SummaryItem si, long now) {
		mActiveEvent = null;
		for (Event e : si.mEvents) {
			if (e.mEvtype == Event.EV_MOON_MOVE && Event.dateBetween(now, e.mDate[0], e.mDate[1]) == mBetweenCheckValue) {
				mActiveEvent = SummaryItem.normalizeCopy(e);
				Log.d("MoonTrans", mActiveEvent.toString());
				break;
			}
		}
	}
}
