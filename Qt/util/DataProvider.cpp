#include "DataProvider.h"
#include "Event.h"
#include "StartPageItem.h"
#include "CommonDataFile.h"
#include "LocationsDataFile.h"
#include "LocationBundle.h"
#include "SubDataInfo.h"
#include "AmaxDatabase.h"
#include "AmaxSettings.h"
#include "SummaryModel.h"
#include "view/SummaryItem.h"

#include <QtSql/QSqlQuery>
#include <QBuffer>
#include <QDebug>
#include <QTimeZone>
#include <QFile>
#include <QResource>
#include <stdio.h>

const long MSECINDAY = 86400;

// constants used in event map
const int WEEK_START_HOUR[] = { 0, 3, 6, 2, 5, 1, 4 };
const int PLANET_HOUR_SEQUENCE[] = { Event::SE_SUN,
		Event::SE_VENUS, Event::SE_MERCURY, Event::SE_MOON, Event::SE_SATURN,
		Event::SE_JUPITER, Event::SE_MARS };

DataProvider::DataProvider()
: mCustomHour(0)
, mCustomMinute(0)
, mUseCustomTime(false)
, mCommonDatafile(NULL)
, mLocationDatafile(NULL)
{
//	mContext = context;
	mTitleDateFormat = tr("title_date_format");
	mDatabase = AmaxDatabaseSingleton::instance();
	mSettings = SettingsSingleton::instance();
	mSummaryModel = new SummaryModel(this);
}

DataProvider::~DataProvider()
{
	delete mSummaryModel;
}

long DataProvider::getCommonId()
{
	return mPeriodId;
}

long DataProvider::getStartJD()
{
	return mStartJD;
}

long DataProvider::getFinalJD()
{
	return mFinalJD;
}

int DataProvider::getYear()
{
	return mYear;
}

int DataProvider::getMonth()
{
	return mMonth;
}

int DataProvider::getDay()
{
	return mDay;
}

void DataProvider::saveState()
{
	mSettings->setStartTime(mStartTime);
	mSettings->setCustomHour(mCustomHour);
	mSettings->setCustomMinute(mCustomMinute);
}

void DataProvider::restoreState()
{
//	MyLog.d(TAG, "restoreInstanceState");
	
	mPeriodId = mSettings->getPeriodId();
	qDebug() << "read KEY_PERIOD_ID=" << mPeriodId;
	if (mPeriodId == 0) { // no default period, unbundle from asset
		mPeriodId = 1;
		unbundlePeriodAsset();
	}
	loadPeriod();
	mSettings->setPeriodId(mPeriodId);
	qDebug() << "write KEY_PERIOD_ID=" << mPeriodId;

	QString cityKey = mSettings->getCityKey();
	if (cityKey == "") { // no default location, unbundle from asset
		cityKey = unbundleLocationAsset();
	}

	loadLocation(cityKey);

	QTimeZone timezone = mCalendar.timeZone();
	mStartTime = mSettings->getStartTime(timezone);
	//MyLog.i(TAG, "Restored mStartTime " + mStartTime);
	mUseCustomTime = mSettings->getUseCustomTime();
	mCustomHour = mSettings->getCustomHour();
	mCustomMinute = mSettings->getCustomMinute();
	mCalendar.setTime_t(mStartTime);
	setDateFromCalendar();
	mStartPageLayout = mSettings->getStartPageLayout();	
}

QString DataProvider::makeLocationFilename(const QString& cityKey)
{
	return mPeriodStr + cityKey;
}

void DataProvider::changeDate(int deltaDays)
{
	// stick to noon to determine date
	long newDate = mStartTime + MSECINDAY * deltaDays + MSECINDAY / 2;
	mEventCache.clear();
	mStartTime = newDate;
	qDebug() << "mStartTime=" << mStartTime;
	mCalendar.setTime_t(mStartTime);
	setDateFromCalendar();
}

bool DataProvider::hasPeriod()
{
	return (mStartTime >= mStartJD && mStartTime < mFinalJD);
}

