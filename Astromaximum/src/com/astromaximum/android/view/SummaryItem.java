package com.astromaximum.android.view;

import java.util.ArrayList;

import com.astromaximum.util.Event;

public class SummaryItem {

	public int mKey;
	public ArrayList<Event> mEvents;

	public SummaryItem(int key, ArrayList<Event> events) {
		mKey = key;
		mEvents = events;
	}

	public SummaryItem(int key, Event e) {
		mKey = key;
		mEvents = new ArrayList<Event>();
		mEvents.add(e);
	}

	public int getActiveEventPosition(long now) {
		int result = -1, index = 0;
		if (mKey == Event.EV_MOON_MOVE) {
			for (Event e : mEvents) {
				if (e.mEvtype == Event.EV_MOON_MOVE
						&& Event.dateBetween(now, e.mDate[0], e.mDate[1]) == 0) {
					result = index;
					break;
					// MyLog.d("MoonTrans", mActiveEvent.toString());
				}
				++index;
			}
		} else {
			for (Event e : mEvents) {
				if (Event.dateBetween(now, e.mDate[0], e.mDate[1]) == 0) {
					result = index;
					break;
				}
				++index;
			}
		}
		return result;
	}
}
