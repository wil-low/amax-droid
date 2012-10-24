package com.astromaximum.android.view;

import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.astromaximum.android.R;
import com.astromaximum.util.Event;

public abstract class ScrollableHolder extends ViewHolder {

	private HorizontalScrollView mScroll;
	protected LinearLayout mLayout;

	public ScrollableHolder(SummaryItem si) {
		mLayoutId = R.layout.item_horizontal_scroll;
	}

	@Override
	protected void initLayout(View v) {
		mScroll = (HorizontalScrollView) v
				.findViewById(R.id.HorizontalScrollView);
		mLayout = (LinearLayout) v.findViewById(R.id.LinearLayout);
	}

	@Override
	public void fillLayout(SummaryItem si) {
		if (!si.mEvents.isEmpty()) {
			for (Event e : si.mEvents) {
				View v = makeChildHolder(e);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
						mLayout.getLayoutParams());
				params.setMargins(10, 0, 10, 0);
				mLayout.addView(v, params);
				v.setOnClickListener(this);
			}
		}
	}

	abstract protected View makeChildHolder(Event e);
}
