package com.astromaximum.android.view;

import com.astromaximum.android.R;
import com.astromaximum.util.Event;

public class TithiHolder extends ViewHolder {

	public TithiHolder(SummaryItem si) {
		super(si, R.layout.item_tithi, LAYOUT_FLAG_TEXT0 | LAYOUT_FLAG_DEGREE
				| LAYOUT_FLAG_INFO);
	}

	@Override
	public void fillLayout() {
		Event e = getActiveEvent();
		if (e != null) {
			mDegree.setText(Integer.toString(e.getDegree()));
			mText0.setText(e.normalizedRangeString());
			mText0.setTextColor(e == mActiveEvent ? mBlueMarkColor : mDefaultTextColor);
		} else {
			mDegree.setText("");
			mText0.setText("");
		}
		updateInfoButton(mSummaryItem);
	}
}
