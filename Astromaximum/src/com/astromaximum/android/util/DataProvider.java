package com.astromaximum.android.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;

import com.astromaximum.android.PreferenceUtils;
import com.astromaximum.android.R;
import com.astromaximum.android.view.SummaryItem;
import com.astromaximum.util.BaseEvent;
import com.astromaximum.util.CommonDataFile;
import com.astromaximum.util.LocationBundle;
import com.astromaximum.util.LocationsDataFile;
import com.astromaximum.util.SubDataProcessor;

public class DataProvider extends SubDataProcessor {
	private static DataProvider mInstance;
	private final static String TAG = "DataProvider";

	// constants used in event map

	private static final byte[] WEEK_START_HOUR = { 0, 3, 6, 2, 5, 1, 4 };
	private static final byte[] PLANET_HOUR_SEQUENCE = { Event.SE_SUN,
			Event.SE_VENUS, Event.SE_MERCURY, Event.SE_MOON, Event.SE_SATURN,
			Event.SE_JUPITER, Event.SE_MARS };

	private int mYear;
	private int mMonth;
	private int mDay;

	private int mCurrentHour;
	private int mCurrentMinute;

	private long mStartTime;
	private long mEndTime;

	public ArrayList<SummaryItem> mEventCache;

	private CommonDataFile mCommonDatafile;
	private LocationsDataFile mLocationDatafile;

	public static final Event[] mEvents = new Event[100];
	private Context mContext;

	protected long mStartJD, mFinalJD;
	private Calendar mCalendar = Calendar.getInstance(TimeZone
			.getTimeZone("UTC"));
	private static final int STREAM_BUFFER_SIZE = 10000;

	private int mCustomHour = 0;
	private int mCustomMinute = 0;
	private String mTitleDateFormat;
	private boolean mUseCustomTime = false;
	private ArrayList<StartPageItem> mStartPageLayout;

	// Keep in sync with string-array name="startpage_items"
	static final int[] START_PAGE_ITEM_SEQ = new int[] { Event.EV_MOON_SIGN,
			Event.EV_MOON_MOVE, Event.EV_PLANET_HOUR, Event.EV_TITHI,
			Event.EV_SUN_DEGREE, Event.EV_ASP_EXACT, Event.EV_RETROGRADE,
			Event.EV_VOC, Event.EV_VIA_COMBUSTA, };

	private DataProvider(Context context) {
		mContext = context;
		mTitleDateFormat = mContext.getResources().getString(
				R.string.title_date_format);
		mEventCache = new ArrayList<SummaryItem>();

		AssetManager manager = mContext.getAssets();
		InputStream is;
		try {
			is = manager.open("common.dat");
			mCommonDatafile = new CommonDataFile(is);
			MyLog.d(TAG, "Common: " + mCommonDatafile.mStartYear + "-"
					+ mCommonDatafile.mStartMonth + "-"
					+ mCommonDatafile.mStartDay + ", "
					+ mCommonDatafile.mDayCount);
			is.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	ArrayList<Event> getEventsOnPeriod(int evtype, int planet, boolean special,
			long dayStart, long dayEnd, int value) {
		boolean flag = false;
		ArrayList<Event> result = new ArrayList<Event>();
		int cnt = getEvents(evtype, planet, dayStart, dayEnd);
		for (int i = 0; i < cnt; i++) {
			final Event ev = mEvents[i];
			if (ev.isInPeriod(dayStart, dayEnd, special)) {
				flag = true;
				if (value > 0) {
					ev.mDegree = (short) value;
				}
				result.add(ev);
			} else if (flag) {
				break;
			}
		}
		return result;
	}

	int getEvents(int evtype, int planet, long dayStart, long dayEnd) {
		//MyLog.i("getEvents", BaseEvent.EVENT_TYPE_STR[evtype]);
		switch (evtype) {
		case Event.EV_ASTRORISE:
		case Event.EV_ASTROSET:
		case Event.EV_RISE:
		case Event.EV_SET:
		case Event.EV_ASCAPHETICS:
			return read(mLocationDatafile.mData, evtype, planet, false,
					dayStart, dayEnd, mFinalJD, null);
		default:
			return read(mCommonDatafile.mData, evtype, planet, true, dayStart,
					dayEnd, mFinalJD, null);
		}
	}

	public long getStartJD() {
		return mStartJD;
	}

	public long getFinalJD() {
		return mFinalJD;
	}

	public static DataProvider getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new DataProvider(context);
			mInstance.restoreState();
		}
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
		MyLog.d(TAG, "saveInstanceState");
		SharedPreferences.Editor editor = PreferenceManager
				.getDefaultSharedPreferences(mContext).edit();
		editor.putLong(PreferenceUtils.KEY_START_TIME, mStartTime);
		editor.putInt(PreferenceUtils.KEY_CUSTOM_HOUR, mCustomHour);
		editor.putInt(PreferenceUtils.KEY_CUSTOM_MINUTE, mCustomMinute);
		editor.commit();
	}

