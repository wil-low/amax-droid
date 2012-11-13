package com.astromaximum.util;

public class StartPageItem {
	public String mCaption;
	public boolean mIsEnabled;
	public int mIndex;

	public StartPageItem(String caption, int index, boolean isEnabled) {
		mCaption = caption;
		mIndex = index;
		mIsEnabled = isEnabled;
	}
}
