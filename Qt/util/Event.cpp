#include "Event.h"
#include <stdio.h>

QHash<int, int> Event::ASPECT_GOODNESS;
QHash<int, int> Event::ASPECT_MAP;

long Event::mPeriod0;
long Event::mPeriod1;

QString Event::mMonthAbbrDayDateFormat;

QDateTime Event::mCalendar;

Event::Event()
: mEvtype(0)
, mPlanet1(-1)
, mDegree(127)
{
}

Event::Event(long date, int planet)
: mPlanet0(planet)
, mPlanet1(-1)
, mDegree(127)
{
	mDate[0] = mDate[1] = date;
}

Event::Event(int evType, long date0, long date1, int planet0, int planet1, int degree)
: mEvtype(evType)
, mPlanet0(planet0)
, mPlanet1(planet1)
, mDegree(degree)
{
	mDate[0] = date0;
	mDate[1] = date1;
}

int Event::getDegree() const
{
	return mDegree & 0x3ff;
}

int Event::getDegType() const
{
	return (mDegree >> 14) & 0x3;
}

short Event::getFullDegree() const
{
	return mDegree;
}

void Event::setFullDegree(short degree)
{
	mDegree = degree;
}

QString Event::getEvTypeStr() const
{
	return EVENT_TYPE_STR[mEvtype];	
}

int Event::dateBetween(long date0, long start, long end)
{
	if (date0 < start)
		return -1;
	if (date0 > end)
		return 1;
	return 0;
}

bool Event::isDateBetween(int index, long start, long end) const
{
	long dat = mDate[index];
	return start <= dat && dat < end;
}

QString Event::toString() const
{
	QString s;
	QTextStream ts(&s);
	ts << "Event " << ": (" << mEvtype << " " << getEvTypeStr() << " "
			<< long2String(mDate[0], DEFAULT_DATE_FORMAT, false) << " - "
			<< long2String(mDate[1], DEFAULT_DATE_FORMAT, false) << " "
			<< getPlanetName(mPlanet0) << "-" << getPlanetName(mPlanet1)
			<< " d " << mDegree << ")";
	return s;
}

QString Event::getPlanetName(char planet)
{
	return PLANET_STR[planet + 1];
}

QString Event::long2String(long date0, const QString& dateFormat, bool h24) const
{
	mCalendar.setTime_t(date0);
	QString s;
	if (!s.isEmpty()) {
		s += formatDate(dateFormat, date0);
		s += " ";
	}
	int hh = 0, mm = 0;
	hh = mCalendar.time().hour();
	mm = mCalendar.time().minute();

	if (h24 && hh + mm == 0) {
		hh = 24;
	}
	char buf[10];
	sprintf (buf, "%02d:%02d", hh, mm);
	s += buf;
	return s;
}

void Event::setTimeZone(const QTimeZone& timezone)
{
	mCalendar.setTimeZone(timezone);
}

QString Event::formatDate(const QString& dateFormat, long date)
{
//	SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
//	return sdf.format(new Date(date));
}

//	class EventDate0Comparator implements Comparator<Event> {
//		int compare(Event o1, Event o2) {
//			return (int) (o1.mDate[0] - o2.mDate[0]);
//		}
//	}

void Event::setTimeRange(long date0, long date1)
{
	mPeriod0 = date0;
	mPeriod1 = date1;
}

QString Event::normalizedRangeString() const
{
	long date0 = mDate[0], date1 = mDate[1];
	if (USE_EXACT_RANGE) {
		if (date0 < mPeriod0)
			date0 = mPeriod0;
		if (date1 > mPeriod1)
			date1 = mPeriod1;

		return long2String(date0, "", false) + " - "
				+ long2String(date1, "", true);
	}

	bool isTillRequired = date0 < mPeriod0;
	bool isSinceRequired = date1 > mPeriod1;

//	if (isTillRequired && isSinceRequired)
//		return mContext.getString(R.string.norm_range_whole_day);

//	if (isTillRequired)
//		return mContext.getString(R.string.norm_range_arrow) + " "
//				+ long2String(date1, "", true);

//	if (isSinceRequired)
//		return long2String(date0, "", false) + " "
//				+ mContext.getString(R.string.norm_range_arrow);

	return long2String(date0, "", false) + " - "
			+ long2String(date1, "", true);
}

void Event::initialize()
{
	ASPECT_GOODNESS[0] = 0;
	ASPECT_GOODNESS[180] = 1;
	ASPECT_GOODNESS[120] = 2;
	ASPECT_GOODNESS[90] = 1;
	ASPECT_GOODNESS[60] = 2;
	ASPECT_GOODNESS[45] = 2;

	ASPECT_MAP[0] = 0;
	ASPECT_MAP[180] = 1;
	ASPECT_MAP[120] = 2;
	ASPECT_MAP[90] = 3;
	ASPECT_MAP[60] = 4;
	ASPECT_MAP[45] = 5;
}

bool Event::isInPeriod(long start, long end, bool special) const
{
	if (mDate[0] == 0) {
		return false;
	}
	int f = dateBetween(mDate[0], start, end) + dateBetween(mDate[1], start, end);
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

//public static void setContext(Context context) {
//	mContext = context;
//	mMonthAbbrDayDateFormat = mContext
//			.getString(R.string.month_abbr_day_date_format);
//}

QDebug operator<<(QDebug dbg, const Event& event)
{
	dbg << event.toString();
	return dbg;
}
