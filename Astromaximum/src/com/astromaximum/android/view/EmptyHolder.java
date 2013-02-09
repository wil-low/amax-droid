package com.astromaximum.android.view;

import com.astromaximum.android.R;
import com.astromaximum.util.Event;

public class EmptyHolder extends ViewHolder {

	public EmptyHolder(SummaryItem si) {
		super(si, R.layout.item_empty, LAYOUT_FLAG_TEXT0);
	}

	@Override
	public void fillLayout() {
		int textId = 0;
		switch (mSummaryItem.mKey) {
		case Event.EV_VOC:
			textId = R.string.no_voc;
			break;
		case Event.EV_VIA_COMBUSTA:
			textId = R.string.no_vc;
			break;
		case Event.EV_ASP_EXACT:
			textId = R.string.no_aspects;
			break;
		case Event.EV_RETROGRADE:
			textId = R.string.no_retrograde;
			break;
		}
		if (textId == 0)
			mText0.setText("");
		else
			mText0.setText(textId);
	}
}
