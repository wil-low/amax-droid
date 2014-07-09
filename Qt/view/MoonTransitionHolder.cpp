package com.astromaximum.android.view;

import android.view.View;
import android.widget.TextView;

import com.astromaximum.android.R;
import com.astromaximum.android.util.AstroFont;
import com.astromaximum.android.util.DataProvider;
import com.astromaximum.android.util.Event;

public class MoonTransitionHolder extends ViewHolder {
	private TextView mTransitionSignView;
	private String mSunSign;

	public MoonTransitionHolder(SummaryItem si) {
		super(si, R.layout.item_moon_transition, LAYOUT_FLAG_PLANET0
				| LAYOUT_FLAG_PLANET1 | LAYOUT_FLAG_TEXT0);
	}

	@Override
	protected void initLayout(View v) {
		super.initLayout(v);
		mTransitionSignView = (TextView) v
				.findViewById(R.id.EventListTransitionSign);
		mSunSign = AstroFont.getSymbol(AstroFont.TYPE_PLANET,
				Event.SE_SUN);
	}

	@Override
	public void fillLayout() {
		Event e = getActiveEvent();
		if (e != null) {
			// MyLog.d("MoonTransitionHolder", e.toString());
			mText0.setTextColor(mDefaultTextColor);
			switch (e.mEvtype) {
			case Event.EV_MOON_MOVE:
				mText0.setText(e.normalizedRangeString());
				setColorByEventMode(mText0, e);
				mPlanet0.setText(mSunSign);
				mPlanet0.setVisibility(View.INVISIBLE);
				mPlanet1.setText(mSunSign);
				mPlanet1.setVisibility(View.INVISIBLE);
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
				mText0.setText(e.long2String(e.mDate[0], DataProvider
						.getInstance().isInCurrentDay(e.mDate[0]) ? null
						: Event.mMonthAbbrDayDateFormat, true));
				mPlanet0.setVisibility(View.VISIBLE);
				mTransitionSignView.setVisibility(View.GONE);
				break;
			case Event.EV_SIGN_ENTER:
				mPlanet0.setText(mSunSign);
				mPlanet0.setVisibility(View.INVISIBLE);
				mPlanet1.setText(AstroFont.getSymbol(AstroFont.TYPE_ZODIAC,
						e.getDegree()));
				mPlanet1.setVisibility(View.VISIBLE);
				mText0.setText(e.long2String(e.mDate[0], DataProvider
						.getInstance().isInCurrentDay(e.mDate[0]) ? null
						: Event.mMonthAbbrDayDateFormat, true));
				mTransitionSignView.setVisibility(View.GONE);
				break;
			}
		} else {
			mPlanet0.setText("");
			mPlanet1.setText("");
			mText0.setText("");
			mPlanet1.setVisibility(View.GONE);
			mTransitionSignView.setVisibility(View.GONE);
		}
	}
}
