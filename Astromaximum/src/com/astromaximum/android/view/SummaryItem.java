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
}