bool DataProvider::hasLocation()
{
	return mLocationDatafile != NULL;
}

void DataProvider::setDate(int year, int month, int day)
{
	mYear = year;
	mMonth = month;
	mDay = day;
	mCalendar.setDate(QDate(mYear, mMonth, mDay));
	mCalendar.setTime(QTime());
	setDateFromCalendar();
}

void DataProvider::setDateFromCalendar()
{
	QDate date = mCalendar.date();
	mYear = date.year();
	mMonth = date.month();
	mDay = date.day();
	mCalendar.setDate(QDate(mYear, mMonth, mDay));
	mCalendar.setTime(QTime());
	mStartTime = mCalendar.toTime_t();
	qDebug() << "setDateFromCalendar: " << mStartTime << "-" << mEndTime << "; " << mYear << "-" << mMonth << "-" << mDay;
	if (!hasPeriod()) {
		QSqlQuery* q = mDatabase->getPeriodByDate(mYear, mMonth);
		if (q->next()) {
			mSettings->setPeriodId(q->value(0).toInt());
		}
	}
}

void DataProvider::prepareCalculation()
{
	setDate(mYear, mMonth, mDay);
	mStartTime = mCalendar.toTime_t();
	mEndTime = mStartTime + MSECINDAY - Event::ROUNDING_MSEC;
	Event::setTimeRange(mStartTime, mEndTime);
}

void DataProvider::calculateAll()
{
	mSummaryModel->beginChange();
	mEventCache.clear();
	qDebug() << "Calculate all for " << mYear << "-" << mMonth << "-" << mDay;
	for (int i = 0; i < mStartPageLayout->size(); ++i) {
		const StartPageItem& item = mStartPageLayout->at(i);
		if (item.mIsEnabled) {
			SummaryItem* si = calculate(START_PAGE_ITEM_SEQ[item.mIndex]);
			if (si) {
				qDebug() << si << *si;
				mEventCache.append(si);
			}
		}
	}
	mSummaryModel->endChange();
}

SummaryItem* DataProvider::calculate(int key)
{
	QList<Event> events;
	switch (key) {
	case Event::EV_VOC:
		events = calculateVOCs();
		break;
	case Event::EV_VIA_COMBUSTA:
		events = calculateVC();
		break;
	case Event::EV_SUN_DEGREE:
		events = calculateSunDegree();
		break;
	case Event::EV_MOON_SIGN:
		events = calculateMoonSign();
		break;
	case Event::EV_PLANET_HOUR:
		events = calculatePlanetaryHours();
		break;
	case Event::EV_ASP_EXACT:
		events = calculateAspects();
		break;
	case Event::EV_MOON_MOVE:
		events = calculateMoonMove();
		break;
	case Event::EV_TITHI:
		events = calculateTithis();
		break;
	case Event::EV_RETROGRADE:
		events = calculateRetrogrades();
		break;
	default:
		return NULL;
	}
	return new SummaryItem(key, events);
	// v.add(new SummaryItem(Event::EV_SUN_RISESET,
	// getRiseSet(Event::SE_SUN)));
	// v.add(new SummaryItem(Event::EV_MOON_RISESET,
	// getRiseSet(Event::SE_MOON)));
}

void DataProvider::setTodayDate()
{
	mCalendar = QDateTime::currentDateTime();
	setDateFromCalendar();
}

QString DataProvider::getCityName()
{
	if (mLocationDatafile == NULL) {
		QSqlQuery* q = mDatabase->getCity(mSettings->getCityKey());
		if (q->next())
			return q->value(1).toString();
		else
			return "";
	}
	return mLocationDatafile->mCity;
}

QString DataProvider::getCurrentDateString()
{
	return mCalendar.toString("ddd, dd MMM yyyy");//mTitleDateFormat);
}

