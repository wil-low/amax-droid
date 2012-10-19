package com.astromaximum.util;

import java.util.Calendar;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseIntArray;

final public class Event implements Parcelable {
	public static final byte SE_SUN = 0;
	public static final byte SE_MOON = 1;
	public static final byte SE_MERCURY = 2;
	public static final byte SE_VENUS = 3;
	public static final byte SE_MARS = 4;
	public static final byte SE_JUPITER = 5;
	public static final byte SE_SATURN = 6;
	public static final byte SE_URANUS = 7;
	public static final byte SE_NEPTUNE = 8;
	public static final byte SE_PLUTO = 9;
	public static final byte SE_TRUE_NODE = 10;
	public static final byte SE_MEAN_APOG = 11;
	public static final byte SE_WHITE_MOON = 12;

	private static final String[] PLANET_STR = { "??", "SO", "MO", "ME", "VE",
			"MA", "JU", "SA", "UR", "NE", "PL", "TN", "AP", "WM" };

	public static final int EV_VOC = 0; // void of course
	public static final int EV_SIGN_ENTER = 1; // enter into sign
	public static final int EV_ASP_EXACT = 2; // exact aspect
	public static final int EV_RISE = 3; // rising & setting
	public static final int EV_DEGREE_PASS = 4; // entering degree
	public static final int EV_VIA_COMBUSTA = 5; // good & bad degrees
	public static final int EV_RETROGRADE = 6;
	public static final int EV_ECLIPSE = 7;
	public static final int EV_TITHI = 8;
	public static final int EV_NAKSHATRA = 9;
	public static final int EV_SET = 10; // rising & setting
	public static final int EV_DECL_EXACT = 11; // declination
	public static final int EV_NAVROZ = 12; // Navroz
	public static final int EV_TOP_DAY = 13; // week days
	public static final int EV_PLANET_HOUR = 14; // planetary hours
	public static final int EV_STATUS = 15;
	public static final int EV_SUN_RISE = 16;
	public static final int EV_MOON_RISE = 17;
	public static final int EV_MOON_MOVE = 18;
	public static final int EV_SEL_DEGREES = 19;
	public static final int EV_DAY_HOURS = 20;
	public static final int EV_NIGHT_HOURS = 21;
	public static final int EV_SUN_DAY = 22;
	public static final int EV_MOON_DAY = 23;
	public static final int EV_TOP_MONTH = 24;
	public static final int EV_MOON_PHASE = 25;
	public static final int EV_ZODIAC_SIGN = 26;
	public static final int EV_PANEL = 27;
	public static final int EV_TOPIC_BUTTON = 28;
	public static final int EV_DEG_2ND = 29; // degrees on second page
	public static final int EV_WEEK_GRID = 30;
	public static final int EV_MONTH_GRID = 31;
	public static final int EV_DECUMBITURE = 32;
	public static final int EV_DECUMB_ASPECT = 33;
	public static final int EV_DECUMB_BEGIN = 34;
	public static final int EV_SUN_DEGREE_LARGE = 35;
	public static final int EV_MOON_SIGN_LARGE = 36;
	public static final int EV_HELP = 37;
	public static final int EV_ASP_EXACT_MOON = 38;
	public static final int EV_DEGPASS0 = 39;
	public static final int EV_DEGPASS1 = 40;
	public static final int EV_DEGPASS2 = 41;
	public static final int EV_DEGPASS3 = 42;
	public static final int EV_HELP0 = 43;
	public static final int EV_HELP1 = 44;
	public static final int EV_ASTRORISE = 45;
	public static final int EV_ASTROSET = 46;
	public static final int EV_APHETICS = 47;
	public static final int EV_FAST = 48;
	public static final int EV_ASCAPHETICS = 49;
	public static final int EV_MSG = 50;
	public static final int EV_BACK = 51;
	public static final int EV_TATTVAS = 52;
	public static final int EV_SUN_DEGREE = 53;
	public static final int EV_MOON_SIGN = 54;
	public static final int EV_SUN_RISESET = 55;
	public static final int EV_MOON_RISESET = 56;
	public static final int EV_LAST = 57; // last - do not use

