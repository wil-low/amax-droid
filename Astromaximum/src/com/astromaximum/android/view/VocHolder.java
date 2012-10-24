package com.astromaximum.android.view;

import com.astromaximum.android.R;
import com.astromaximum.util.Event;

public class VocHolder extends ViewHolder {

	public VocHolder(SummaryItem si) {
		mLayoutId = R.layout.item_voc;
		mFlags = LAYOUT_FLAG_TEXT0 | LAYOUT_FLAG_TEXT1 | LAYOUT_FLAG_INFO;
	}

	@Override
	public void fillLayout(SummaryItem si) {
		mText0.setText("");
		mText1.setText("");
		int size = si.mEvents.size();
		if (size > 0) {
			Event e = si.normalizeCopy(si.mEvents.elementAt(0));
			mText0.setText(Event.long2String(e.mDate[0], 1, false) + " - "
					+ Event.long2String(e.mDate[1], 1, true));
		}
		if (size > 1) {
			Event e = si.normalizeCopy(si.mEvents.elementAt(1));
			mText1.setText(Event.long2String(e.mDate[0], 1, false) + " - "
					+ Event.long2String(e.mDate[1], 1, true));
		}
		updateInfoButton(si.getActiveEvent());
	}
}
