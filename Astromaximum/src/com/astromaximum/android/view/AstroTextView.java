package com.astromaximum.android.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class AstroTextView extends TextView {

	static Typeface mTypeface;
	
	public static void assignTypeface(Typeface tf) {
		mTypeface = tf;
	}
	
	public AstroTextView(Context context) {
		super(context);
		setTypeface(mTypeface);
	}

	public AstroTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setTypeface(mTypeface);
	}

	public AstroTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setTypeface(mTypeface);
	}

}
