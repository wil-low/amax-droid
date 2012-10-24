package com.astromaximum.android;

import android.app.Activity;
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
