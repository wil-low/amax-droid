package com.astromaximum.android.view;

import com.astromaximum.android.R;
import com.astromaximum.android.util.Event;

public class VcHolder extends ViewHolder {

	public VcHolder(SummaryItem si) {
		super(si, R.layout.item_vc, LAYOUT_FLAG_TEXT0);
	}

	@Override
	public void fillLayout() {
		Event e = getActiveEvent();
		if (e != null) {
			mText0.setText(e.normalizedRangeString());
			setColorByEventMode(mText0, e);
		} else {
			mText0.setText("");
		}
	}
}
