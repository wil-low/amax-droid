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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.Vector;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.util.Log;

import com.astromaximum.android.PreferenceUtils;
import com.astromaximum.android.view.SummaryItem;

public class DataProvider {
	private static DataProvider mInstance;
	private final String TAG = "DataProvider";

	public static final int RANGE_DAY = 0;
	public static final int RANGE_WEEK = 1;
	public static final int RANGE_MONTH = 2;
	public static final int RANGE_LAST = 3;

	// constants used in event map

	public static final String KEY_VOC = "VOC";
	public static final String KEY_VC = "VC";
	public static final String KEY_SUN_DEGREE = "SUN_DEGREE";
	public static final String KEY_MOON_SIGN = "MOON_SIGN";
	public static final String KEY_TITHI = "TITHI";
	public static final String KEY_PLANET_HOUR = "PLANET_HOUR";
	public static final String KEY_ASPECTS = "ASPECTS";
	public static final String KEY_MOON_MOVE = "MOON_MOVE";
	public static final String KEY_RETROGRADE = "RETROGRADE";
	public static final String KEY_SUN_RISESET = "SUN_RISESET";
	public static final String KEY_MOON_RISESET = "MOON_RISESET";

	private static final byte[] WEEK_START_HOUR = { 0, 3, 6, 2, 5, 1, 4 };
	private static final byte[] PLANET_HOUR_SEQUENCE = { Event.SE_SUN,
			Event.SE_VENUS, Event.SE_MERCURY, Event.SE_MOON, Event.SE_SATURN,
			Event.SE_JUPITER, Event.SE_MARS };

	private int mYear;
	private int mMonth;
	private int mDay;
	private long mStartTime;
	private long mEndTime;

	private Vector<Vector<SummaryItem>> mEventCache;

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
	private Calendar mCalendar = Calendar.getInstance(TimeZone
			.getTimeZone("UTC"));
	private static final int STREAM_BUFFER_SIZE = 10000;

	boolean mExternalStorageAvailable = false;
	boolean mExternalStorageWriteable = false;
	private String mLocationDir = null;

	private DataProvider(Context context) {
		mContext = context;
		mEventCache = new Vector<Vector<SummaryItem>>();
		for (int i = 0; i < RANGE_LAST; ++i)
			mEventCache.add(new Vector<SummaryItem>());

		AssetManager manager = mContext.getAssets();
		InputStream is;
		try {
			is = manager.open("common.dat");
			mCommonDatafile = new CommonDataFile(is);
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

	int readSubData(DataInputStream is, int evtype, int planet,
			boolean isCommon, long dayStart, long dayEnd) {
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
					last.mDate[1] = mydate0 - Event.ROUNDING_MSEC;
					mydate1 = mFinalJD;
				}
				if (last.isInPeriod(dayStart, dayEnd, false)) {
					mEvents[eventsCount++] = new Event(last);
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
				mEvents[eventsCount++] = new Event(last);
			}
		} catch (IOException ex) {
		}
		return eventsCount;
	}

	Vector<Event> getEventsOnPeriod(int evtype, int planet, boolean special,
			long dayStart, long dayEnd, int value) {
		boolean flag = false;
		Vector<Event> result = new Vector<Event>();
		int cnt = getEvents(evtype, planet, dayStart, dayEnd);
		for (int i = 0; i < cnt; i++) {
			final Event ev = mEvents[i];
			if (ev.isInPeriod(dayStart, dayEnd, special)) {
				flag = true;
				if (value > 0) {
					ev.mDegree = (short) value;
				}
				result.addElement(ev);
			} else if (flag) {
				break;
			}
		}
		return result;
	}

