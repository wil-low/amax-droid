package com.astromaximum.android.view;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


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
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		return super.onTouchEvent(event);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Paint paint = new Paint();
		paint.setColor(Color.LTGRAY);
		paint.setAntiAlias(true);
		canvas.drawLine(0, 0, getWidth(), getHeight(), paint);
		canvas.drawLine(0, getHeight(), getWidth(), 0 , paint);
	}
}
