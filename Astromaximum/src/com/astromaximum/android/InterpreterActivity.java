package com.astromaximum.android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.astromaximum.android.util.DataProvider;
import com.astromaximum.android.util.Event;
import com.astromaximum.android.util.MyLog;

public class InterpreterActivity extends SherlockActivity {

	private static String TAG = "InterpreterActivity";
	private Context mContext;
	private TextView mInterpreter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.activity_interpreter);
		getSupportActionBar().setDisplayShowHomeEnabled(false);

		DataProvider.getInstance(getApplicationContext());

		String text = getIntent().getStringExtra(
				PreferenceUtils.LISTKEY_INTERPRETER_TEXT);
		Event ev = getIntent().getParcelableExtra(
				PreferenceUtils.LISTKEY_INTERPRETER_EVENT);

		getSupportActionBar().setTitle(makeTitle(ev));
		getSupportActionBar().setSubtitle(makeSubTitle(ev));

		mInterpreter = (TextView) findViewById(R.id.textInterpretation);
		if (text != null)
			mInterpreter.setText(Html.fromHtml(text));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		//getSupportMenuInflater().inflate(R.menu.interpreter, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		/*case R.id.menu_share:
			Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
			emailIntent.setType("text/plain");
			emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Astromaximum: " + getSupportActionBar().getSubtitle());
			String text = getSupportActionBar().getSubtitle() + "\n" + getSupportActionBar().getTitle() + "\n\n" + mInterpreter.getText();
			emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, text);
			startActivity(emailIntent);
			break;*/
		}
		return true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		MyLog.d(TAG, "OnPause");
	}

	@Override
	protected void onResume() {
		super.onResume();
		MyLog.d(TAG, "OnResume");
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		MyLog.d(TAG, "OnRestart");
	}

	private String getStr(int id) {
		return mContext.getResources().getString(id);
	}

	private String makeSubTitle(Event ev) {
		int id = 0;
		switch (ev.mEvtype) {
		case Event.EV_VOC:
			id = R.string.si_key_voc;
			break;
		case Event.EV_VIA_COMBUSTA:
			id = R.string.si_key_vc;
			break;
		case Event.EV_DEGREE_PASS:
			return String
					.format(getStr(R.string.fmt_planet_in_degree),
							mContext.getResources().getStringArray(
									R.array.planets)[ev.mPlanet0],
							ev.getDegree() % 30 + 1,
							mContext.getResources().getStringArray(
									R.array.constell_genitive)[ev.getDegree() / 30]);
		case Event.EV_SIGN_ENTER:
			return String
					.format(getStr(R.string.fmt_planet_in_sign),
							mContext.getResources().getStringArray(
									R.array.planets)[ev.mPlanet0],
							mContext.getResources().getStringArray(
									R.array.constell_locative)[ev.getDegree()]);
		case Event.EV_PLANET_HOUR:
			return String.format(
					getStr(R.string.fmt_hour_of_planet),
					mContext.getResources().getStringArray(
							R.array.planets_genitive)[ev.mPlanet0]);
		case Event.EV_MOON_MOVE:
			id = R.string.si_key_moon_move;
			break;
		case Event.EV_ASP_EXACT_MOON:
		case Event.EV_ASP_EXACT:
			return String
					.format(getStr(R.string.fmt_aspect),
							mContext.getResources().getStringArray(
									R.array.aspect)[Event.ASPECT_MAP.get(ev
									.getDegree())],
							mContext.getResources().getStringArray(
									R.array.planets)[ev.mPlanet0],
							mContext.getResources().getStringArray(
									R.array.planets)[ev.mPlanet1]);
		case Event.EV_TITHI:
			return getStr(R.string.si_key_tithi) + " "
					+ Integer.toString(ev.getDegree());
		case Event.EV_RETROGRADE:
			return mContext.getResources().getStringArray(
					R.array.planets_retrograde)[ev.mPlanet0];
		}
		return getStr(id);
	}

	private String makeTitle(Event e) {
		StringBuilder result = new StringBuilder();

		switch (e.mEvtype) {
		case Event.EV_ASP_EXACT_MOON:
		case Event.EV_ASP_EXACT:
			result.append(Event.long2String(e.mDate[0],
					Event.mMonthAbbrDayDateFormat, false));
			break;
		default:
			result.append(
					Event.long2String(e.mDate[0],
							Event.mMonthAbbrDayDateFormat, false))
					.append(" - ")
					.append(Event.long2String(e.mDate[1],
							Event.mMonthAbbrDayDateFormat, true));
		}
		return result.toString();
	}
}
