package com.astromaximum.android.view;

import com.astromaximum.util.Event;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;

public class MoonSignEventView extends SingleEventView {

	public MoonSignEventView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public MoonSignEventView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public MoonSignEventView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void addEvent(Event event) {
		super.addEvent(event);
		event.setCaption(Event.long2String(event.getDate0(), 1, true)); 
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Event event = getEvent();
		String captionStr = (event == null) ? "MoonSign?" : event.getCaption(); 
		Paint paint = new Paint();
		paint.setColor(Color.LTGRAY);
		Rect bounds = new Rect();
		paint.getTextBounds(captionStr, 0, captionStr.length(), bounds);
		canvas.drawText(captionStr, (getWidth() - bounds.width()) / 2, (getHeight() + bounds.height()) / 2, paint);
	}

}