long DataProvider::getCustomTime()
{
	QDateTime calendar = QDateTime::currentDateTime();
	calendar.setDate(QDate(mYear, mMonth, mDay));
	if (mUseCustomTime) {
		calendar.setTime(QTime(mCustomHour, mCustomMinute, 0, 0));
	}
	else {
		QTime time = calendar.time();
		mCurrentHour = time.hour();
		mCurrentMinute = time.minute();
	}
//	MyLog.d("getCustomTime",
//			(QString) DateFormat.format("dd MMMM yyyy, kk:mm:ss", calendar));
	return calendar.toTime_t();
}

long DataProvider::getCurrentTime()
{
	if (!mUseCustomTime) {
		QDateTime calendar = QDateTime::currentDateTime();
		QTime time = calendar.time();
		mCurrentHour = time.hour();
		mCurrentMinute = time.minute();
//		MyLog.d("getCurrentTime", (QString) DateFormat.format(
//				"dd MMMM yyyy, kk:mm:ss", calendar));
		return calendar.toTime_t();
	}
	return 0;
}

void DataProvider::setCustomTime(int hour, int min)
{
	mCustomHour = hour;
	mCustomMinute = min;
}

int DataProvider::getCustomHour()
{
	return mCustomHour;
}

int DataProvider::getCustomMinute()
{
	return mCustomMinute;
}

QString DataProvider::getHighlightTimeString()
{
	char buf[10];
	if (mUseCustomTime)
		sprintf(buf, "%02d:%02d", mCustomHour, mCustomMinute);
	else
		sprintf(buf, "%02d:%02d", mCurrentHour, mCurrentMinute);
	return buf;
}

bool DataProvider::isInCurrentDay(long date)
{
	return Event::dateBetween(date, mStartTime, mEndTime) == 0;
}

QString DataProvider::makePeriodCaption(int year, int startMonth, int monthCount)
{
	QDateTime calendar(QDate(year, startMonth, 1));
//	QString text = (QString) DateFormat.format("yyyy MMM ", calendar);
//	if (monthCount > 1) {
//		calendar.addMonths(monthCount);
//		text += DateFormat.format("- MMM", calendar);
//	}
//	return text;
}

QList<Event> DataProvider::getEventsOnPeriod(int evtype, int planet, bool special, long dayStart, long dayEnd, int value)
{
	bool flag = false;
	QList<Event> result;
	int cnt = getEvents(evtype, planet, dayStart, dayEnd);
	for (int i = 0; i < cnt; i++) {
		Event ev(mEvents[i]);
		if (ev.isInPeriod(dayStart, dayEnd, special)) {
			flag = true;
			if (value > 0) {
				ev.setFullDegree(value);
			}
			result.append(ev);
		}
		else if (flag) {
			break;
		}
	}
	return result;
}

int DataProvider::getEvents(int evtype, int planet, long dayStart, long dayEnd)
{
	switch (evtype) {
	case Event::EV_ASTRORISE:
	case Event::EV_ASTROSET:
	case Event::EV_RISE:
	case Event::EV_SET:
	case Event::EV_ASCAPHETICS:
		return read(&mLocationDatafile->mData, evtype, planet, false,
				dayStart, dayEnd, mFinalJD, NULL);
	default:
		return read(&mCommonDatafile->mData, evtype, planet, true, dayStart,
				dayEnd, mFinalJD, NULL);
	}
}

void DataProvider::loadPeriod()
{
	fillCommonIds();
	QFile in(SettingsSingleton::instance()->dir() + mPeriodStr);
	if (in.exists()) {
		mCommonDatafile = new CommonDataFile(in.fileName(), false);
	}
	else {
		mDatabase->getPeriodStringAndKey(1, mPeriodStr, mPeriodKey);
		QFile in(SettingsSingleton::instance()->dir() + mPeriodStr);
		mCommonDatafile = new CommonDataFile(in.fileName(), false);
	}
	qDebug() << "Common: " << mCommonDatafile->mStartYear << "-"
		<< mCommonDatafile->mStartMonth << "-"
		<< mCommonDatafile->mStartDay << ", "
		<< mCommonDatafile->mDayCount << " > " << mPeriodKey;
}

