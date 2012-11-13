package com.astromaximum.android.view;

import com.astromaximum.android.R;
import com.astromaximum.util.Event;

public class VocHolder extends ViewHolder {

	public VocHolder(SummaryItem si) {
		super(si, R.layout.item_voc, LAYOUT_FLAG_TEXT0 | LAYOUT_FLAG_INFO);
	}

	@Override
	public void fillLayout() {
		Event e = getActiveEvent();
		if (e != null) {
			mText0.setText(e.normalizedRangeString());
			mText0.setTextColor(e == mActiveEvent ? mBlueMarkColor : mDefaultTextColor);
		} else {
			mText0.setText("");
		}
		updateInfoButton(mSummaryItem);
	}
}
