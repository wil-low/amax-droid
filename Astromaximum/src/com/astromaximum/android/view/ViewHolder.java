package com.astromaximum.android.view;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.astromaximum.android.EventListActivity;
import com.astromaximum.android.InterpreterActivity;
import com.astromaximum.android.PreferenceUtils;
import com.astromaximum.android.R;
import com.astromaximum.android.util.Event;
import com.astromaximum.android.util.InterpretationProvider;

public abstract class ViewHolder implements OnClickListener {
	private static final String TAG = "ViewHolder";
	protected TextView mType;
	protected TextView mText0;
	protected TextView mText1;
	protected AstroTextView mPlanet0;
	protected AstroTextView mPlanet1;
	protected AstroTextView mAspect;
	protected ImageView mInfo;
	protected AstroTextView mZodiac;
	protected TextView mDegree;

	protected int mLayoutId;
	protected int mFlags;

	protected static LayoutInflater mInflater;
	private static Resources mResources;

	protected static int LAYOUT_FLAG_CAPTION = 1 << 0;
	protected static int LAYOUT_FLAG_TEXT0 = 1 << 1;
	protected static int LAYOUT_FLAG_TEXT1 = 1 << 2;
	protected static int LAYOUT_FLAG_PLANET0 = 1 << 3;
	protected static int LAYOUT_FLAG_PLANET1 = 1 << 4;
	protected static int LAYOUT_FLAG_DEGREE = 1 << 5;
	protected static int LAYOUT_FLAG_ASPECT = 1 << 6;
	protected static int LAYOUT_FLAG_INFO = 1 << 7;
	protected static int LAYOUT_FLAG_ZODIAC = 1 << 8;

	protected static Context mContext;

	protected static int mDefaultTextColor;
	protected static int mBlueMarkColor;
	protected static int mRedMarkColor;

	protected SummaryItem mSummaryItem;
	public Event mActiveEvent;
	protected boolean mIsSummaryMode = true;

	public ViewHolder(SummaryItem si, int layoutId, int flags) {
		mSummaryItem = si;
		mLayoutId = layoutId;
		mFlags = flags;
	}

	public static ViewHolder makeHolder(SummaryItem si, boolean isSummaryMode) {
		ViewHolder holder = null;

		if (si.mEvents.isEmpty())
			return new EmptyHolder(si);

		switch (si.mKey) {
		case Event.EV_VOC:
			holder = new VocHolder(si);
			break;
		case Event.EV_VIA_COMBUSTA:
			holder = new VcHolder(si);
			break;
		case Event.EV_SUN_DEGREE:
			holder = new SunDegreeHolder(si);
			break;
		case Event.EV_MOON_SIGN:
			holder = new MoonSignHolder(si);
			break;
		case Event.EV_ASP_EXACT:
			if (isSummaryMode)
				holder = new AspectScrollHolder(si);
			else
				holder = new AspectHolder(si);
			break;
		case Event.EV_RETROGRADE:
			if (isSummaryMode)
				holder = new RetrogradeScrollHolder(si);
			else
				holder = new RetrogradeHolder(si);
			break;
		case Event.EV_PLANET_HOUR:
			holder = new PlanetHourHolder(si);
			break;
		case Event.EV_MOON_MOVE:
			holder = new MoonTransitionHolder(si);
			break;
		case Event.EV_TITHI:
			holder = new TithiHolder(si);
			break;
		default:
			return null;
		}
		holder.mIsSummaryMode = isSummaryMode;
		return holder;
	}