void DataProvider::unbundlePeriodAsset()
{
	fillCommonIds();
	QFile in(":/common.dat");
	qDebug() << in.exists();
	QString target = SettingsSingleton::instance()->dir() + "/" + mPeriodStr;
	if (!in.copy(target))
		qDebug() << "Could not copy to " << target;
}

void DataProvider::fillCommonIds()
{
	mDatabase->getPeriodStringAndKey(mPeriodId, mPeriodStr, mPeriodKey);
}

void DataProvider::loadLocation(const QString& cityKey)
{
	makeLocationDatafile(cityKey);
	mSettings->setCityKey(cityKey);

	long locationStart = 0;
	long locationFinal = 0;
	
	if (mLocationDatafile) {
		QTimeZone tz(mLocationDatafile->mTimezone.toLatin1());
		mCalendar.setTimeZone(tz);
		Event::setTimeZone(tz);

		qDebug()
			<< "Location: "
			<< mLocationDatafile->mStartYear	<< "-"
			<< mLocationDatafile->mStartMonth << "-"
			<< mLocationDatafile->mStartDay << ", "
			<< mLocationDatafile->mMonthCount << " > " << cityKey;
		
		mCalendar.setDate(QDate(mLocationDatafile->mStartYear, mLocationDatafile->mStartMonth, mLocationDatafile->mStartDay));
		mCalendar.setTime(QTime());

		locationStart = mCalendar.toTime_t();
		mCalendar = mCalendar.addMonths(mLocationDatafile->mMonthCount);
		locationFinal = mCalendar.toTime_t();
	}

	mCalendar.setDate(QDate(mCommonDatafile->mStartYear, mCommonDatafile->mStartMonth, mCommonDatafile->mStartDay));
	mCalendar.setTime(QTime());

	long commonStart = mCalendar.toTime_t();
	mCalendar = mCalendar.addMonths(mCommonDatafile->mMonthCount);
	long commonFinal = mCalendar.toTime_t();

	qDebug() << "Common: " << commonStart << "-" << commonFinal
		<< "; location: " << locationStart << "-" << locationFinal;
	
	mStartJD = commonStart;
	mFinalJD = commonFinal;
}

void DataProvider::makeLocationDatafile(const QString& cityKey)
{
	mLocationDatafile = NULL;
	QString filename = makeLocationFilename(cityKey);
	QFile in(mSettings->dir() + filename);
	in.open(QIODevice::ReadOnly);
	QByteArray ba = in.readAll();
	mLocationDatafile = new LocationsDataFile(ba);
	in.close();
}

QString DataProvider::unbundleLocationAsset()
{
	QString lastCityKey = "";
	LocationBundle locBundle(":/locations.dat");
	int index = 0;
	for (int i = 0; i < locBundle.mRecordCount; ++i) {
		QByteArray buffer = locBundle.extractLocation(index);
		LocationsDataFile datafile(buffer);
		char buf[20];
		sprintf (buf, "%08x", datafile.mCityKey);
		lastCityKey = buf;
		qDebug() << "Unbundle: " << index << ", " << lastCityKey << " " << datafile.mCity;
		QString filename = makeLocationFilename(lastCityKey);
		QFile out(SettingsSingleton::instance()->dir() + filename);
		out.open(QIODevice::WriteOnly);
		out.write(buffer);
		out.close();
		++index;
	}
	return lastCityKey;
}


QList<Event> DataProvider::calculateVOCs()
{
	return getEventsOnPeriod(Event::EV_VOC, Event::SE_MOON, false,
			mStartTime, mEndTime, 0);
}

QList<Event> DataProvider::calculateVC()
{
	return getEventsOnPeriod(Event::EV_VIA_COMBUSTA, Event::SE_MOON, false,
			mStartTime, mEndTime, 0);
}

QList<Event> DataProvider::calculateSunDegree()
{
	return getEventsOnPeriod(Event::EV_DEGREE_PASS, Event::SE_SUN, false,
			mStartTime, mEndTime, 0);
}

