package com.astromaximum.android.view;
import com.astromaximum.util.Event;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;


public class EventView extends View {
	
	public EventView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public EventView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public EventView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean onTouchEvent(MotionEvent motionEvent) {
		Event event = getEvent();
		if (event != null) {
			Toast toast = Toast.makeText(getContext(), event.toString(), Toast.LENGTH_LONG);
			toast.show();
		}
		return super.onTouchEvent(motionEvent);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Paint paint = new Paint();
		paint.setColor(Color.LTGRAY);
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.STROKE);
		canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
//		canvas.drawLine(0, 0, getWidth(), getHeight(), paint);
//		canvas.drawLine(0, getHeight(), getWidth(), 0 , paint);
	}

	public void clearEvents() {
	}

	public void addEvent(Event event) {
	}

	public Event getEvent() {
		return null;
	}

	public Event getEvent(int index) {
		return null;
	}
}
