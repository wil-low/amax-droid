package com.astromaximum.android.view;

import com.astromaximum.android.R;
import com.astromaximum.util.AstroFont;
import com.astromaximum.util.Event;

public class TithiHolder extends ViewHolder {

	public TithiHolder(SummaryItem si) {
		mLayoutId = R.layout.item_tithi;
		mFlags = LAYOUT_FLAG_TEXT0 | LAYOUT_FLAG_DEGREE | LAYOUT_FLAG_INFO;
	}

	@Override
	public void fillLayout(SummaryItem si) {
		if (mActiveEvent != null) {
			mDegree.setText(Integer.toString(mActiveEvent.getDegree()));
			Event e = si.normalizeCopy(mActiveEvent);
			mText0.setText(Event.long2String(e.mDate[0], 1, false) + " - "
					+ Event.long2String(e.mDate[1], 1, true));
		} else {
			mDegree.setText("");
			mText0.setText("");
		}
		updateInfoButton(si);
	}
}