QList<Event> DataProvider::calculateMoonSign()
{
	return getEventsOnPeriod(Event::EV_SIGN_ENTER, Event::SE_MOON, false,
			mStartTime, mEndTime, 0);
}

QList<Event> DataProvider::calculateTithis()
{
	return getEventsOnPeriod(Event::EV_TITHI, Event::SE_MOON, false,
			mStartTime, mEndTime, 0);
}

QList<Event> DataProvider::calculatePlanetaryHours()
{
	QList<Event> sunRises = getEventsOnPeriod(Event::EV_RISE,
			Event::SE_SUN, true, mStartTime - MSECINDAY, mEndTime
					+ MSECINDAY, 0);
	QList<Event> sunSets = getEventsOnPeriod(Event::EV_SET,
			Event::SE_SUN, true, mStartTime - MSECINDAY, mEndTime
					+ MSECINDAY, 0);

	QList<Event> result;
	if (sunRises.size() < 3 || (sunRises.size() != sunSets.size()))
		return result;

	for (int i = 0; i < sunRises.size(); ++i)
		sunRises[i].mDate[1] = sunSets[i].mDate[0];

	getPlanetaryHours(result, sunRises[0], sunRises[1]);
	getPlanetaryHours(result, sunRises[1], sunRises[2]);
	return result;
}

void DataProvider::getPlanetaryHours(QList<Event>& result, const Event& currentSunRise, const Event& nextSunRise)
{
	int startHour = WEEK_START_HOUR[mCalendar.date().dayOfWeek() - 1];
	long dayHour = (currentSunRise.mDate[1] - currentSunRise.mDate[0]) / 12;
	long nightHour = (nextSunRise.mDate[0] - currentSunRise.mDate[1]) / 12;
	long st = currentSunRise.mDate[0];
	for (int i = 0; i < 24; ++i) {
		Event ev(st - (st % Event::ROUNDING_MSEC), PLANET_HOUR_SEQUENCE[startHour % 7]);
		ev.mEvtype = Event::EV_PLANET_HOUR;
		st += i < 12 ? dayHour : nightHour;
		ev.mDate[1] = st - Event::ROUNDING_MSEC; // exclude last minute
		ev.mDate[1] -= (ev.mDate[1] % Event::ROUNDING_MSEC);
		if (ev.isInPeriod(mStartTime, mEndTime, false))
			result.append(ev);
		++startHour;
	}
}

QList<Event> DataProvider::calculateAspects()
{
	return getAspectsOnPeriod(-1, mStartTime, mEndTime);
}

