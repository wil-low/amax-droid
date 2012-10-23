package com.astromaximum.android.view;

import com.astromaximum.android.R;
import com.astromaximum.util.Event;

public class VocHolder extends ViewHolder {

	public VocHolder(SummaryItem si) {
		mLayoutId = R.layout.item_voc;
		mFlags = LAYOUT_FLAG_TEXT0 | LAYOUT_FLAG_INFO;
	}

	@Override
	public void fillLayout(SummaryItem si) {
		Event e = si.getActiveEvent();
		if (e != null) {
			mText0.setText(Event.long2String(e.mDate[0], 1, true) + " - " + Event.long2String(e.mDate[1], 1, true));
		}
		else {
			mText0.setText("");
		}
		updateInfoButton(e);
	}
}