	int getEvents(int evtype, int planet, long dayStart, long dayEnd) {
		switch (evtype) {
		case Event.EV_ASTRORISE:
		case Event.EV_ASTROSET:
		case Event.EV_RISE:
		case Event.EV_SET:
		case Event.EV_NAVROZ:
		case Event.EV_ASCAPHETICS:
			return readSubData(mLocationDatafile.mData, evtype, planet, false,
					dayStart, dayEnd);
		default:
			return readSubData(mCommonDatafile.mData, evtype, planet, true,
					dayStart, dayEnd);
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
		editor.putLong(PreferenceUtils.KEY_START_TIME, mStartTime);
		editor.commit();
	}

	public void restoreState() {
		Log.d(TAG, "restoreInstanceState");
		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(mContext);
		String locationId = sharedPref.getString(
				PreferenceUtils.KEY_LOCATION_ID, "");
		Log.i(TAG, "Received locationId " + locationId + ": " + mYear);
		if (locationId.equals("")) { // no default location, unbundle from asset
			if (mExternalStorageWriteable)
				locationId = unbundleLocationAsset();
		}
		loadLocation(locationId, sharedPref);
		Log.i(TAG, "Received locationId " + locationId + ": " + mYear + " "
				+ mLocationDatafile.mCity);
		mStartTime = sharedPref.getLong(PreferenceUtils.KEY_START_TIME,
				mCalendar.getTimeInMillis());
		mCalendar.setTimeInMillis(mStartTime);
		setDateFromCalendar();
	}

	private void loadLocation(String locationId, SharedPreferences sharedPref) {
		BufferedInputStream is = null;
		File locFile = new File(mLocationDir, locationId + ".dat");
		try {
			is = new BufferedInputStream(new FileInputStream(locFile),
					STREAM_BUFFER_SIZE);
			mLocationDatafile = new LocationsDataFile(is);
		} catch (FileNotFoundException e) {
			locationId = PreferenceUtils.getSortedLocations(mContext)
					.firstKey();
			locFile = new File(mLocationDir, locationId + ".dat");
			try {
				is = new BufferedInputStream(new FileInputStream(locFile),
						STREAM_BUFFER_SIZE);
				mLocationDatafile = new LocationsDataFile(is);
			} catch (FileNotFoundException e2) {
				e2.printStackTrace();
			}
		}
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString(PreferenceUtils.KEY_LOCATION_ID, locationId);
		editor.commit();
		mCalendar = new GregorianCalendar(
				TimeZone.getTimeZone(mLocationDatafile.mTimezone));
		Event.setTimeZone(mLocationDatafile.mTimezone);
	}

	private String unbundleLocationAsset() {
		String lastLocationId = "";
		AssetManager manager = mContext.getAssets();
		try {
			InputStream is = manager.open("locations.dat");
			LocationBundle locBundle = new LocationBundle(is);
			int index = 0;
			byte[] buffer = null;
			SharedPreferences sharedPref = mContext.getSharedPreferences(
					PreferenceUtils.PREF_LOCATION_LIST, 0);
			SharedPreferences.Editor editor = sharedPref.edit();
			for (int i = 0; i < locBundle.mRecordCount; ++i) {
				buffer = locBundle.extractLocation(index);
				LocationsDataFile datafile = new LocationsDataFile(
						new ByteArrayInputStream(buffer));
				lastLocationId = Integer.toHexString(datafile.mCityId);
				Log.i(TAG, index + ": " + lastLocationId + " " + datafile.mCity);
				File locFile = new File(mLocationDir, lastLocationId + ".dat");
				OutputStream out = null;
				try {
					out = new BufferedOutputStream(
							new FileOutputStream(locFile), STREAM_BUFFER_SIZE);
					out.write(buffer);
					editor.putString(lastLocationId, datafile.mCity);
				} finally {
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

	public void changeDate(int deltaDays) {
		// stick to noon to determine date
		mStartTime += MSECINDAY * deltaDays + MSECINDAY / 2;
		mCalendar.setTimeInMillis(mStartTime);
		setDateFromCalendar();
	}

	public void setDate(int year, int month, int day) {
		mYear = year;
		mMonth = month;
		mDay = day;
	}

	public void setDateFromCalendar() {
		mYear = mCalendar.get(Calendar.YEAR);
		mMonth = mCalendar.get(Calendar.MONTH);
		mDay = mCalendar.get(Calendar.DAY_OF_MONTH);
	}

	public void gatherEvents(int rangeType) {
		mEventCache.get(rangeType).clear();
		switch (rangeType) {
		case RANGE_DAY:
			mCalendar.set(mYear, mMonth, mDay, 0, 0, 0);
			mCalendar.set(Calendar.MILLISECOND, 0);
			mStartTime = mCalendar.getTimeInMillis();
			mEndTime = mStartTime + MSECINDAY - Event.ROUNDING_MSEC;
			Log.d(TAG, new Date(mStartTime) + " / " + new Date(mEndTime));
			SummaryItem.setTimeRange(mStartTime, mEndTime);

			Event[] riseSet = new Event[2];
			// ****** SUN & MOON RISES & SETS
			for (int i = Event.SE_SUN; i <= Event.SE_MOON; i++) {
				Event eop = getEventOnPeriod(Event.EV_RISE, i, true,
						mStartTime, mEndTime);
				if (eop == null || eop.mDate[0] < mStartTime) {
					eop = new Event(0, i);
				}
				riseSet[i] = eop;
				eop = getEventOnPeriod(Event.EV_SET, i, false, mStartTime,
						mEndTime);
				if (eop == null || eop.mDate[0] < mStartTime) {
					eop = new Event(0, i);
				}
				riseSet[i].mDate[1] = eop.mDate[0];
			}

			Vector<SummaryItem> v = new Vector<SummaryItem>();
			v.add(new SummaryItem(Event.EV_VOC, getVOCs()));
			v.add(new SummaryItem(Event.EV_VIA_COMBUSTA, getVC()));
			v.add(new SummaryItem(Event.EV_SUN_DEGREE, getSunDegree()));
			v.add(new SummaryItem(Event.EV_MOON_SIGN, getMoonSign()));
			// v.add(new SummaryItem(Event.EV_SUN_RISESET,
			// getRiseSet(Event.SE_SUN)));
			// v.add(new SummaryItem(Event.EV_MOON_RISESET,
			// getRiseSet(Event.SE_MOON)));
			v.add(new SummaryItem(Event.EV_PLANET_HOUR,
					getPlanetaryHours(riseSet[Event.SE_SUN])));
			v.add(new SummaryItem(Event.EV_ASP_EXACT, getAspects()));
			v.add(new SummaryItem(Event.EV_MOON_MOVE, getMoonMove()));
			v.add(new SummaryItem(Event.EV_RETROGRADE, getRetrogrades()));
			v.add(new SummaryItem(Event.EV_TITHI, getTithis()));
			mEventCache.set(rangeType, v);
			/*
			 * getEventsOnPeriod(tmpVector, Event.EV_DEGREE_PASS, Event.SE_SUN,
			 * false, mStartTime, mEndTime, 0); getEventsOnPeriod(tmpVector,
			 * Event.EV_SIGN_ENTER, Event.SE_MOON, false, mStartTime, mEndTime,
			 * 0); getEventsOnPeriod(tmpVector, Event.EV_TITHI, Event.SE_MOON,
			 * false, mStartTime, mEndTime, 0); getEventsOnPeriod(tmpVector,
			 * Event.EV_PLANET_HOUR, -1, false, mStartTime, mEndTime, 0);
			 * getEventsOnPeriod(tmpVector, Event.EV_ASP_EXACT, -1, false,
			 * mStartTime, mEndTime, 0);
			 * mEventCache.get(rangeType).addAll(tmpVector);
			 */

			break;
		}
	}

	private Vector<Event> selectSingleEvent(Vector<Event> vc) {
		if (vc.isEmpty())
			return vc;
		Vector<Event> result = new Vector<Event>();
		result.add(vc.get(0));
		return result;
	}

	private Vector<Event> getVOCs() {
		return getEventsOnPeriod(Event.EV_VOC, Event.SE_MOON, false,
				mStartTime, mEndTime, 0);
	}

	private Vector<Event> getVC() {
		return getEventsOnPeriod(Event.EV_VIA_COMBUSTA, Event.SE_MOON, false,
				mStartTime, mEndTime, 0);
	}

	private Vector<Event> getSunDegree() {
		return getEventsOnPeriod(Event.EV_DEGREE_PASS, Event.SE_SUN, true,
				mStartTime, mEndTime, 0);
	}

	private Vector<Event> getMoonSign() {
		return getEventsOnPeriod(Event.EV_SIGN_ENTER, Event.SE_MOON, true,
				mStartTime, mEndTime, 0);
	}

	private Vector<Event> getTithis() {
		return getEventsOnPeriod(Event.EV_TITHI, Event.SE_MOON, false,
				mStartTime, mEndTime, 0);
	}

	private Vector<Event> getPlanetaryHours(Event currentSunRise) {
		Event nextSunRise = getEventOnPeriod(Event.EV_RISE, Event.SE_SUN, true,
				mStartTime + MSECINDAY, mEndTime + MSECINDAY);
		Vector<Event> result = new Vector<Event>();
		int startHour = WEEK_START_HOUR[mCalendar.get(Calendar.DAY_OF_WEEK) - 1];
		final long dayHour = (currentSunRise.mDate[1] - currentSunRise.mDate[0]) / 12;
		final long nightHour = (nextSunRise.mDate[0] - currentSunRise.mDate[1]) / 12;
		long st = currentSunRise.mDate[0];
		for (int i = 0; i < 24; ++i) {
			Event ev = new Event(st, PLANET_HOUR_SEQUENCE[startHour % 7]);
			ev.mEvtype = Event.EV_PLANET_HOUR;
			st += i < 12 ? dayHour : nightHour;
			ev.mDate[1] = st - Event.ROUNDING_MSEC; // exclude last minute
			result.add(ev);
			++startHour;
		}

		return result;
	}

	private Vector<Event> getAspects() {
		return getAspectsOnPeriod(-1, mStartTime, mEndTime);
	}

	private Vector<Event> getMoonMove() {
        Vector<Event> asp = getEventsOnPeriod(Event.EV_SIGN_ENTER, Event.SE_MOON,
                true, mStartTime - MSECINDAY * 2, mEndTime + MSECINDAY * 4, 0);
//        for (Event ev : asp)
//            ev.mPlanet1 = Event.SE_MOON;
        Vector<Event> moonMoveVec = getAspectsOnPeriod(Event.SE_MOON,
        		mStartTime - MSECINDAY * 2, mEndTime + MSECINDAY * 2);

        mergeEvents(moonMoveVec, asp, true);
        asp.removeAllElements();
        mergeEvents(asp, moonMoveVec, false);
        int id1 = -1;
        int id2 = -1;
        int counter = 0;
        for (Event ev : asp) {
            final long dat = ev.mDate[0];
            if (dat < mStartTime) {
                id1 = counter;
            }
            if (id2 == -1 && dat >= mEndTime) {
                id2 = counter;
            }
            ++counter;
        }
        asp.setSize(id2 + 1);
        try {
            for (int i = 0; i < id1; i++) {
                asp.removeElementAt(0);
            }
        } catch (ArrayIndexOutOfBoundsException aie) {
        }
        int sz = asp.size() - 1;
        int idx = 1;
        for (int i = 0; i < sz; i++) {
            Event evprev = asp.elementAt(idx - 1);
            long dd = (evprev.mPlanet0 == evprev.mPlanet1) ? evprev.mDate[0] : evprev.mDate[1];
            Event ev = new Event(dd, -1);
            ev.mEvtype = Event.EV_MOON_MOVE;
            ev.mDate[1] = asp.elementAt(idx).mDate[0] - Event.ROUNDING_MSEC;
            ev.mPlanet0 = evprev.mPlanet1;
            ev.mPlanet1 = asp.elementAt(idx).mPlanet1;
//      ev.dump();
            asp.insertElementAt(ev, idx);
            idx += 2;
        }
        sz = asp.size();
        for (int i = 0; i < sz; ++i) {
        	Event e = asp.elementAt(i);
        	if (e.mEvtype == Event.EV_MOON_MOVE) {
    			int j = i - 1;
    			while (j >= 0) {
    				byte planet = asp.elementAt(j).mPlanet1;
    				if (planet <= Event.SE_SATURN) {
    					e.mPlanet0 = planet;
    					break;
    				}
    				--j;
    			}
    			j = i + 1;
    			while (j < sz) {
    				byte planet = asp.elementAt(j).mPlanet1;
    				if (planet <= Event.SE_SATURN) {
    					e.mPlanet1 = planet;
    					break;
    				}
    				++j;
    			}
        	} else if (e.mEvtype == Event.EV_ASP_EXACT)
				e.mEvtype = Event.EV_ASP_EXACT_MOON;
        }
/*		
		Vector<Event> moonAspects = getAspectsOnPeriod(Event.SE_MOON,
				startTime, endTime);
		Vector<Event> result = getEventsOnPeriod(Event.EV_SIGN_ENTER,
				Event.SE_MOON, false, startTime, endTime, 0);
		moonAspects.addAll(result);
		Comparator<Event> comparator = new Event.EventDate0Comparator();
		Collections.sort(moonAspects, comparator);

		Vector<Event> moonMove = new Vector<Event>();

		int vecSize = moonAspects.size();
		for (int i = 0; i < vecSize - 1; ++i) {
			Event current = moonAspects.elementAt(i);
			Log.d(TAG, current.toString());
			Event transitionEvent = new Event(0, Event.SE_MOON);
			transitionEvent.mEvtype = Event.EV_MOON_MOVE;
			if (current.mEvtype == Event.EV_SIGN_ENTER)
				transitionEvent.mDate[0] = current.mDate[0];
			else
				transitionEvent.mDate[0] = current.mDate[1];
			transitionEvent.mDate[1] = moonAspects.elementAt(i + 1).mDate[0];
			
			int j = i;
			while (j >= 0) {
				byte planet = moonAspects.elementAt(j).mPlanet1;
				if (planet <= Event.SE_SATURN) {
					transitionEvent.mPlanet0 = planet;
					break;
				}
				--j;
			}
			j = i + 1;
			while (j < vecSize) {
				byte planet = moonAspects.elementAt(j).mPlanet1;
				if (planet <= Event.SE_SATURN) {
					transitionEvent.mPlanet1 = planet;
					break;
				}
				++j;
			}
			moonMove.add(current);
			moonMove.add(transitionEvent);
		}
		moonMove.add(moonAspects.elementAt(vecSize - 1));
		result.clear();
		for (Event e : moonMove) {
			if (Event.dateBetween(e.mDate[0], mStartTime, mEndTime) == 0) {
				if (e.mEvtype == Event.EV_ASP_EXACT) {
					e.mEvtype = Event.EV_ASP_EXACT_MOON;
				}
				e.mDate[1] -= Event.ROUNDING_MSEC;
				result.add(e);
			}
		}
		return result;*/
        return asp;
	}

    private static void mergeEvents(Vector<Event> dest, Vector<Event> add, boolean isSort) {
        for (Event ev : add) {
            if (isSort) {
                int idx = 0;
                final long dat = ev.mDate[0];
                final int sz = dest.size();
                while (idx < sz && dat > dest.elementAt(idx).mDate[0]) {
                    ++idx;
                }
                dest.insertElementAt(ev, idx);
            } else {
                dest.addElement(ev);
            }
        }
    }

	private Vector<Event> getRetrogrades() {
		Vector<Event> result = new Vector<Event>();
		for (int planet = Event.SE_SUN; planet <= Event.SE_PLUTO; ++planet) {
			Vector<Event> v = getEventsOnPeriod(Event.EV_RETROGRADE, planet,
					false, mStartTime, mEndTime, 0);
			if (!v.isEmpty())
				result.addAll(v);
		}
		return result;
	}

	private Vector<Event> getRiseSet(int planet, long startTime, long endTime) {
		Vector<Event> result = new Vector<Event>();
		Event eop = getEventOnPeriod(Event.EV_RISE, planet, true, startTime,
				endTime);
		if (eop == null || eop.mDate[0] < startTime) {
			eop = new Event(0, planet);
		}
		Event eop1 = getEventOnPeriod(Event.EV_SET, planet, false, startTime,
				mEndTime);
		if (eop1 == null || eop1.mDate[0] < startTime) {
			eop1 = new Event(0, planet);
		}
		eop.mDate[1] = eop1.mDate[0];
		result.add(eop);
		return result;
	}

	private Event getEventOnPeriod(int evType, int planet, boolean special,
			long startTime, long endTime) {
		int cnt = getEvents(evType, planet, startTime, endTime);
		if (evType == Event.EV_RISE && planet == Event.SE_SUN) {
			Event dummy = new Event(startTime, 0);
			dummy.mDate[1] = endTime;
			Log.d("dummy", dummy.toString());
			for (int i = 0; i < cnt; i++) {
				Log.d("getEventOnPeriod", mEvents[i].toString());
			}
		}
		for (int i = 0; i < cnt; i++) {
			final Event ev = mEvents[i];
			if (ev.isInPeriod(startTime, endTime, special)) {
				return ev;
			}
		}
		return null;
	}

	private Vector<Event> getAspectsOnPeriod(int planet, long startTime,
			long endTime) {
		Vector<Event> result = new Vector<Event>();
		boolean flag = false;
		int cnt = getEvents(Event.EV_ASP_EXACT,
				planet == Event.SE_MOON ? Event.SE_MOON : -1, startTime,
				endTime);
		for (int i = 0; i < cnt; i++) {
			final Event ev = mEvents[i];
			if (planet == -1 || ev.mPlanet0 == planet || ev.mPlanet1 == planet) {
				if (ev.isDateBetween(0, startTime, endTime)) {
					flag = true;
					result.addElement(ev);
				}
			} else if (flag) {
				break;
			}
		}
		return result;
	}

	public Vector<SummaryItem> get(int rangeType) {
		return mEventCache.get(rangeType);
	}

	public void setTodayDate() {
		Log.d(TAG, mCalendar.getTimeZone().getDisplayName());
		mCalendar = Calendar.getInstance(mCalendar.getTimeZone());
		setDateFromCalendar();
		Log.d("setTodayDate", mYear + "-" + mMonth + "-" + mDay);
		Log.d("setTodayDate",
				(String) DateFormat.format("yyyy MMMM dddd", mCalendar));
	}

	public String getLocationName() {
		if (mLocationDatafile == null)
			return null;
		return mLocationDatafile.mCity;
	}

	public String getCurrentDateString(String titleDateFormat) {
		return (String) DateFormat.format(titleDateFormat, mCalendar);
	}

	// mStartJD = calendar.getTime().getTime();
	// mFinalJD = mStartJD + mDayCount * MSECINDAY;
}
