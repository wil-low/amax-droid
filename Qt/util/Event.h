package com.astromaximum.android.util;

import java.util.Comparator;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateFormat;
import android.util.SparseIntArray;

import com.astromaximum.android.R;
import com.astromaximum.util.BaseEvent;

final public class Event extends com.astromaximum.util.BaseEvent implements Parcelable {

	public static final String[] CONSTELL_STR = { "Ari", "Tau", "Gem", "Cnc",
		"Leo", "Vir", "Lib", "Sco", "Sgr", "Cap", "Aqu", "Psc" };

	// angle: (ordinal number, aspect goodness(0 - conjunction, 1 - bad, 2 -
	// good))
	// ASPECT = {0: (0, 0), 180: (1, 1), 120: (2, 2), 90: (3, 1), 60: (4, 2),
	// 45: (5, 2)}
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

	// TODO Move USE_EXACT_RANGE to settings
	private static final boolean USE_EXACT_RANGE = false;

	private static long mPeriod0;
	private static long mPeriod1;
	private static Context mContext;
	public static String mMonthAbbrDayDateFormat;


	Event(long date, int planet) {
		super(date, planet);
	}
	
	public Event(BaseEvent event) {
		super(event);
	}
	
	static String to2String(int value) {
		String str = Integer.toString(value);
		if (str.length() == 1) {
			str = "0" + str;
		}
		return str;
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

	public static int dateBetween(long date0, long start, long end) {
		if (date0 < start) {
			return -1;
		}
		if (date0 > end) {
			return 1;
		}
		return 0;
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

	public static class EventDate0Comparator implements Comparator<Event> {
		public int compare(Event o1, Event o2) {
			return (int) (o1.mDate[0] - o2.mDate[0]);
		}
	}

	public static void setTimeRange(long date0, long date1) {
		mPeriod0 = date0;
		mPeriod1 = date1;
	}

	public String normalizedRangeString() {
		long date0 = mDate[0], date1 = mDate[1];
		if (USE_EXACT_RANGE) {
			if (date0 < mPeriod0)
				date0 = mPeriod0;
			if (date1 > mPeriod1)
				date1 = mPeriod1;

			return long2String(date0, null, false) + " - "
					+ long2String(date1, null, true);
		}

		boolean isTillRequired = date0 < mPeriod0;
		boolean isSinceRequired = date1 > mPeriod1;

		if (isTillRequired && isSinceRequired)
			return mContext.getString(R.string.norm_range_whole_day);

		if (isTillRequired)
			return mContext.getString(R.string.norm_range_arrow) + " "
					+ long2String(date1, null, true);

		if (isSinceRequired)
			return long2String(date0, null, false) + " "
					+ mContext.getString(R.string.norm_range_arrow);

		return long2String(date0, null, false) + " - "
				+ long2String(date1, null, true);
	}

	public static void setContext(Context context) {
		mContext = context;
		mMonthAbbrDayDateFormat = mContext
				.getString(R.string.month_abbr_day_date_format);
	}

	@Override
	public String formatDate(String dateFormat, long date) {
		return (String) DateFormat.format(dateFormat, mCalendar);
	}
}
