package com.astromaximum.android.view;

import com.astromaximum.util.Event;

public interface EventHolder {
	public void clearEvents();
	public void addEvent(Event event);
	public Event getEvent();
	public Event getEvent(int index);
}
