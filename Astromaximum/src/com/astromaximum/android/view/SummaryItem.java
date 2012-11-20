package com.astromaximum.android.view;

import java.util.ArrayList;

import com.astromaximum.util.Event;

public class SummaryItem {

	public int mKey;
	public int mEventMode;
	public ArrayList<Event> mEvents;
	
	// how is active event selected
	public static final int EVENT_MODE_NONE = 0;
	public static final int EVENT_MODE_CURRENT_TIME = 1;
	public static final int EVENT_MODE_CUSTOM_TIME = 2;

	public SummaryItem(int key, ArrayList<Event> events) {
		mKey = key;
		mEvents = events;
		mEventMode = EVENT_MODE_NONE;
	}

	public SummaryItem(int key, Event e) {
		mKey = key;
		mEvents = new ArrayList<Event>();
		mEvents.add(e);
		mEventMode = EVENT_MODE_NONE;
	}

	public int getActiveEventPosition(long now, boolean useCustomTime) {
		int index = 0;
		switch (mKey) {
		case Event.EV_MOON_MOVE:
			for (Event e : mEvents) {
				if (e.mEvtype == Event.EV_MOON_MOVE
						&& Event.dateBetween(now, e.mDate[0], e.mDate[1]) == 0) {
					mEventMode = useCustomTime ? EVENT_MODE_CUSTOM_TIME : EVENT_MODE_CURRENT_TIME;
					return index;
				}
				++index;
			}
			break;
		case Event.EV_VOC:
		case Event.EV_VIA_COMBUSTA:
			int prev = -1;
			for (Event e : mEvents) {
				int between = Event.dateBetween(now, e.mDate[0], e.mDate[1]);
				if (between == 0) {
					mEventMode = useCustomTime ? EVENT_MODE_CUSTOM_TIME : EVENT_MODE_CURRENT_TIME;
					return index;
				} else if (between == 1) {
					mEventMode = EVENT_MODE_NONE;
					return index;
				}
				prev = index;
				++index;
			}
			return prev;
		default:
			for (Event e : mEvents) {
				if (Event.dateBetween(now, e.mDate[0], e.mDate[1]) == 0) {
					mEventMode = useCustomTime ? EVENT_MODE_CUSTOM_TIME : EVENT_MODE_CURRENT_TIME;
					return index;
				}
				++index;
			}
			break;
		}
		return -1;
	}
}
