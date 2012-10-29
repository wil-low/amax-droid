package com.astromaximum.android.view;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.astromaximum.android.EventListActivity;
import com.astromaximum.android.InterpreterActivity;
import com.astromaximum.android.R;
import com.astromaximum.util.Event;
import com.astromaximum.util.InterpretationProvider;

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
	protected SummaryItem mSummaryItem;
	public Event mActiveEvent;

	public static View makeView(SummaryItem si) {
		ViewHolder holder = null;
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
			holder = new AspectScrollHolder(si);
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
			holder = new SimpleHolder(si);
			break;
		}
		View v = mInflater.inflate(holder.mLayoutId, null);
		holder.initLayout(v);
		v.setTag(holder);
		return v;
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
			mInfo = (ImageView) v.findViewById(R.id.EventListItemInfo);
			if (mInfo != null)
				mInfo.setOnClickListener(this);
		}
		if ((mFlags & LAYOUT_FLAG_ZODIAC) != 0)
			mZodiac = (AstroTextView) v.findViewById(R.id.EventListItemZodiac);
		v.setOnClickListener(this);
	}

	public static void setContext(Context context) {
		mContext = context;
		mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mResources = mContext.getResources();
	}

	abstract public void fillLayout(SummaryItem si);

	public void onClick(View v) {
		if (v.getId() == R.id.EventListItemInfo) {
			SummaryItem si = (SummaryItem) v.getTag();
			if (si != null) {
				Intent intent = new Intent(mContext, EventListActivity.class);
				intent.putExtra(SummaryItem.LISTKEY_EVENT_KEY, si.mKey);
				intent.putExtra(SummaryItem.LISTKEY_EVENT_DATE, "mTitleDate");
				mContext.startActivity(intent);
			}
		} else {
			Object obj = v.getTag();
			if (obj != null) {
				ViewHolder holder = (ViewHolder) obj;
				String text = InterpretationProvider.getInstance().getText(
						holder.mActiveEvent);
				if (text != null) {
					Intent intent = new Intent(mContext,
							InterpreterActivity.class);
					intent.putExtra(SummaryItem.LISTKEY_INTERPRETER_TEXT, text);
					intent.putExtra(SummaryItem.LISTKEY_INTERPRETER_EVENT,
							holder.mActiveEvent);
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
			int remainingEventCount = si.mEvents.size();
			if (mActiveEvent != null)
				--remainingEventCount;
			if (remainingEventCount > 0) {
				mInfo.setVisibility(View.VISIBLE);
				mInfo.setTag(si);
			} else {
				mInfo.setVisibility(View.INVISIBLE);
				mInfo.setTag(null);
			}
		}
	}

	public void calculateActiveEvent(SummaryItem si) {
		mActiveEvent = null;
		if (!si.mEvents.isEmpty())
			mActiveEvent = SummaryItem.normalizeCopy(si.mEvents.get(0));
	}

}