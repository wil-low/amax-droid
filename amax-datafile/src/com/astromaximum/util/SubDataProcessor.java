package com.astromaximum.util;

import java.io.DataInputStream;
import java.io.IOException;

public abstract class SubDataProcessor {
	public static final int EF_DATE = 0x1; // contains 2nd date - 4b
	public static final int EF_PLANET1 = 0x2; // contains 1nd planet - 1b
	public static final int EF_PLANET2 = 0x4; // contains 2nd planet - 1b
	public static final int EF_DEGREE = 0x8; // contains degree or angle - 2b
	public static final int EF_CUMUL_DATE_B = 0x10; // date are cumulative
														// from 1st 4b - 1b
	public static final int EF_CUMUL_DATE_W = 0x20; // date are cumulative
														// from 1st 4b - 2b
	public static final int EF_SHORT_DEGREE = 0x40; // contains angle 0..180
														// - 1b
	public static final int EF_NEXT_DATE2 = 0x80; // 2nd date is 1st in next
														// event

	public static final long MSECINDAY = 86400 * 1000;

	protected int read(DataInputStream is, int evtype, int planet,
			boolean isCommon, long dayStart, long dayEnd, long mFinalJD,
			SubDataInfo info) {
		try {
			is.reset();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int eventsCount = 0;
		int flags = 0;
		int skipOff;
		BaseEvent last = new BaseEvent(0, 0);
		last.mEvtype = evtype;
		int fnext_date2;
		int PERIOD = (evtype == BaseEvent.EV_ASCAPHETICS) ? 2 * 60 : 24 * 60;
		int totalCount = 0;
		try {
			while (true) {
				is.readUnsignedByte();
				int rub = is.readUnsignedByte();
				while (evtype != rub) {
					skipOff = is.readShort() - 3;
					is.skip(skipOff);
					is.readUnsignedByte();
					rub = is.readUnsignedByte();
				}
				skipOff = is.readShort();
				flags = is.readShort();
				if (planet == is.readByte()) {
					break;
				} else {
					is.skip(skipOff - 6);
				}
			}
			totalCount = is.readShort();
			int fcumul_date_b = (flags & EF_CUMUL_DATE_B);
			int fcumul_date_w = (flags & EF_CUMUL_DATE_W);
			int fdate = (flags & EF_DATE);
			int fplanet1 = (flags & EF_PLANET1);
			int fplanet2 = (flags & EF_PLANET2);
			int fdegree = (flags & EF_DEGREE);
			int fshort_degree = (flags & EF_SHORT_DEGREE);
			fnext_date2 = (flags & EF_NEXT_DATE2);

			byte myplanet0 = (byte) planet, myplanet1 = -1;
			int mydgr = 127;
			long mydate0, mydate1;
			int cumul;
			long date = 0;
			for (int i = 0; i < totalCount; i++) {
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
					mydate1 = ((long) is.readInt() * 1000) - 1;
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
					last.mDate[1] = mydate0 - BaseEvent.ROUNDING_MSEC;
					mydate1 = mFinalJD;
				}
				if (last.isInPeriod(dayStart, dayEnd, false)) {
					addEvent(eventsCount, last);
					eventsCount++;
				} else {
					if (eventsCount > 0) {
						break;
					}
				}
				last.mPlanet0 = myplanet0;
				last.mPlanet1 = myplanet1;
				last.mDegree = (short) mydgr;
				last.mDate[0] = mydate0;
				last.mDate[1] = mydate1;
			}
			if (last.isInPeriod(dayStart, dayEnd, false)) {
				addEvent(eventsCount, last);
				eventsCount++;
			}
		} catch (IOException ex) {
		}

		if (info != null) {
			info.mEventType = evtype;
			info.mPlanet = planet;
			info.mFlags = flags;
			info.mTotalCount = totalCount;
		}

		return eventsCount;
	}

	abstract protected void addEvent(int idx, BaseEvent event);
}
