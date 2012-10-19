package com.astromaximum.android.view;

import java.util.Vector;

import com.astromaximum.util.Event;

public class SummaryItem {

	public static final String LISTKEY_EVENT_KEY = "com.astromaximum.android.eventKey";
	public static final String LISTKEY_EVENT_DATE = "com.astromaximum.android.eventDate";
	public static final String LISTKEY_INTERPRETER_TEXT = "com.astromaximum.android.interpreterCode";
	public static final String LISTKEY_INTERPRETER_EVENT = "com.astromaximum.android.event";
	private static long mPeriod0;
	private static long mPeriod1;
	public int mKey;
	public Vector<Event> mEvents;

	public SummaryItem(int key, Vector<Event> events) {
		mKey = key;
		mEvents = events;
	}

	public String toString() {
		if (mEvents.isEmpty())
			return Integer.toString(mKey);
		return mEvents.get(0).toString();
	}

	public Event getActiveEvent() {
		if (mEvents.isEmpty())
			return null;
		return mEvents.get(0);
	}

	public static void setTimeRange(long date0, long date1) {
		mPeriod0 = date0;
		mPeriod1 = date1;
	}
}
