package com.astromaximum.util;


public class LocationFilter extends SubDataReader {
	final BaseEvent[] mEvents = new BaseEvent[100];

	@Override
	protected void addEvent(int idx, BaseEvent event) {
		mEvents[idx] = new BaseEvent(event);
	}

}
