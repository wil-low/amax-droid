package com.astromaximum.android.view;

import com.astromaximum.android.R;
import com.astromaximum.util.Event;

public class SimpleHolder extends ViewHolder {

	public SimpleHolder(SummaryItem si) {
		mLayoutId = R.layout.event_list_item;
		mFlags = LAYOUT_FLAG_CAPTION | LAYOUT_FLAG_TEXT0 | LAYOUT_FLAG_TEXT1
				| LAYOUT_FLAG_PLANET0 | LAYOUT_FLAG_PLANET1 | LAYOUT_FLAG_INFO;
	}

	@Override
	public void fillLayout(SummaryItem si) {
		Event e = si.getActiveEvent();
		if (e != null) {
			mText0.setText(Event.long2String(e.getDate0(), 1, true) + " - "
					+ Event.long2String(e.getDate1(), 1, true));
		} else {
			mText0.setText("");
		}
		updateInfoButton(e);
	}

}
