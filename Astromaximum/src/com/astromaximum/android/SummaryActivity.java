package com.astromaximum.android;

import android.app.Activity;
import android.os.Bundle;

public class SummaryActivity extends Activity {
	private final String TAG = "SummaryActivity";

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.summary);
	}
}