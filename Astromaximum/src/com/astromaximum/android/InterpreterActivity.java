package com.astromaximum.android;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;

import com.astromaximum.android.view.SummaryItem;
import com.astromaximum.util.Event;

public class InterpreterActivity extends Activity {

	private static String TAG = "InterpreterActivity";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_interpreter);
		String text = getIntent().getStringExtra(
				SummaryItem.LISTKEY_INTERPRETER_TEXT);
		Event ev = getIntent().getParcelableExtra(
				SummaryItem.LISTKEY_INTERPRETER_EVENT);

		TextView header = (TextView) findViewById(R.id.textHeader);
		header.setText(ev.toString());

		TextView interpreter = (TextView) findViewById(R.id.textInterpretation);
		if (text != null)
			interpreter.setText(Html.fromHtml(text));
	}

	public static String getInterpreterText(Context context, Event ev) {
		String interpreterCode = makeInterpreterCode(ev);
		Log.d(TAG, ev.toString());
		Log.d(TAG, interpreterCode);
		int resourceId = context.getResources().getIdentifier(interpreterCode,
				"string", context.getPackageName());
		if (resourceId == 0) {
			return null;
		} else {
			return context.getResources().getString(resourceId);
		}
	}

	static String makeInterpreterCode(Event ev) {
		String strPlanet = "";
		String strEventType = Integer.toString(ev.mEvtype);
		String param0 = "", param1 = "", param2 = "";
		switch (ev.mEvtype) {
		case Event.EV_ASP_EXACT:
			param0 = Integer.toString(ev.mPlanet0);
			param1 = Integer.toString(ev.mPlanet1);
			param2 = Integer.toString(Event.ASPECT_GOODNESS.get(ev.getDegree()));
			break;
		case Event.EV_DEGREE_PASS:
			param0 = Integer.toString(ev.getDegree());
			break;
		case Event.EV_TITHI:
		case Event.EV_SIGN_ENTER:
			strPlanet = Integer.toString(ev.mPlanet0);
			param0 = Integer.toString(ev.getDegree());
			break;
		case Event.EV_VOC:
		case Event.EV_VIA_COMBUSTA:
			strPlanet = Integer.toString(ev.mPlanet0);
			param0 = "0";
			break;
		case Event.EV_RETROGRADE:
		case Event.EV_PLANET_HOUR:
			param0 = Integer.toString(ev.mPlanet0);
			break;
		}
		return "int" + strPlanet + "_" + strEventType + "_" + param0 + "_"
				+ param1 + "_" + param2;
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		Log.d(TAG, "OnPause");
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "OnResume");
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		Log.d(TAG, "OnRestart");
	}
}
