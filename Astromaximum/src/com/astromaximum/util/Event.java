package com.astromaximum.util;

import java.util.*;

final public class Event {
    static final byte SE_SUN = 0;
    static final byte SE_MOON = 1;
    static final byte SE_MERCURY = 2;
    static final byte SE_VENUS = 3;
    static final byte SE_MARS = 4;
    static final byte SE_JUPITER = 5;
    static final byte SE_SATURN = 6;
    static final byte SE_URANUS = 7;
    static final byte SE_NEPTUNE = 8;
    static final byte SE_PLUTO = 9;
    static final byte SE_TRUE_NODE = 10;
    static final byte SE_MEAN_APOG = 11;
    static final byte SE_WHITE_MOON = 12;
    static final int EV_VOC = 0; // void of course
    static final int EV_SIGN_ENTER = 1; // enter into sign
    static final int EV_ASP_EXACT = 2; // exact aspect
    static final int EV_RISE = 3;  // rising & setting
    static final int EV_DEGREE_PASS = 4;  // entering degree
    static final int EV_VIA_COMBUSTA = 5;  // good & bad degrees
    static final int EV_RETROGRADE = 6;
    static final int EV_ECLIPSE = 7;
    static final int EV_TITHI = 8;
    static final int EV_NAKSHATRA = 9;
    static final int EV_SET = 10;  // rising & setting
    static final int EV_DECL_EXACT = 11;  // declination
    static final int EV_NAVROZ = 12;  // Navroz
    static final int EV_TOP_DAY = 13;  // week days
    static final int EV_PLANET_HOUR = 14;  // planetary hours
    static final int EV_STATUS = 15;
    static final int EV_SUN_RISE = 16;
    static final int EV_MOON_RISE = 17;
    static final int EV_MOON_MOVE = 18;
    static final int EV_SEL_DEGREES = 19;
    static final int EV_DAY_HOURS = 20;
    static final int EV_NIGHT_HOURS = 21;
    static final int EV_SUN_DAY = 22;
    static final int EV_MOON_DAY = 23;
    static final int EV_TOP_MONTH = 24;
    static final int EV_MOON_PHASE = 25;
    static final int EV_ZODIAC_SIGN = 26;
    static final int EV_PANEL = 27;
    static final int EV_TOPIC_BUTTON = 28;
    static final int EV_DEG_2ND = 29; // degrees on second page
    static final int EV_WEEK_GRID = 30;
    static final int EV_MONTH_GRID = 31;
    static final int EV_DECUMBITURE = 32;
    static final int EV_DECUMB_ASPECT = 33;
    static final int EV_DECUMB_BEGIN = 34;
    static final int EV_SUN_DEGREE_LARGE = 35;
    static final int EV_MOON_SIGN_LARGE = 36;
    static final int EV_HELP = 37;
    static final int EV_ASP_EXACT_MOON = 38;
    static final int EV_DEGPASS0 = 39;
    static final int EV_DEGPASS1 = 40;
    static final int EV_DEGPASS2 = 41;
    static final int EV_DEGPASS3 = 42;
    static final int EV_HELP0 = 43;
    static final int EV_HELP1 = 44;
    static final int EV_ASTRORISE = 45;
    static final int EV_ASTROSET = 46;
    static final int EV_APHETICS = 47;
    static final int EV_FAST = 48;
    static final int EV_ASCAPHETICS = 49;
    static final int EV_MSG = 50;
    static final int EV_BACK = 51;
    static final int EV_TATTVAS = 52;
    static final int EV_LAST = 53;  // last - do not use

    // Any changes above must be synched with %eventType in tools.pm
    // and EventType in mutter2/events.h !!!

    int mEvtype = 0;
    byte mPlanet0, mPlanet1 = -1;
    long mDate0, mDate1;
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
    Event(long dat, int planet) {
        mPlanet0 = (byte) planet;
        mDate0 = mDate1 = dat;
    }

    /**
     * to2String
     *
     * @param value int
     * @return String
     */
    static String to2String(int value) {
        String str = Integer.toString(value);
        if (str.length() == 1) {
            str = "0" + str;
        }
        return str;
    }

    int getDegree() {
        return mDegree & 0x3ff;
    }

    int getDegType() {
        return (mDegree >>> 14) & 0x3;
    }

    /**
     * Event
     *
     * @param event Event
     */
    Event(Event event) {
        mPlanet0 = event.mPlanet0;
        mPlanet1 = event.mPlanet1;
        mDate0 = event.mDate0;
        mDate1 = event.mDate1;
        mDegree = event.mDegree;
    }

	public void toSQL() {
		System.out.println("INSERT INTO common_events (evtype, date0, date1, degree, planet0, planet1) VALUES ("
				+ mEvtype + ", julianday('" + long2String(mDate0, 0, false) + "', 'utc'), julianday('"
				+ long2String(mDate1, 0, false) + "', 'utc') , " + mDegree + ", " + mPlanet0 + ", " + mPlanet1 + ");");
	}

	public String date2Sql (long date)
	{
		return "julianday('" + long2String(mDate0, 0, false) + "', 'utc')";
	}
	
    public static String long2String(long date0, int hoursOnly, boolean h24) {
//        date0 += localOffset(date0);
        DataFile.calendar.setTime(new Date(date0));
        final StringBuffer s = new StringBuffer();
        if (hoursOnly == 0) {
            s.append(Integer.toString(DataFile.calendar.get(Calendar.YEAR)))
                    .append("-")
            		.append(to2String(DataFile.calendar.get(Calendar.MONTH) + 1))
                    .append("-")
                    .append(to2String(DataFile.calendar.get(Calendar.DAY_OF_MONTH)));
            s.append(" ");
        }
        int hh = 0, mm = 0;
        try {
            hh = DataFile.calendar.get(Calendar.HOUR_OF_DAY);
            mm = DataFile.calendar.get(Calendar.MINUTE);
        }
        catch (Exception e) {
            System.out.println("Ex: long2String(" + Long.toString(date0) + ", "
                + Integer.toString(hoursOnly) +", " + (h24 ? "true" : "false"));
        }
        if (h24 && hh + mm == 0) {
            hh = 24;
        }
        s.append(to2String(hh)).
                append(":").append(to2String(mm));
//    if(!hoursOnly)
//      s.append("/");

//    s+=to2String(date0[index])+":"+to2String(date0[index]);
        return s.toString();//s;
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
		return mDate0;
	}

	public void setDate0(long date0) {
		mDate0 = date0;
	}

	public long getDate1() {
		return mDate1;
	}

	public void setDate1(long date1) {
		mDate1 = date1;
	}

	public short getFullDegree() {
		return mDegree;
	}

	public void setFullDegree(short degree) {
		mDegree = degree;
	}
}