	public static final String[] EVENT_TYPE_STR = { "EV_VOC", // 0; // void of
																// course
			"EV_SIGN_ENTER", // 1; // enter into sign
			"EV_ASP_EXACT", // 2; // exact aspect
			"EV_RISE", // 3; // rising & setting
			"EV_DEGREE_PASS", // 4; // entering degree
			"EV_VIA_COMBUSTA", // 5; // good & bad degrees
			"EV_RETROGRADE", // 6;
			"EV_ECLIPSE", // 7;
			"EV_TITHI", // 8;
			"EV_NAKSHATRA", // 9;
			"EV_SET", // 10; // rising & setting
			"EV_DECL_EXACT", // 11; // declination
			"EV_NAVROZ", // 12; // Navroz
			"EV_TOP_DAY", // 13; // week days
			"EV_PLANET_HOUR", // 14; // planetary hours
			"EV_STATUS", // 15;
			"EV_SUN_RISE", // 16;
			"EV_MOON_RISE", // 17;
			"EV_MOON_MOVE", // 18;
			"EV_SEL_DEGREES", // 19;
			"EV_DAY_HOURS", // 20;
			"EV_NIGHT_HOURS", // 21;
			"EV_SUN_DAY", // 22;
			"EV_MOON_DAY", // 23;
			"EV_TOP_MONTH", // 24;
			"EV_MOON_PHASE", // 25;
			"EV_ZODIAC_SIGN", // 26;
			"EV_PANEL", // 27;
			"EV_TOPIC_BUTTON", // 28;
			"EV_DEG_2ND", // 29; // degrees on second page
			"EV_WEEK_GRID", // 30;
			"EV_MONTH_GRID", // 31;
			"EV_DECUMBITURE", // 32;
			"EV_DECUMB_ASPECT", // 33;
			"EV_DECUMB_BEGIN", // 34;
			"EV_SUN_DEGREE_LARGE", // 35;
			"EV_MOON_SIGN_LARGE", // 36;
			"EV_HELP", // 37;
			"EV_ASP_EXACT_MOON", // 38;
			"EV_DEGPASS0", // 39;
			"EV_DEGPASS1", // 40;
			"EV_DEGPASS2", // 41;
			"EV_DEGPASS3", // 42;
			"EV_HELP0", // 43;
			"EV_HELP1", // 44;
			"EV_ASTRORISE", // 45;
			"EV_ASTROSET", // 46;
			"EV_APHETICS", // 47;
			"EV_FAST", // 48;
			"EV_ASCAPHETICS", // 49;
			"EV_MSG", // 50;
			"EV_BACK", // 51;
			"EV_TATTVAS", // 52;
			"EV_SUN_DEGREE", // 53;
			"EV_MOON_SIGN", // 54;
			"EV_SUN_RISESET", // 55;
			"EV_MOON_RISESET", // 56;
			"EV_LAST", // 57; // last - do not use
	};
	// Any changes above must be synched with %eventType in tools.pm
	// and EventType in mutter2/events.h !!!
	
	
    // angle: (ordinal number, aspect goodness(0 - conjunction, 1 - bad, 2 - good))
    //ASPECT = {0: (0, 0), 180: (1, 1), 120: (2, 2), 90: (3, 1), 60: (4, 2), 45: (5, 2)}
	public static final SparseIntArray ASPECT_GOODNESS = new SparseIntArray();
	static {
		ASPECT_GOODNESS.put(0, 0);
		ASPECT_GOODNESS.put(180, 1);
		ASPECT_GOODNESS.put(120, 2);
		ASPECT_GOODNESS.put(90, 1);
		ASPECT_GOODNESS.put(60, 2);
		ASPECT_GOODNESS.put(45, 2);
	}

	public static final SparseIntArray ASPECT_MAP = new SparseIntArray();
	static {
		ASPECT_MAP.put(0, 0);
		ASPECT_MAP.put(180, 1);
		ASPECT_MAP.put(120, 2);
		ASPECT_MAP.put(90, 3);
		ASPECT_MAP.put(60, 4);
		ASPECT_MAP.put(45, 5);
	}

	static final long ROUNDING_MSEC = 60 * 1000;

	int mEvtype = 0;
	byte mPlanet0, mPlanet1 = -1;
	long[] mDate = new long[2];
	short mDegree = 127;

	String mCaption = null;

	public String getCaption() {
		return mCaption;
	}

	public void setCaption(String caption) {
		mCaption = caption;
	}

	/**
	 * @param dat
	 * @param planet
	 */
	Event(long date, int planet) {
		mPlanet0 = (byte) planet;
		mDate[0] = mDate[1] = date;
	}

	public Event(int evType, long date0, long date1, int planet0, int planet1,
			int degree) {
		mEvtype = evType;
		mDate[0] = date0;
		mDate[1] = date1;
		mPlanet0 = (byte) planet0;
		mPlanet1 = (byte) planet1;
		mDegree = (short) degree;
	}

	/**
	 * to2String
	 * 
	 * @param value
	 *            int
	 * @return String
	 */
	static String to2String(int value) {
		String str = Integer.toString(value);
		if (str.length() == 1) {
			str = "0" + str;
		}
		return str;
	}

	public int getDegree() {
		return mDegree & 0x3ff;
	}

