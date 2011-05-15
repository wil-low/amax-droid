package com.astromaximum.util;

public interface EventConsumer {
	void addEvent(int year, Event event);
	void addEvent(int year, Event event, long locationId);
	void addLocation(Location location);
}
