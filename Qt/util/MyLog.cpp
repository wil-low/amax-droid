package com.astromaximum.android.util;

import java.util.ArrayList;

import android.util.Log;

public class MyLog {
	private static int mLevel;

	public final static void setLevel(int level) {
		mLevel = level;
	}

	public final static void v(String tag, String message) {
		if (mLevel > 0)
			return;
		Log.v(tag, message);
	}

	public final static void d(String tag, String message) {
		if (mLevel > 1)
			return;
		Log.d(tag, message);
	}

	public final static void i(String tag, String message) {
		if (mLevel > 2)
			return;
		Log.i(tag, message);
	}

	public final static void w(String tag, String message) {
		if (mLevel > 3)
			return;
		Log.w(tag, message);
	}

	public final static void e(String tag, String message) {
		if (mLevel > 4)
			return;
		Log.e(tag, message);
	}

	public static void d(String tag, ArrayList<Event> events) {
		if (mLevel > 1)
			return;
		for (Event e : events)
			Log.d(tag, e.toString());
	}
}