	int getDegType() {
		return (mDegree >>> 14) & 0x3;
	}

	/**
	 * Event
	 * 
	 * @param event
	 *            Event
	 */
	Event(Event event) {
		mPlanet0 = event.mPlanet0;
		mPlanet1 = event.mPlanet1;
		mDate[0] = event.mDate[0];
		mDate[1] = event.mDate[1];
		mDegree = event.mDegree;
		mEvtype = event.mEvtype;
	}

	public String toString() {
		return "Event: (" + mEvtype + " " + getEvTypeStr() + " "
				+ long2String(mDate[0], 0, false) + " - "
				+ long2String(mDate[1], 0, false) + " " + getPlanetName(mPlanet0)
				+ "-" + getPlanetName(mPlanet1) + " d " + mDegree + ")";
	}

	private String getPlanetName(byte planet) {
		return PLANET_STR[planet + 1];
	}

	public static String long2String(long date0, int hoursOnly, boolean h24) {
		DataProvider.mCalendar.setTimeInMillis(date0);
		final StringBuffer s = new StringBuffer();
		if (hoursOnly == 0) {
			s.append(
					Integer.toString(DataProvider.mCalendar.get(Calendar.YEAR)))
					.append("-")
					.append(to2String(DataProvider.mCalendar
							.get(Calendar.MONTH) + 1))
					.append("-")
					.append(to2String(DataProvider.mCalendar
							.get(Calendar.DAY_OF_MONTH)));
			s.append(" ");
		}
		int hh = 0, mm = 0;
		try {
			hh = DataProvider.mCalendar.get(Calendar.HOUR_OF_DAY);
			mm = DataProvider.mCalendar.get(Calendar.MINUTE);
		} catch (Exception e) {
			System.out.println("Ex: long2String(" + Long.toString(date0) + ", "
					+ Integer.toString(hoursOnly) + ", "
					+ (h24 ? "true" : "false"));
		}
		if (h24 && hh + mm == 0) {
			hh = 24;
		}
		s.append(to2String(hh)).append(":").append(to2String(mm));
		// if(!hoursOnly)
		// s.append("/");

		// s+=to2String(date0[index])+":"+to2String(date0[index]);
		return s.toString();
	}

	public int getEvtype() {
		return mEvtype;
	}

	public void setEvtype(int evtype) {
		mEvtype = evtype;
	}

	public byte getPlanet0() {
		return mPlanet0;
	}

	public void setPlanet0(byte planet0) {
		mPlanet0 = planet0;
	}

	public byte getPlanet1() {
		return mPlanet1;
	}

	public void setPlanet1(byte planet1) {
		mPlanet1 = planet1;
	}

	public long getDate0() {
		return mDate[0];
	}

	public void setDate0(long date0) {
		mDate[0] = date0;
	}

	public long getDate1() {
		return mDate[1];
	}

	public void setDate1(long date1) {
		mDate[1] = date1;
	}

	public short getFullDegree() {
		return mDegree;
	}

	public void setFullDegree(short degree) {
		mDegree = degree;
	}

	public String getEvTypeStr() {
		return EVENT_TYPE_STR[mEvtype];
	}

	boolean isInPeriod(long start, long end, boolean special) {
		if (mDate[0] == 0) {
			return false;
		}
		final int f = dateBetween(mDate[0], start, end)
				+ dateBetween(mDate[1], start, end);
		if ((f == 2) || (f == -2)) {
			return false;
		}
		if (special) {
			if (f == -1) {
				return false;
			}
		}
		return true;
	}

	static int dateBetween(long date0, long start, long end) {
		if (date0 < start) {
			return -1;
		}
		if (date0 >= end) {
			return 1;
		}
		return 0;
	}

	boolean isDateBetween(int index, long start, long end) {
		long dat = mDate[index];
		return start <= dat && dat < end;
	}

	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void writeToParcel(Parcel out, int flags) {
		out.writeInt(mEvtype);
		out.writeByte(mPlanet0);
		out.writeByte(mPlanet1);
		out.writeLong(mDate[0]);
		out.writeLong(mDate[1]);
		out.writeInt(mDegree);
	}

	private Event(Parcel in) {
		mEvtype = in.readInt();
		mPlanet0 = in.readByte();
		mPlanet1 = in.readByte();
		mDate[0] = in.readLong();
		mDate[1] = in.readLong();
		mDegree = (short) in.readInt();
	}

	public static final Parcelable.Creator<Event> CREATOR = new Parcelable.Creator<Event>() {
		public Event createFromParcel(Parcel in) {
			return new Event(in);
		}

		public Event[] newArray(int size) {
			return new Event[size];
		}
	};
}
