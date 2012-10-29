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
		if (mActiveEvent != null)
			mText0.setText(Event.long2String(mActiveEvent.mDate[0], 1, false) + " - " + Event.long2String(mActiveEvent.mDate[1], 1, true));
		else
			mText0.setText("");
		updateInfoButton(si);
	}
}