	public void restoreState() {
		MyLog.d(TAG, "restoreInstanceState");
		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(mContext);
		String locationId = sharedPref.getString(
				PreferenceUtils.KEY_LOCATION_ID, "");
		if (locationId.equals("")) { // no default location, unbundle from asset
			locationId = unbundleLocationAsset();
		}
		loadLocation(locationId, sharedPref);

		mStartTime = sharedPref
				.getLong(PreferenceUtils.KEY_START_TIME,
						Calendar.getInstance(mCalendar.getTimeZone())
								.getTimeInMillis());
		MyLog.i(TAG, "Restored mStartTime " + mStartTime);
		mUseCustomTime = sharedPref.getBoolean(
				PreferenceUtils.KEY_USE_CUSTOM_TIME, false);
		mCustomHour = sharedPref.getInt(PreferenceUtils.KEY_CUSTOM_HOUR, 0);
		mCustomMinute = sharedPref.getInt(PreferenceUtils.KEY_CUSTOM_MINUTE, 0);
		mCalendar.setTimeInMillis(mStartTime);
		setDateFromCalendar();
		mStartPageLayout = PreferenceUtils.getStartPageLayout(mContext);
	}

	private void loadLocation(String locationId, SharedPreferences sharedPref) {
		BufferedInputStream is = null;
		try {
			is = new BufferedInputStream(mContext.openFileInput(locationId
					+ ".dat"), STREAM_BUFFER_SIZE);
			mLocationDatafile = new LocationsDataFile(is);
		} catch (FileNotFoundException e) {
			locationId = PreferenceUtils.getSortedLocations(mContext)
					.firstKey();
			try {
				is = new BufferedInputStream(mContext.openFileInput(locationId
						+ ".dat"), STREAM_BUFFER_SIZE);
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
		
		MyLog.d(TAG, "Location: " + mLocationDatafile.mStartYear + "-"
				+ mLocationDatafile.mStartMonth + "-"
				+ mLocationDatafile.mStartDay + ", "
				+ mLocationDatafile.mDayCount);

		mCalendar.set(mCommonDatafile.mStartYear,
				mCommonDatafile.mStartMonth, mCommonDatafile.mStartDay, 0,
				0, 0);
		long commonStart = mCalendar.getTime().getTime();
		long commonFinal = commonStart + mCommonDatafile.mDayCount * MSECINDAY;

		mCalendar.set(mLocationDatafile.mStartYear,
				mLocationDatafile.mStartMonth, mLocationDatafile.mStartDay, 0,
				0, 0);
		long locationStart = mCalendar.getTime().getTime();
		long locationFinal = locationStart + mLocationDatafile.mDayCount * MSECINDAY;
		
		mStartJD = Math.max(commonStart, locationStart);
		mFinalJD = Math.min(commonFinal, locationFinal);
		
		if (mStartJD > mFinalJD) { // invalid range
			mStartJD = mFinalJD = 0;
		}

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
				lastLocationId = String.format("%08X", datafile.mCityId);
				MyLog.i(TAG, "Unbundle: " + index + ", " + lastLocationId + " "
						+ datafile.mCity);
				OutputStream out = null;
				try {
					out = new BufferedOutputStream(mContext.openFileOutput(
							lastLocationId + ".dat", Context.MODE_PRIVATE),
							STREAM_BUFFER_SIZE);
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

	public boolean changeDate(int deltaDays) {
		// stick to noon to determine date
		long newDate = mStartTime + MSECINDAY * deltaDays + MSECINDAY / 2;
		if (newDate < mStartJD || newDate > mFinalJD)
			return false;
		mEventCache.clear();
		mStartTime = newDate;
		mCalendar.setTimeInMillis(mStartTime);
		setDateFromCalendar();
		return true;
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

	public void prepareCalculation() {
		mCalendar.set(mYear, mMonth, mDay, 0, 0, 0);
		mCalendar.set(Calendar.MILLISECOND, 0);
		mStartTime = mCalendar.getTimeInMillis();
		mEndTime = mStartTime + MSECINDAY - Event.ROUNDING_MSEC;
		Event.setTimeRange(mStartTime, mEndTime);
		mEventCache.clear();
	}

	public void calculateAll() {
		MyLog.d(TAG, "Calculate all for " + mYear + "-" + mMonth + "-" + mDay);
		for (StartPageItem item : mStartPageLayout) {
			if (item.mIsEnabled)
				calculate(START_PAGE_ITEM_SEQ[item.mIndex]);
		}
	}

	public SummaryItem calculate(int key) {
		ArrayList<Event> events = null;
		switch (key) {
		case Event.EV_VOC:
			events = calculateVOCs();
			break;
		case Event.EV_VIA_COMBUSTA:
			events = calculateVC();
			break;
		case Event.EV_SUN_DEGREE:
			events = calculateSunDegree();
			break;
		case Event.EV_MOON_SIGN:
			events = calculateMoonSign();
			break;
		case Event.EV_PLANET_HOUR:
			events = calculatePlanetaryHours();
			break;
		case Event.EV_ASP_EXACT:
			events = calculateAspects();
			break;
		case Event.EV_MOON_MOVE:
			events = calculateMoonMove();
			break;
		case Event.EV_TITHI:
			events = calculateTithis();
			break;
		case Event.EV_RETROGRADE:
			events = calculateRetrogrades();
			break;
		default:
			return null;
		}
		SummaryItem si = new SummaryItem(key, events);
		mEventCache.add(si);
		return si;
		// v.add(new SummaryItem(Event.EV_SUN_RISESET,
		// getRiseSet(Event.SE_SUN)));
		// v.add(new SummaryItem(Event.EV_MOON_RISESET,
		// getRiseSet(Event.SE_MOON)));
	}

	private ArrayList<Event> calculateVOCs() {
		return getEventsOnPeriod(Event.EV_VOC, Event.SE_MOON, false,
				mStartTime, mEndTime, 0);
	}

	private ArrayList<Event> calculateVC() {
		return getEventsOnPeriod(Event.EV_VIA_COMBUSTA, Event.SE_MOON, false,
				mStartTime, mEndTime, 0);
	}

	private ArrayList<Event> calculateSunDegree() {
		return getEventsOnPeriod(Event.EV_DEGREE_PASS, Event.SE_SUN, false,
				mStartTime, mEndTime, 0);
	}

	private ArrayList<Event> calculateMoonSign() {
		return getEventsOnPeriod(Event.EV_SIGN_ENTER, Event.SE_MOON, false,
				mStartTime, mEndTime, 0);
	}

	private ArrayList<Event> calculateTithis() {
		return getEventsOnPeriod(Event.EV_TITHI, Event.SE_MOON, false,
				mStartTime, mEndTime, 0);
	}

	private ArrayList<Event> calculatePlanetaryHours() {
		ArrayList<Event> sunRises = getEventsOnPeriod(Event.EV_RISE,
				Event.SE_SUN, true, mStartTime - MSECINDAY, mEndTime
						+ MSECINDAY, 0);
		ArrayList<Event> sunSets = getEventsOnPeriod(Event.EV_SET,
				Event.SE_SUN, true, mStartTime - MSECINDAY, mEndTime
						+ MSECINDAY, 0);

		ArrayList<Event> result = new ArrayList<Event>();
		if (sunRises.size() < 3 || (sunRises.size() != sunSets.size()))
			return result;
		
		for (int i = 0; i < sunRises.size(); ++i)
			sunRises.get(i).mDate[1] = sunSets.get(i).mDate[0];

		getPlanetaryHours(result, sunRises.get(0), sunRises.get(1));
		getPlanetaryHours(result, sunRises.get(1), sunRises.get(2));
		return result;
	}

	private void getPlanetaryHours(ArrayList<Event> result,
			Event currentSunRise, Event nextSunRise) {
		int startHour = WEEK_START_HOUR[mCalendar.get(Calendar.DAY_OF_WEEK) - 1];
		final long dayHour = (currentSunRise.mDate[1] - currentSunRise.mDate[0]) / 12;
		final long nightHour = (nextSunRise.mDate[0] - currentSunRise.mDate[1]) / 12;
		long st = currentSunRise.mDate[0];
		for (int i = 0; i < 24; ++i) {
			Event ev = new Event(st - (st % Event.ROUNDING_MSEC),
					PLANET_HOUR_SEQUENCE[startHour % 7]);
			ev.mEvtype = Event.EV_PLANET_HOUR;
			st += i < 12 ? dayHour : nightHour;
			ev.mDate[1] = st - Event.ROUNDING_MSEC; // exclude last minute
			ev.mDate[1] -= (ev.mDate[1] % Event.ROUNDING_MSEC);
			if (ev.isInPeriod(mStartTime, mEndTime, false))
				result.add(ev);
			++startHour;
		}
	}

	private ArrayList<Event> calculateAspects() {
		return getAspectsOnPeriod(-1, mStartTime, mEndTime);
	}

	private ArrayList<Event> calculateMoonMove() {
		ArrayList<Event> asp = getEventsOnPeriod(Event.EV_SIGN_ENTER,
				Event.SE_MOON, true, mStartTime - MSECINDAY * 2, mEndTime
						+ MSECINDAY * 4, 0);
		ArrayList<Event> moonMoveVec = getAspectsOnPeriod(Event.SE_MOON,
				mStartTime - MSECINDAY * 2, mEndTime + MSECINDAY * 2);
		
		if (asp.isEmpty() || moonMoveVec.isEmpty())
			return new ArrayList<Event>();
		
		mergeEvents(moonMoveVec, asp, true);
		asp.clear();
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
		moonMoveVec.clear();
		if (id1 == -1)
			return moonMoveVec;
		
		for (int i = id1; i <= id2; i++)
			moonMoveVec.add(asp.get(i));

		int sz = moonMoveVec.size() - 1;
		int idx = 1;
		for (int i = 0; i < sz; i++) {
			Event evprev = moonMoveVec.get(idx - 1);
			long dd = (evprev.mEvtype == Event.EV_SIGN_ENTER) ? evprev.mDate[0]
					: evprev.mDate[1];
			Event ev = new Event(dd, -1);
			ev.mEvtype = Event.EV_MOON_MOVE;
			ev.mDate[1] = moonMoveVec.get(idx).mDate[0] - Event.ROUNDING_MSEC;
			ev.mPlanet0 = evprev.mPlanet1;
			ev.mPlanet1 = moonMoveVec.get(idx).mPlanet1;
			moonMoveVec.add(idx, ev);
			idx += 2;
		}
		sz = moonMoveVec.size();
		for (int i = 0; i < sz; ++i) {
			Event e = moonMoveVec.get(i);
			if (e.mEvtype == Event.EV_MOON_MOVE) {
				int j = i - 1;
				while (j >= 0) {
					Event prev = moonMoveVec.get(j);
					if (prev.mEvtype != Event.EV_MOON_MOVE) {
						byte planet = prev.mPlanet1;
						if (planet <= Event.SE_SATURN) {
							e.mPlanet0 = planet;
							break;
						}
					}
					--j;
				}
				j = i + 1;
				while (j < sz) {
					Event next = moonMoveVec.get(j);
					if (next.mEvtype != Event.EV_MOON_MOVE) {
						byte planet = next.mPlanet1;
						if (planet <= Event.SE_SATURN) {
							e.mPlanet1 = planet;
							break;
						}
					}
					++j;
				}
			} else if (e.mEvtype == Event.EV_ASP_EXACT)
				e.mEvtype = Event.EV_ASP_EXACT_MOON;
		}
		return moonMoveVec;
	}

	private static void mergeEvents(ArrayList<Event> dest,
			ArrayList<Event> add, boolean isSort) {
		for (Event ev : add) {
			if (isSort) {
				int idx = 0;
				final long dat = ev.mDate[0];
				final int sz = dest.size();
				while (idx < sz && dat > dest.get(idx).mDate[0]) {
					++idx;
				}
				dest.add(idx, ev);
			} else {
				dest.add(ev);
			}
		}
	}

	private ArrayList<Event> calculateRetrogrades() {
		ArrayList<Event> result = new ArrayList<Event>();
		for (int planet = Event.SE_MERCURY; planet <= Event.SE_PLUTO; ++planet) {
			ArrayList<Event> v = getEventsOnPeriod(Event.EV_RETROGRADE, planet,
					false, mStartTime, mEndTime, 0);
			if (!v.isEmpty())
				result.addAll(v);
		}
		return result;
	}

	private ArrayList<Event> getRiseSet(int planet, long startTime, long endTime) {
		ArrayList<Event> result = new ArrayList<Event>();
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
			MyLog.d("dummy", dummy.toString());
			for (int i = 0; i < cnt; i++) {
				MyLog.d("getEventOnPeriod", mEvents[i].toString());
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

	private ArrayList<Event> getAspectsOnPeriod(int planet, long startTime,
			long endTime) {
		ArrayList<Event> result = new ArrayList<Event>();
		boolean flag = false;
		int cnt = getEvents(Event.EV_ASP_EXACT,
				planet == Event.SE_MOON ? Event.SE_MOON : -1, startTime,
				endTime);
		for (int i = 0; i < cnt; i++) {
			final Event ev = mEvents[i];
			if (planet == -1 || ev.mPlanet0 == planet || ev.mPlanet1 == planet) {
				if (ev.isDateBetween(0, startTime, endTime)) {
					flag = true;
					result.add(ev);
				}
			} else if (flag) {
				break;
			}
		}
		return result;
	}

	public void setTodayDate() {
		mCalendar = Calendar.getInstance(mCalendar.getTimeZone());
		setDateFromCalendar();
	}

	public String getLocationName() {
		if (mLocationDatafile == null)
			return null;
		return mLocationDatafile.mCity;
	}

	public String getCurrentDateString() {
		String s = (String) DateFormat.format(mTitleDateFormat, mCalendar);
		return Character.toUpperCase(s.charAt(0)) + s.substring(1);
	}

	public long getCustomTime() {
		Calendar calendar = Calendar.getInstance(mCalendar.getTimeZone());
		if (mUseCustomTime) {
			calendar.set(mYear, mMonth, mDay, mCustomHour, mCustomMinute);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
		} else {
			calendar.set(Calendar.YEAR, mYear);
			calendar.set(Calendar.MONTH, mMonth);
			calendar.set(Calendar.DAY_OF_MONTH, mDay);
			mCurrentHour = calendar.get(Calendar.HOUR_OF_DAY);
			mCurrentMinute = calendar.get(Calendar.MINUTE);
		}
		MyLog.d("getCustomTime",
				(String) DateFormat.format("dd MMMM yyyy, kk:mm:ss", calendar));
		return calendar.getTimeInMillis();
	}

	public long getCurrentTime() {
		if (!mUseCustomTime) {
			Calendar calendar = Calendar.getInstance(mCalendar.getTimeZone());
			mCurrentHour = calendar.get(Calendar.HOUR_OF_DAY);
			mCurrentMinute = calendar.get(Calendar.MINUTE);
			MyLog.d("getCurrentTime", (String) DateFormat.format(
					"dd MMMM yyyy, kk:mm:ss", calendar));
			return calendar.getTimeInMillis();
		}
		return 0;
	}

	// mStartJD = calendar.getTime().getTime();
	// mFinalJD = mStartJD + mDayCount * MSECINDAY;

	public void setCustomTime(int hour, int min) {
		mCustomHour = hour;
		mCustomMinute = min;
	}

	public int getCustomHour() {
		return mCustomHour;
	}

	public int getCustomMinute() {
		return mCustomMinute;
	}

	public String getHighlightTimeString() {
		if (mUseCustomTime)
			return String.format("%02d:%02d", mCustomHour, mCustomMinute);
		return String.format("%02d:%02d", mCurrentHour, mCurrentMinute);
	}

	public boolean isInCurrentDay(long date) {
		return Event.dateBetween(date, mStartTime, mEndTime) == 0;
	}

	@Override
	protected void addEvent(int idx, BaseEvent event) {
		mEvents[idx] = new Event(event);
	}
}
