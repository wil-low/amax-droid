package com.astromaximum.android.view;

import java.util.Vector;

import com.astromaximum.util.Event;

import android.content.Context;
import android.util.AttributeSet;

public class MultipleEventView extends EventView {
	protected Vector<Event> events = null;
	
	public MultipleEventView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public MultipleEventView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public MultipleEventView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void clearEvents() {
		events = null;
	}

	@Override
	public void addEvent(Event event) {
		if (events == null)
			events = new Vector<Event>();
		events.add(event);
	}

	@Override
	public Event getEvent() {
		return getEvent(0);
	}

	@Override
	public Event getEvent(int index) {
		if (events != null) {
			try {
				return events.get(index);
			}
			catch (ArrayIndexOutOfBoundsException ex) {}
		}
		return null;
	}
}
