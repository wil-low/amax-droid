package com.astromaximum.android.view;

import com.astromaximum.android.R;
import com.astromaximum.android.util.Event;

public class VocHolder extends ViewHolder {

	public VocHolder(SummaryItem si) {
		super(si, R.layout.item_voc, LAYOUT_FLAG_TEXT0);
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
