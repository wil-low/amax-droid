package com.astromaximum.util;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Arrays;

import android.content.Context;
import android.content.res.AssetManager;

import com.astromaximum.android.R;

public class InterpretationProvider {
	private static final String TAG = "InterpretationProvider";
	private static InterpretationProvider mInstance;
	private Context mContext;
	DataInputStream mTexts;

	private InterpretationProvider(Context context) {
		mContext = context;
		AssetManager manager = mContext.getAssets();
		try {
			mTexts = new DataInputStream(manager.open(context.getResources()
					.getString(R.string.interpretation_dir) + "/interpret.dat"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static InterpretationProvider getInstance(Context context) {
		if (mInstance == null)
			mInstance = new InterpretationProvider(context);
		return mInstance;
	}

	public static InterpretationProvider getInstance() {
		return mInstance;
	}

	public String getText(Event e) {
		if (e == null)
			return null;
		int[] params = makeInterpreterCode(e);
		MyLog.d(TAG, "type: " + Event.EVENT_TYPE_STR[e.mEvtype] + " (" + e.mEvtype + "), params: " 
						+ Integer.toString(params[0])+ ", "
						+ Integer.toString(params[1]) + ", "
						+ Integer.toString(params[2]) + ", "
						+ Integer.toString(params[3]));
		int[] tempParams = new int[4];
		try {
			mTexts.reset();
			mTexts.skip(4);
			int sectionCount = mTexts.readUnsignedShort();
			int eventCount = 0;
			for (int i = 0; i < sectionCount; ++i) {
				if (e.mEvtype == mTexts.readByte()) {
					int offset = mTexts.readInt();
					eventCount = mTexts.readUnsignedShort();
					mTexts.reset();
					mTexts.skip(offset);
					break;
				} else {
					mTexts.skip(6);
				}
			}
			for (int i = 0; i < eventCount; ++i) {
				tempParams[0] = mTexts.readByte();
				for (int j = 0; j < 3; ++j)
					tempParams[j + 1] = mTexts.readShort();
				if (Arrays.equals(params, tempParams)) {
					return mTexts.readUTF();
				} else {
					int len = mTexts.readUnsignedShort();
					mTexts.skip(len);
				}
			}

		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	int[] makeInterpreterCode(Event ev) {
		int planet = -1;
		int param0 = -1, param1 = -1, param2 = -1;
		switch (ev.mEvtype) {
		case Event.EV_ASP_EXACT_MOON:
			planet = ev.mPlanet0;
			param0 = ev.mPlanet1;
			param1 = Event.ASPECT_GOODNESS.get(ev.getDegree());
			break;
		case Event.EV_ASP_EXACT:
			param0 = ev.mPlanet0;
			param1 = ev.mPlanet1;
			param2 = Event.ASPECT_GOODNESS.get(ev.getDegree());
			break;
		case Event.EV_DEGREE_PASS:
			param0 = ev.getDegree();
			break;
		case Event.EV_TITHI:
		case Event.EV_SIGN_ENTER:
		case Event.EV_MOON_DAY:
			planet = ev.mPlanet0;
			param0 = ev.getDegree();
			break;
		case Event.EV_NAVROZ:
			planet = ev.mPlanet0;
			param0 = ev.getDegree() + 1;
			break;
		case Event.EV_VOC:
		case Event.EV_VIA_COMBUSTA:
			planet = ev.mPlanet0;
			param0 = 0;
			break;
		case Event.EV_RETROGRADE:
		case Event.EV_PLANET_HOUR:
			param0 = ev.mPlanet0;
			break;
		case Event.EV_MOON_MOVE:
			planet = Event.SE_MOON;
			if (ev.mPlanet1 == -1) {
				param0 = 255;
				param1 = Event.SE_MOON;
			} else {
				if (ev.mPlanet0 == -1)
					param0 = Event.SE_MOON;
				else
					param0 = ev.mPlanet0;
				param1 = ev.mPlanet1;
			}
		}
		return new int[] { planet, param0, param1, param2 };
	}
}
