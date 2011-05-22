package com.astromaximum.android.view;

import com.astromaximum.util.Event;

import android.content.Context;
import android.util.AttributeSet;

public class SingleEventView extends EventView{
	protected Event event = null;
	
	public SingleEventView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public SingleEventView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public SingleEventView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void clearEvents() {
		event = null;
	}

	@Override
	public void addEvent(Event event) {
		this.event = event;
	}

	@Override
	public Event getEvent() {
		return event;
	}

	@Override
	public Event getEvent(int index) {
		return event;
	}
}
