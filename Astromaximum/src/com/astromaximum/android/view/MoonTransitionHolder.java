package com.astromaximum.android.view;

import android.view.View;
import android.widget.TextView;

import com.astromaximum.android.R;
import com.astromaximum.util.AstroFont;
import com.astromaximum.util.DataProvider;
import com.astromaximum.util.Event;

public class MoonTransitionHolder extends ViewHolder {
	private TextView mTransitionSignView;

	public MoonTransitionHolder(SummaryItem si) {
		super(si, R.layout.item_moon_transition, LAYOUT_FLAG_PLANET0
				| LAYOUT_FLAG_PLANET1 | LAYOUT_FLAG_TEXT0 | LAYOUT_FLAG_TEXT1
				| LAYOUT_FLAG_INFO);
	}

	@Override
	protected void initLayout(View v) {
		super.initLayout(v);
		mTransitionSignView = (TextView) v
				.findViewById(R.id.EventListTransitionSign);
	}

	@Override
	public void fillLayout() {
		Event e = getActiveEvent();
		if (e != null) {
			// MyLog.d("MoonTransitionHolder", e.toString());
			mText0.setTextColor(mDefaultTextColor);
			switch (e.mEvtype) {
			case Event.EV_MOON_MOVE:
				mPlanet0.setText(AstroFont.getSymbol(AstroFont.TYPE_PLANET,
						e.mPlanet0 == -1 ? Event.SE_MOON : e.mPlanet0));
				if (e.mPlanet1 == -1) { // VOC
					mPlanet1.setVisibility(View.GONE);
					mText1.setVisibility(View.VISIBLE);
				} else {
					mPlanet1.setText(AstroFont.getSymbol(AstroFont.TYPE_PLANET,
							e.mPlanet1));
					mPlanet1.setVisibility(View.VISIBLE);
					mText1.setVisibility(View.GONE);
				}
				mText0.setText(e.normalizedRangeString());
				if (e == mActiveEvent)
					mText0.setTextColor(mBlueMarkColor);
				mPlanet0.setVisibility(View.VISIBLE);
				mTransitionSignView.setVisibility(View.VISIBLE);
				break;
			case Event.EV_ASP_EXACT_MOON:
				mPlanet0.setText(AstroFont.getSymbol(AstroFont.TYPE_PLANET,
						e.mPlanet0)
						+ " "
						+ AstroFont.getSymbol(AstroFont.TYPE_ASPECT,
								e.getDegree())
						+ " "
						+ AstroFont
								.getSymbol(AstroFont.TYPE_PLANET, e.mPlanet1));
				mPlanet1.setVisibility(View.GONE);
				mText1.setVisibility(View.GONE);
				mText0.setText(Event.long2String(e.mDate[0], DataProvider
						.getInstance().isInCurrentDay(e.mDate[0]) ? null
						: Event.mMonthAbbrDayDateFormat, true));
				mPlanet0.setVisibility(View.VISIBLE);
				mTransitionSignView.setVisibility(View.GONE);
				break;
			case Event.EV_SIGN_ENTER:
				mPlanet1.setText(AstroFont.getSymbol(AstroFont.TYPE_ZODIAC,
						e.getDegree()));
				mPlanet0.setVisibility(View.GONE);
				mPlanet1.setVisibility(View.VISIBLE);
				mText1.setVisibility(View.GONE);
				mText0.setText(Event.long2String(e.mDate[0], DataProvider
						.getInstance().isInCurrentDay(e.mDate[0]) ? null
						: Event.mMonthAbbrDayDateFormat, true));
				mTransitionSignView.setVisibility(View.GONE);
				break;
			}
		} else {
			mPlanet0.setText("");
			mPlanet1.setText("");
			mText0.setText("");
			mText1.setText("");
			mPlanet1.setVisibility(View.GONE);
			mText1.setVisibility(View.GONE);
			mTransitionSignView.setVisibility(View.GONE);
		}
		updateInfoButton(mSummaryItem);
	}

	@Override
	public void calculateActiveEvent(long now) {
		mActiveEvent = null;
		for (Event e : mSummaryItem.mEvents) {
			if (e.mEvtype == Event.EV_MOON_MOVE
					&& Event.dateBetween(now, e.mDate[0], e.mDate[1]) == 0) {
				mActiveEvent = e;
				// MyLog.d("MoonTrans", mActiveEvent.toString());
				break;
			}
		}
	}
}
