package com.astromaximum.android.view;

import java.util.Vector;

import com.astromaximum.util.Event;

public class SummaryItem {

	public String mKey;
	public Vector<Event> mEvents;

	public SummaryItem(String key, Vector<Event> events) {
		mKey = key;
		mEvents = events;
	}

}
