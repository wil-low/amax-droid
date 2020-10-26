package com.astromaximum.android.view;

import android.view.View;

import com.astromaximum.android.R;
import com.astromaximum.android.util.AstroFont;
import com.astromaximum.android.util.Event;

public class AspectHolder extends ViewHolder {
	public AspectHolder(SummaryItem si) {
		super(si, R.layout.item_aspect, LAYOUT_FLAG_PLANET0
				| LAYOUT_FLAG_ASPECT | LAYOUT_FLAG_PLANET1 | LAYOUT_FLAG_TEXT0);
	}

	@Override
	public void fillLayout() {
		Event e = getActiveEvent();
		if (e != null) {
			mPlanet0.setText(AstroFont.getSymbol(AstroFont.TYPE_PLANET,
					e.mPlanet0));
			mAspect.setText(AstroFont.getSymbol(AstroFont.TYPE_ASPECT,
					e.getDegree()));
			mPlanet1.setText(AstroFont.getSymbol(AstroFont.TYPE_PLANET,
					e.mPlanet1));
			if (mIsSummaryMode) {
				mText0.setVisibility(View.GONE);
			} else {
				mText0.setText(e.long2String(e.mDate[0], null, false));
				setColorByEventMode(mText0, e);
			}
		}
	}
}
