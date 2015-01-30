package com.astromaximum.android.view;

import com.astromaximum.android.R;
import com.astromaximum.android.util.Event;

public class TithiHolder extends ViewHolder {

	public TithiHolder(SummaryItem si) {
		super(si, R.layout.item_tithi, LAYOUT_FLAG_TEXT0 | LAYOUT_FLAG_DEGREE);
	}

	@Override
	public void fillLayout() {
		Event e = getActiveEvent();
		if (e != null) {
			mDegree.setText(Integer.toString(e.getDegree()));
			mText0.setText(e.normalizedRangeString());
			setColorByEventMode(mText0, e);
		} else {
			mDegree.setText("");
			mText0.setText("");
		}
	}
}
