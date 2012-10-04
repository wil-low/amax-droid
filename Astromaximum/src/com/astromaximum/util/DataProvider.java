package com.astromaximum.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.Vector;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

import com.astromaximum.android.Preferences;

public class DataProvider {
	private static DataProvider mInstance;
	private final String STATE_KEY_YEAR = "Year";
	private final String STATE_KEY_MONTH = "Month";
	private final String STATE_KEY_DAY = "Day";
	private final String TAG = "DataProvider";

	public static final int RANGE_DAY = 0;
	public static final int RANGE_WEEK = 1;
	public static final int RANGE_MONTH = 2;
	public static final int RANGE_LAST = 3;

	private int mYear;
	private int mMonth;
	private int mDay;
	private long mStartTime;
	private long mEndTime;

	private Vector<Vector<Event>> mEventCache;

	private CommonDataFile mCommonDatafile;
	private LocationsDataFile mLocationDatafile;

	public static final Event[] mEvents = new Event[100];
	private Context mContext;

	protected static final int EF_DATE = 0x1; // contains 2nd date - 4b
	protected static final int EF_PLANET1 = 0x2; // contains 1nd planet - 1b
	protected static final int EF_PLANET2 = 0x4; // contains 2nd planet - 1b
	protected static final int EF_DEGREE = 0x8; // contains degree or angle - 2b
	protected static final int EF_CUMUL_DATE_B = 0x10; // date are cumulative
														// from 1st 4b - 1b
	protected static final int EF_CUMUL_DATE_W = 0x20; // date are cumulative
														// from 1st 4b - 2b
	protected static final int EF_SHORT_DEGREE = 0x40; // contains angle 0..180
														// - 1b
	protected static final int EF_NEXT_DATE2 = 0x80; // 2nd date is 1st in next
														// event

	public static final long MSECINDAY = 86400 * 1000;
	protected long mStartJD, mFinalJD;
	protected int mDayCount;
	public static final Calendar mCalendar = getUtcCalendar();
	private static final int STREAM_BUFFER_SIZE = 10000;

	boolean mExternalStorageAvailable = false;
	boolean mExternalStorageWriteable = false;
	private String mLocationDir = null;

	private DataProvider(Context context) {
		mContext = context;
		mEventCache = new Vector<Vector<Event>>();
		for (int i = 0; i < RANGE_LAST; ++i)
			mEventCache.add(new Vector<Event>());
		
		AssetManager manager = mContext.getAssets();
		InputStream is;
		try {
			is = manager.open("common.dat");
			mCommonDatafile = new CommonDataFile(is, mCalendar);
			checkStorage();
			if (mExternalStorageAvailable) {
				File cacheDir = Environment.getExternalStorageDirectory();
				mLocationDir = cacheDir.getAbsolutePath()
						+ "/Android/data/com.astromaximum.android/cache";
				cacheDir = new File(mLocationDir);
				cacheDir.mkdirs();
			}	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Event.mCalendar = (GregorianCalendar)mCalendar;
	}

	void checkStorage() {
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
			// We can read and write the media
			mExternalStorageAvailable = mExternalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			// We can only read the media
			mExternalStorageAvailable = true;
			mExternalStorageWriteable = false;
		} else {
			// Something else is wrong. It may be one of many other states, but
			// all we need
			// to know is we can neither read nor write
			mExternalStorageAvailable = mExternalStorageWriteable = false;
		}
	}

