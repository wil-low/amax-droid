package com.astromaximum.android.view;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.astromaximum.android.util.Event;

public class SummaryAdapter extends ArrayAdapter<SummaryItem> {
	private long mCustomTime;
	private long mCurrentTime;

	private static int VOC_VIEW = 0;
	private static int VC_VIEW = 1;
	private static int SUN_DEGREE_VIEW = 2;
	private static int MOON_SIGN_VIEW = 3;
	private static int ASPECT_VIEW = 4;
	private static int PLANET_HOUR_VIEW = 5;
	private static int MOON_MOVE_VIEW = 6;
	private static int TITHI_VIEW = 7;
	private static int EMPTY_VIEW = 8;

	private static int VIEW_COUNT = 9;

	public SummaryAdapter(Context context, ArrayList<SummaryItem> eventCache,
			long customTime, long currentTime) {
		super(context, 0, eventCache);
		mCustomTime = customTime;
		mCurrentTime = currentTime;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		SummaryItem si = getItem(position);
		ViewHolder holder = null;
		if (v == null) {
			holder = ViewHolder.makeHolder(si, true);
			v = ViewHolder.mInflater.inflate(holder.mLayoutId, null);
			holder.initLayout(v);
			v.setTag(holder);
		} else {
			holder = (ViewHolder) v.getTag();
		}
		holder.calculateActiveEvent(mCustomTime, mCurrentTime);
		holder.fillLayout();
		return v;
	}

	@Override
	public int getViewTypeCount() {
		return VIEW_COUNT;
	}

	@Override
	public int getItemViewType(int position) {
		SummaryItem si = getItem(position);
		
		if (si.mEvents.isEmpty())
			return EMPTY_VIEW;
		
		switch (si.mKey) {
		case Event.EV_VOC:
			return VOC_VIEW;
		case Event.EV_VIA_COMBUSTA:
			return VC_VIEW;
		case Event.EV_SUN_DEGREE:
			return SUN_DEGREE_VIEW;
		case Event.EV_MOON_SIGN:
			return MOON_SIGN_VIEW;
		case Event.EV_ASP_EXACT:
			return ASPECT_VIEW;
		case Event.EV_PLANET_HOUR:
			return PLANET_HOUR_VIEW;
		case Event.EV_MOON_MOVE:
			return MOON_MOVE_VIEW;
		case Event.EV_TITHI:
			return TITHI_VIEW;
		}
		return VIEW_COUNT;
	}
}
