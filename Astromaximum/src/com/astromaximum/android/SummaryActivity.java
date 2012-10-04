package com.astromaximum.android;

import android.os.Bundle;
import android.util.Log;

import com.astromaximum.android.view.EventView;
import com.astromaximum.util.Event;
import com.astromaximum.util.EventConsumer;

public class SummaryActivity extends EventActivity implements EventConsumer {
	private final String TAG = "SummaryActivity";

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.summary);
	}

    @Override
	protected void onResume() {
		super.onResume();
        Log.d(TAG, "OnResume");
		updateDisplay();
	}

	private void updateDisplay() {
	}

	public void addEvent(Event event) {
		EventView view = null;
		switch (event.getEvtype()) {
		case Event.EV_VOC:
			view = (EventView)findViewById(R.id.voc);
			break;
		case Event.EV_VIA_COMBUSTA:
			view = (EventView)findViewById(R.id.vc);
			break;
		case Event.EV_DEGREE_PASS:
			if (event.getPlanet0() == Event.SE_SUN)
				view = (EventView)findViewById(R.id.sun_degree_large);
			break;
		case Event.EV_SIGN_ENTER:
			if (event.getPlanet0() == Event.SE_MOON)
				view = (EventView)findViewById(R.id.moon_sign_large);
			break;
		}
		if (view != null)
			view.addEvent(event);
	}
}