QList<Event> DataProvider::calculateMoonMove()
{
	QList<Event> asp = getEventsOnPeriod(Event::EV_SIGN_ENTER,
			Event::SE_MOON, true, mStartTime - MSECINDAY * 2, mEndTime
					+ MSECINDAY * 4, 0);
	QList<Event> moonMoveVec = getAspectsOnPeriod(Event::SE_MOON,
			mStartTime - MSECINDAY * 2, mEndTime + MSECINDAY * 2);

	if (asp.isEmpty() || moonMoveVec.isEmpty())
		return QList<Event>();

	mergeEvents(moonMoveVec, asp, true);
	asp.clear();
	mergeEvents(asp, moonMoveVec, false);
	int id1 = -1;
	int id2 = -1;
	int counter = 0;
	for (int i = 0; i < asp.size(); ++i) {
		Event ev(asp[i]);
		long dat = ev.mDate[0];
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
		moonMoveVec.append(asp[i]);

	int sz = moonMoveVec.size() - 1;
	int idx = 1;
	for (int i = 0; i < sz; i++) {
		Event evprev(moonMoveVec[idx - 1]);
		long dd = (evprev.mEvtype == Event::EV_SIGN_ENTER) ? evprev.mDate[0]
				: evprev.mDate[1];
		Event ev(dd, -1);
		ev.mEvtype = Event::EV_MOON_MOVE;
		ev.mDate[1] = moonMoveVec[idx].mDate[0] - Event::ROUNDING_MSEC;
		ev.mPlanet0 = evprev.mPlanet1;
		ev.mPlanet1 = moonMoveVec[idx].mPlanet1;
		moonMoveVec.insert(idx, ev);
		idx += 2;
	}
	sz = moonMoveVec.size();
	for (int i = 0; i < sz; ++i) {
		Event e(moonMoveVec[i]);
		if (e.mEvtype == Event::EV_MOON_MOVE) {
			int j = i - 1;
			while (j >= 0) {
				Event prev(moonMoveVec[j]);
				if (prev.mEvtype != Event::EV_MOON_MOVE) {
					char planet = prev.mPlanet1;
					if (planet <= Event::SE_SATURN) {
						e.mPlanet0 = planet;
						break;
					}
				}
				--j;
			}
			j = i + 1;
			while (j < sz) {
				Event next(moonMoveVec[j]);
				if (next.mEvtype != Event::EV_MOON_MOVE) {
					char planet = next.mPlanet1;
					if (planet <= Event::SE_SATURN) {
						e.mPlanet1 = planet;
						break;
					}
				}
				++j;
			}
		} else if (e.mEvtype == Event::EV_ASP_EXACT)
			e.mEvtype = Event::EV_ASP_EXACT_MOON;
	}
	return moonMoveVec;
}

void DataProvider::mergeEvents(QList<Event>& dest, const QList<Event>& add, bool isSort)
{
	for (int i = 0; i < add.size(); ++i) {
		Event ev(add[i]);
		if (isSort) {
			int idx = 0;
			long dat = ev.mDate[0];
			int sz = dest.size();
			while (idx < sz && dat > dest[idx].mDate[0]) {
				++idx;
			}
			dest.insert(idx, ev);
		} else {
			dest.append (ev);
		}
	}
}

QList<Event> DataProvider::calculateRetrogrades()
{
	QList<Event> result;
	for (int planet = Event::SE_MERCURY; planet <= Event::SE_PLUTO; ++planet) {
		QList<Event> v = getEventsOnPeriod(Event::EV_RETROGRADE, planet,
				false, mStartTime, mEndTime, 0);
		if (!v.isEmpty())
			result.append(v);
	}
	return result;
}

QList<Event> DataProvider::getRiseSet(int planet, long startTime, long endTime)
{
	QList<Event> result;
	Event eop = getEventOnPeriod(Event::EV_RISE, planet, true, startTime,
			endTime);
	if (eop.mDate[0] < startTime) {
		eop = Event(0, planet);
	}
	Event eop1 = getEventOnPeriod(Event::EV_SET, planet, false, startTime,
			mEndTime);
	if (eop1.mDate[0] < startTime) {
		eop1 = Event(0, planet);
	}
	eop.mDate[1] = eop1.mDate[0];
	result.append(eop);
	return result;
}

Event DataProvider::getEventOnPeriod(int evType, int planet, bool special, long startTime, long endTime)
{
	int cnt = getEvents(evType, planet, startTime, endTime);
	if (evType == Event::EV_RISE && planet == Event::SE_SUN) {
		//Event* dummy = new Event(startTime, 0);
		//dummy->mDate[1] = endTime;
//		MyLog.d("dummy", dummy_>toString());
//		for (int i = 0; i < cnt; i++) {
//			MyLog.d("getEventOnPeriod", mEvents[i]->toString());
//		}
	}
	for (int i = 0; i < cnt; i++) {
		Event* ev = &mEvents[i];
		if (ev->isInPeriod(startTime, endTime, special)) {
			return *ev;
		}
	}
	return Event();
}

QList<Event> DataProvider::getAspectsOnPeriod(int planet, long startTime, long endTime)
{
	QList<Event> result;
	bool flag = false;
	int cnt = getEvents(Event::EV_ASP_EXACT,
			planet == Event::SE_MOON ? Event::SE_MOON : -1, startTime,
			endTime);
	for (int i = 0; i < cnt; i++) {
		Event ev(mEvents[i]);
		if (planet == -1 || ev.mPlanet0 == planet || ev.mPlanet1 == planet) {
			if (ev.isDateBetween(0, startTime, endTime)) {
				flag = true;
				result.append(ev);
			}
		}
		else if (flag) {
			break;
		}
	}
	return result;
}

int DataProvider::read(QByteArray* data, int evtype, int planet, bool isCommon,
	long dayStart, long dayEnd, long mFinalJD, SubDataInfo* info) {
	QBuffer is (data);
	is.open(QIODevice::ReadOnly);

	int eventsCount = 0;
	int flags = 0;
	int skipOff;
	Event last(0, 0);
	last.mEvtype = evtype;
	int fnext_date2;
	int PERIOD = (evtype == Event::EV_ASCAPHETICS) ? 2 * 60 : 24 * 60;
	int totalCount = 0;
	try {
		while (true) {
			readUnsignedByte(is);
			int rub = readUnsignedByte(is);
			while (evtype != rub) {
				skipOff = readShort(is) - 3;
				skip(is, skipOff);
				readUnsignedByte(is);
				rub = readUnsignedByte(is);
			}
			skipOff = readShort(is);
			flags = readShort(is);
			if (planet == readByte(is)) {
				break;
			} else {
				skip(is, skipOff - 6);
			}
		}
		totalCount = readShort(is);
		int fcumul_date_b = (flags & EF_CUMUL_DATE_B);
		int fcumul_date_w = (flags & EF_CUMUL_DATE_W);
		int fdate = (flags & EF_DATE);
		int fplanet1 = (flags & EF_PLANET1);
		int fplanet2 = (flags & EF_PLANET2);
		int fdegree = (flags & EF_DEGREE);
		int fshort_degree = (flags & EF_SHORT_DEGREE);
		fnext_date2 = (flags & EF_NEXT_DATE2);

		char myplanet0 = planet, myplanet1 = -1;
		int mydgr = 127;
		long mydate0, mydate1;
		int cumul;
		long date = 0;
		for (int i = 0; i < totalCount; i++) {
			if (fcumul_date_b != 0) {
				if (i != 0) {
					cumul = readByte(is);
					date += (cumul + PERIOD) * 60;
				}
				else {
					date = readInt(is);
				}
			}
			else if (fcumul_date_w != 0) {
				if (i != 0) {
					cumul = readShort(is);
					date += (cumul + PERIOD) * 60;
				}
				else {
					date = readInt(is);
				}
			}
			else {
				date = readInt(is);
			}

			mydate0 = date;
			if (fdate != 0)
				mydate1 = (long) readInt(is) - 1;
			else
				mydate1 = mydate0;
			if (fplanet1 != 0)
				myplanet0 = readByte(is);
			if (fplanet2 != 0)
				myplanet1 = readByte(is);
			if (fdegree != 0) {
				if (fshort_degree != 0)
					mydgr = readUnsignedByte(is);
				else
					mydgr = readShort(is);
			}
			if (fnext_date2 != 0) {
				last.mDate[1] = mydate0 - Event::ROUNDING_MSEC;
				mydate1 = mFinalJD;
			}
			if (last.isInPeriod(dayStart, dayEnd, false)) {
				mEvents[eventsCount] = last;
				eventsCount++;
			}
			else {
				if (eventsCount > 0)
					break;
			}
			last.mPlanet0 = myplanet0;
			last.mPlanet1 = myplanet1;
			last.setFullDegree(mydgr);
			last.mDate[0] = mydate0;
			last.mDate[1] = mydate1;
		}
		if (last.isInPeriod(dayStart, dayEnd, false)) {
			mEvents[eventsCount] = last;
			eventsCount++;
		}
	}
	catch (std::exception&){}
	
	is.close();
	
	if (info != NULL) {
		info->mEventType = evtype;
		info->mPlanet = planet;
		info->mFlags = flags;
		info->mTotalCount = totalCount;
	}

	return eventsCount;
}
