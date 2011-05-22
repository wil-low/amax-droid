package com.astromaximum.util;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * <p>Title: Astromaximum</p>
 * <p/>
 * <p>Description: </p>
 * <p/>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p/>
 * <p>Company: Wiland Inc.</p>
 *
 * @author Andrei Ivushkin
 * @version 1.0
 * @noinspection CastToConcreteClass
 */
public abstract class DataFile {
    protected static final int EF_DATE = 0x1; // contains 2nd date - 4b
    protected static final int EF_PLANET1 = 0x2; // contains 1nd planet - 1b
    protected static final int EF_PLANET2 = 0x4; // contains 2nd planet - 1b
    protected static final int EF_DEGREE = 0x8; // contains degree or angle - 2b
    protected static final int EF_CUMUL_DATE_B = 0x10; // date are cumulative from 1st 4b - 1b
    protected static final int EF_CUMUL_DATE_W = 0x20; // date are cumulative from 1st 4b - 2b
    protected static final int EF_SHORT_DEGREE = 0x40; // contains angle 0..180 - 1b
    protected static final int EF_NEXT_DATE2 = 0x80; // 2nd date is 1st in next event

    public static final long MSECINDAY = 86400 * 1000;
    protected long mStartJD, mFinalJD;
    protected int mDayCount;
    protected byte[] mBuffer;
    protected byte[] mCustomData;
    public static final Calendar calendar = getUtcCalendar();

    public int readEvents(DataInputStream is, DataFileEventConsumer consumer) {
        int eventsCount=0;
        int flag;
        Event last = new Event(0, 0);
        int fnext_date2;
        try {
            System.out.println("BEGIN TRANSACTION;");
            while (true) {
                is.readUnsignedByte();
                last.mEvtype = is.readUnsignedByte();
                is.readShort();
                flag = is.readShort();
                int planet = is.readByte();
	            int PERIOD = (last.mEvtype == Event.EV_ASCAPHETICS) ? 2 * 60 : 24 * 60;
	            final int count = is.readShort();
	            int fcumul_date_b = (flag & EF_CUMUL_DATE_B);
	            int fcumul_date_w = (flag & EF_CUMUL_DATE_W);
	            int fdate = (flag & EF_DATE);
	            int fplanet1 = (flag & EF_PLANET1);
	            int fplanet2 = (flag & EF_PLANET2);
	            int fdegree = (flag & EF_DEGREE);
	            int fshort_degree = (flag & EF_SHORT_DEGREE);
	            fnext_date2 = (flag & EF_NEXT_DATE2);
	
	            byte myplanet0 = (byte) planet, myplanet1 = -1;
	            int mydgr = 127;
	            long mydate0 = 0, mydate1 = 0;
	            int cumul;
	            long date = 0;
	            for (int i = 0; i < count; i++) {
	                if (fcumul_date_b != 0) {
	                    if (i != 0) {
	                        cumul = is.readByte();
	                        date += (cumul + PERIOD) * 60;
	                    } else {
	                        date = is.readInt();
	                    }
	                } else if (fcumul_date_w != 0) {
	                    if (i != 0) {
	                        cumul = is.readShort();
	                        date += (cumul + PERIOD) * 60;
	                    } else {
	                        date = is.readInt();
	                    }
	                } else {
	                    date = is.readInt();
	                }
	
	                mydate0 = date * 1000;
	                if (fdate != 0) {
	                    mydate1 = ((long) is.readInt() * 1000);
	                } else {
	                    mydate1 = mydate0;
	                }
	                if (fplanet1 != 0) {
	                    myplanet0 = is.readByte();
	                }
	                if (fplanet2 != 0) {
	                    myplanet1 = is.readByte();
	                }
	                if (fdegree != 0) {
	                    if (fshort_degree != 0) {
	                        mydgr = is.readUnsignedByte();
	                    } else {
	                        mydgr = is.readShort();
	                    }
	                }
	                if (fnext_date2 != 0) {
	                    last.mDate1 = mydate0;
	                    mydate1 = mFinalJD;
	                }
		            last.mPlanet0 = myplanet0;
		            last.mPlanet1 = myplanet1;
		            last.mDegree = (short) mydgr;
		            last.mDate0 = mydate0;
		            last.mDate1 = mydate1;
		            addEvent(consumer, last);
		            ++eventsCount;
	            }
            }
        }
        catch (IOException ex) {
        	System.out.println("Ex: " + ex.getMessage());
        }
        System.out.println("END TRANSACTION;");
        return eventsCount;
    }

    public abstract int readSubData(DataFileEventConsumer consumer) throws IOException;
    protected abstract void addEvent(DataFileEventConsumer consumer, Event last);

/* 
    int getEvents(int evtype, int planet, long dayStart, long dayEnd) {
        switch (evtype) {
            case Event.EV_ASTRORISE:
            case Event.EV_ASTROSET:
            case Event.EV_RISE:
            case Event.EV_SET:
            case Event.EV_NAVROZ:
            case Event.EV_ASCAPHETICS:
                return readSubData(geoposData, evtype, planet, false, dayStart, dayEnd);
            default:
                return readSubData(commonData, evtype, planet, true, dayStart, dayEnd);
        }
    }
*/

    long getStartJD() {
		return mStartJD;
	}

	long getFinalJD() {
		return mFinalJD;
	}

	int getDayCount() {
		return mDayCount;
	}

	public static Calendar getUtcCalendar() {
		return Calendar.getInstance(TimeZone.getTimeZone("UTC"));
	}
    
}

