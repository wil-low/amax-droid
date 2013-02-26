package com.astromaximum.android.view;

import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.astromaximum.android.R;
import com.astromaximum.android.util.Event;

public abstract class ScrollableHolder extends ViewHolder {

	private HorizontalScrollView mScroll;
	protected LinearLayout mLayout;

	public ScrollableHolder(SummaryItem si) {
		super(si, R.layout.item_horizontal_scroll, 0);
	}

	@Override
	protected void initLayout(View v) {
		mScroll = (HorizontalScrollView) v
				.findViewById(R.id.HorizontalScrollView);
		mLayout = (LinearLayout) v.findViewById(R.id.LinearLayout);
		mInfo = (ImageView) v.findViewById(R.id.ShowEventList);
		if (mInfo != null)
			mInfo.setOnClickListener(this);
	}

	@Override
	public void fillLayout() {
		if (!mSummaryItem.mEvents.isEmpty()) {
			for (Event e : mSummaryItem.mEvents) {
				View v = makeChildHolder(e);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
						mLayout.getLayoutParams());
				//params.setMargins(10, 0, 10, 0);
				mLayout.addView(v, params);
				v.setOnClickListener(this);
			}
		}
		if (mSummaryItem.mEvents.size() > 1) {
			mInfo.setVisibility(View.VISIBLE);
			mInfo.setTag(mSummaryItem);
		} else {
			mInfo.setVisibility(View.INVISIBLE);
			mInfo.setTag(null);
		}
	}

	abstract protected View makeChildHolder(Event e);

}