	int readSubData(DataInputStream is, int evtype, int planet, boolean isCommon) {
		try {
			is.reset();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int eventsCount = 0;
		int flag;
		int skipOff;
		Event last = new Event(0, 0);
		last.mEvtype = evtype;
		int fnext_date2;
		int PERIOD = (evtype == Event.EV_ASCAPHETICS) ? 2 * 60 : 24 * 60;
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
				flag = is.readShort();
				if (planet == is.readByte()) {
					break;
				} else {
					is.skip(skipOff - 6);
				}
			}
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
			long mydate0, mydate1;
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
					last.setDate1(mydate0 - Event.ROUNDING_MSEC);
					mydate1 = mFinalJD;
				}
				if (last.isInPeriod(mStartTime, mEndTime, false)) {
					mEvents[eventsCount++] = new Event(last);
				} else {
					if (eventsCount > 0) {
						break;
					}
				}
				last.mPlanet0 = myplanet0;
				last.mPlanet1 = myplanet1;
				last.mDegree = (short) mydgr;
				last.setDate0(mydate0);
				last.setDate1(mydate1);
			}
			if (last.isInPeriod(mStartTime, mEndTime, false)) {
				mEvents[eventsCount++] = new Event(last);
			}
		} catch (IOException ex) {
		}
		return eventsCount;
	}

	void getEventsOnPeriod(Vector<Event> v, int evtype, int planet,
			boolean special, long dayStart, long dayEnd, int value) {
		boolean flag = false;
		int cnt = getEvents(evtype, planet, dayStart, dayEnd);
		for (int i = 0; i < cnt; i++) {
			final Event ev = mEvents[i];
			if (ev.isInPeriod(dayStart, dayEnd, special)) {
				flag = true;
				if (value > 0) {
					ev.mDegree = (short) value;
				}
				v.addElement(ev);
			} else if (flag) {
				break;
			}
		}
	}

	int getEvents(int evtype, int planet, long dayStart, long dayEnd) {
		switch (evtype) {
		case Event.EV_ASTRORISE:
		case Event.EV_ASTROSET:
		case Event.EV_RISE:
		case Event.EV_SET:
		case Event.EV_NAVROZ:
		case Event.EV_ASCAPHETICS:
			return readSubData(mLocationDatafile.mData, evtype, planet, false);
		default:
			return readSubData(mCommonDatafile.mData, evtype, planet, true);
		}
	}

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

	public static DataProvider getInstance(Context context) {
		if (mInstance == null)
			mInstance = new DataProvider(context);
		return mInstance;
	}

	public static DataProvider getInstance() {
		return mInstance;
	}

	public int getYear() {
		return mYear;
	}

	public int getMonth() {
		return mMonth;
	}

	public int getDay() {
		return mDay;
	}

	public void saveState() {
		Log.d(TAG, "saveInstanceState");
		SharedPreferences.Editor editor = PreferenceManager
				.getDefaultSharedPreferences(mContext).edit();
		editor.putInt(STATE_KEY_YEAR, mYear);
		editor.putInt(STATE_KEY_MONTH, mMonth);
		editor.putInt(STATE_KEY_DAY, mDay);
		editor.commit();
	}

	public void restoreState() {
		Log.d(TAG, "restoreInstanceState");
		Calendar c = getUtcCalendar();
		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(mContext);
		mYear = sharedPref.getInt(STATE_KEY_YEAR, c.get(Calendar.YEAR));
		mMonth = sharedPref.getInt(STATE_KEY_MONTH, c.get(Calendar.MONTH));
		mDay = sharedPref.getInt(STATE_KEY_DAY, c.get(Calendar.DAY_OF_MONTH));
		int locationId = sharedPref.getInt(Preferences.KEY_LOCATION_ID, 0);
		Log.i(TAG, "Received locationId " + locationId + ": " + mYear);
		if (locationId == 0) { // no default location, unbundle from asset
			if (mExternalStorageWriteable)
				locationId = unbundleLocationAsset(sharedPref);
		}
		loadLocation(locationId, sharedPref);
		Log.i(TAG, "Received locationId " + locationId + ": " + mYear + " " + mLocationDatafile.mCity);
	}

	private void loadLocation(int locationId, SharedPreferences sharedPref) {
		String hexId = Integer.toHexString(locationId);
		File locFile = new File(mLocationDir, hexId + ".dat");
		try {
			BufferedInputStream is = new BufferedInputStream(new FileInputStream(locFile), STREAM_BUFFER_SIZE);
			mLocationDatafile = new LocationsDataFile(is);
			SharedPreferences.Editor editor = sharedPref.edit();
			editor.putInt(Preferences.KEY_LOCATION_ID, locationId);
			editor.commit();
			Event.mCalendar = new GregorianCalendar(TimeZone.getTimeZone(mLocationDatafile.mTimezone));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private int unbundleLocationAsset(SharedPreferences sharedPref) {
		int lastLocationId = 0;
		AssetManager manager = mContext.getAssets();
		try {
			InputStream is = manager.open("locations.dat");
			LocationBundle locBundle = new LocationBundle(is);
			int index = 0;
			byte[] buffer = null;
			SharedPreferences.Editor editor = sharedPref.edit();
			for (int i = 0; i < locBundle.mRecordCount; ++i) {
				buffer = locBundle.extractLocation(index);
				LocationsDataFile datafile = new LocationsDataFile(
						new ByteArrayInputStream(buffer));
				lastLocationId = datafile.mCityId;
				String hexId = Integer.toHexString(datafile.mCityId);
				Log.i(TAG, index + ": " + hexId + " " + datafile.mCity);
				File locFile = new File(mLocationDir, hexId + ".dat");
				OutputStream out = null;
				try {
					out = new BufferedOutputStream(new FileOutputStream(locFile), STREAM_BUFFER_SIZE);
					out.write(buffer);
					editor.putString(Preferences.STATE_KEY_LOC_PREFIX + index, hexId);
				}
				finally {
					if (out != null)
						out.close();
				}
				++index;
			}
			editor.commit();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return lastLocationId;
	}

	public void setDate(int year, int month, int day) {
		mYear = year;
		mMonth = month;
		mDay = day;
	}

	public void gatherEvents(int rangeType) {
		mEventCache.get(rangeType).clear();
		switch (rangeType) {
		case RANGE_DAY:
			mCalendar.set(mYear, mMonth, mDay, 0, 0, 0);
			mCalendar.set(Calendar.MILLISECOND, 0);
			mStartTime = mCalendar.getTimeInMillis();
			mEndTime = mStartTime + +MSECINDAY;
			final Vector<Event> tithi = new Vector<Event>();
			getEventsOnPeriod(tithi, Event.EV_TITHI, Event.SE_MOON, false,
					mStartTime, mEndTime, 0);
			mEventCache.get(rangeType).addAll(tithi);
			break;
		}
	}

	public Object[] get(int rangeType) {
		return mEventCache.get(rangeType).toArray();
	}

	// mStartJD = calendar.getTime().getTime();
	// mFinalJD = mStartJD + mDayCount * MSECINDAY;
}