	protected void initLayout(View v) {
		if ((mFlags & LAYOUT_FLAG_CAPTION) != 0)
			mType = (TextView) v.findViewById(R.id.EventListItemType);
		if ((mFlags & LAYOUT_FLAG_TEXT0) != 0)
			mText0 = (TextView) v.findViewById(R.id.EventListItemText0);
		if ((mFlags & LAYOUT_FLAG_TEXT1) != 0)
			mText1 = (TextView) v.findViewById(R.id.EventListItemText1);
		if ((mFlags & LAYOUT_FLAG_PLANET0) != 0)
			mPlanet0 = (AstroTextView) v
					.findViewById(R.id.EventListItemPlanet0);
		if ((mFlags & LAYOUT_FLAG_PLANET1) != 0)
			mPlanet1 = (AstroTextView) v
					.findViewById(R.id.EventListItemPlanet1);
		if ((mFlags & LAYOUT_FLAG_DEGREE) != 0)
			mDegree = (TextView) v.findViewById(R.id.EventListItemDegree);
		if ((mFlags & LAYOUT_FLAG_ASPECT) != 0)
			mAspect = (AstroTextView) v.findViewById(R.id.EventListItemAspect);
		if ((mFlags & LAYOUT_FLAG_INFO) != 0) {
			mInfo = (ImageView) v.findViewById(R.id.ShowEventList);
			if (mInfo != null)
				mInfo.setOnClickListener(this);
		}
		if ((mFlags & LAYOUT_FLAG_ZODIAC) != 0)
			mZodiac = (AstroTextView) v.findViewById(R.id.EventListItemZodiac);
		v.setOnClickListener(this);
	}

	public static void initialize(Context context) {
		mContext = context;
		mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mResources = mContext.getResources();
		mDefaultTextColor = mResources.getColor(R.color.main_text);
		mBlueMarkColor = mResources.getColor(R.color.blue_mark);
		mRedMarkColor = mResources.getColor(R.color.red_mark);
		AstroTextView.assignTypeface(Typeface.createFromAsset(
				mContext.getAssets(), "Astronom.ttf"));
	}

	abstract public void fillLayout();

	public void onClick(View v) {
		if (v.getId() == R.id.ShowEventList) {
			SummaryItem si = (SummaryItem) v.getTag();
			if (si != null) {
				Intent intent = new Intent(mContext, EventListActivity.class);
				intent.putExtra(PreferenceUtils.LISTKEY_EVENT_KEY, si.mKey);
				mContext.startActivity(intent);
			}
		} else {
			Object obj = v.getTag();
			if (obj != null) {
				ViewHolder holder = (ViewHolder) obj;
				Event e = holder.getActiveEvent();
				String text = InterpretationProvider.getInstance().getText(e);
				if (text != null) {
					Intent intent = new Intent(mContext,
							InterpreterActivity.class);
					intent.putExtra(PreferenceUtils.LISTKEY_INTERPRETER_TEXT,
							text);
					intent.putExtra(PreferenceUtils.LISTKEY_INTERPRETER_EVENT,
							e);
					mContext.startActivity(intent);
				}
			}
		}
	}

	void setImage(ImageView v, int id) {
		Drawable drawable = mResources.getDrawable(id);
		v.setImageDrawable(drawable);
	}

	void clearImage(ImageView v) {
		v.setImageDrawable(null);
	}

	void updateInfoButton(SummaryItem si) {
		if (mInfo != null) {
			if (mIsSummaryMode) {
				int remainingEventCount = si.mEvents.size();
				if (mActiveEvent != null)
					--remainingEventCount;
				if (remainingEventCount > 0) {
					mInfo.setVisibility(View.VISIBLE);
					mInfo.setTag(si);
					return;
				}
			} else {
				mInfo.setPadding(0, 0, 0, 0);
			}
			mInfo.setVisibility(View.INVISIBLE);
			mInfo.setTag(null);
		}
	}

	public final void calculateActiveEvent(long customTime, long currentTime) {
		int pos = mSummaryItem.getActiveEventPosition(customTime, currentTime);
		mActiveEvent = (pos == -1) ? null : mSummaryItem.mEvents.get(pos);
	}

	public Event getActiveEvent() {
		return mIsSummaryMode ? mActiveEvent : mSummaryItem.mEvents.get(0);
	}

	protected void setColorByEventMode(TextView tv, Event e) {
		int color = mDefaultTextColor;
		if (e == mActiveEvent) {
			switch (mSummaryItem.mEventMode) {
			case SummaryItem.EVENT_MODE_CURRENT_TIME:
				color = mRedMarkColor;
				break;
			case SummaryItem.EVENT_MODE_CUSTOM_TIME:
				color = mBlueMarkColor;
				break;
			}
		}
		tv.setTextColor(color);
	}
}
