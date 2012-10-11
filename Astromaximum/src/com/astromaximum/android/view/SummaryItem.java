package com.astromaximum.android.view;

import java.util.Vector;

import com.astromaximum.util.Event;

public class SummaryItem {

	public static final String LISTKEY_EVENT_KEY = "com.astromaximum.android.eventKey";
	public static final String LISTKEY_EVENT_DATE = "com.astromaximum.android.eventDate";
	public String mKey;
	public Vector<Event> mEvents;

	public SummaryItem(String key, Vector<Event> events) {
		mKey = key;
		mEvents = events;
	}

	public String toString() {
		if (mEvents.isEmpty())
			return mKey;
		return mEvents.get(0).toString();
	}

}
