#pragma once

#include "DataReader.h"
#include "Event.h"
#include "Singleton.h"
#include "StartPageItem.h"
#include <QCoreApplication>
#include <QString>
#include <QList>
#include <QDateTime>

class SummaryItem;
class Event;
class CommonDataFile;
class LocationsDataFile;
class AmaxDatabase;
class AmaxSettings;
class SubDataInfo;
class SummaryModel;

// Keep in sync with string-array name="startpage_items"
const int START_PAGE_ITEM_SEQ[] = {
	Event::EV_VOC,
	Event::EV_MOON_MOVE,
	Event::EV_PLANET_HOUR,
	Event::EV_MOON_SIGN,
	Event::EV_RETROGRADE,
	Event::EV_ASP_EXACT,
	Event::EV_VIA_COMBUSTA,
	Event::EV_SUN_DEGREE,
	Event::EV_TITHI,
};

class DataProvider : public DataReader
{
	Q_DECLARE_TR_FUNCTIONS(DataProvider)

public:
	enum {
		EF_DATE = 0x1, // contains 2nd date - 4b
		EF_PLANET1 = 0x2, // contains 1nd planet - 1b
		EF_PLANET2 = 0x4, // contains 2nd planet - 1b
		EF_DEGREE = 0x8, // contains degree or angle - 2b
		EF_CUMUL_DATE_B = 0x10, // date are cumulative from 1st 4b - 1b
		EF_CUMUL_DATE_W = 0x20, // date are cumulative from 1st 4b - 2b
		EF_SHORT_DEGREE = 0x40, // contains angle 0..180 - 1b
		EF_NEXT_DATE2 = 0x80, // 2nd date is 1st in next event
	};

	DataProvider();
	~DataProvider();
	long getCommonId();
	long getStartJD();
	long getFinalJD();
	int getYear();
	int getMonth();
	int getDay();

	void saveState();
	void restoreState();

	int read(QByteArray* data, int evtype, int planet, bool isCommon,
		long dayStart, long dayEnd, long mFinalJD, SubDataInfo* info);

	QString makeLocationFilename(const QString& cityKey);

	void changeDate(int deltaDays);
	bool hasPeriod();
	bool hasLocation();
	void setDate(int year, int month, int day);
	void setDateFromCalendar();
	void prepareCalculation();
	void calculateAll();
	SummaryItem* calculate(int key);
	void setTodayDate();
	QString getCityName();
	QString getCurrentDateString();
	long getCustomTime();
	long getCurrentTime();
	void setCustomTime(int hour, int min);
	int getCustomHour();
	int getCustomMinute();
	QString getHighlightTimeString();
	bool isInCurrentDay(long date);
	static QString makePeriodCaption(int year, int startMonth, int monthCount);

	QList<SummaryItem*> mEventCache;
	Event mEvents[100];
	long mPeriodId;
	QString mPeriodKey;
	QString mPeriodStr;

	SummaryModel* mSummaryModel;
	
private:
	int mYear;
	int mMonth;
	int mDay;

	int mCurrentHour;
	int mCurrentMinute;

	long mStartTime;
	long mEndTime;

	CommonDataFile* mCommonDatafile;
	LocationsDataFile* mLocationDatafile;

	long mStartJD, mFinalJD;
	QDateTime mCalendar;
	static const int STREAM_BUFFER_SIZE = 30000;

	int mCustomHour;
	int mCustomMinute;
	QString mTitleDateFormat;
	bool mUseCustomTime;
	QList<StartPageItem>* mStartPageLayout;

	AmaxDatabase* mDatabase;
	AmaxSettings* mSettings;


	QList<Event*> getEventsOnPeriod(int evtype, int planet, bool special, long dayStart, long dayEnd, int value);
	int getEvents(int evtype, int planet, long dayStart, long dayEnd);
	void loadPeriod();
	void unbundlePeriodAsset();
	void fillCommonIds();
	void loadLocation(const QString& cityKey);
	void makeLocationDatafile(const QString& cityKey);
	QString unbundleLocationAsset();

	QList<Event*> calculateVOCs();
	QList<Event*> calculateVC();
	QList<Event*> calculateSunDegree();
	QList<Event*> calculateMoonSign();
	QList<Event*> calculateTithis();
	QList<Event*> calculatePlanetaryHours();
	void getPlanetaryHours(QList<Event*>& result, const Event* currentSunRise, const Event* nextSunRise);
	QList<Event*> calculateAspects();
	QList<Event*> calculateMoonMove();
	static void mergeEvents(QList<Event*>& dest, const QList<Event*>& add, bool isSort);
	QList<Event*> calculateRetrogrades();
	QList<Event*> getRiseSet(int planet, long startTime, long endTime);
	Event* getEventOnPeriod(int evType, int planet, bool special, long startTime, long endTime);
	QList<Event*> getAspectsOnPeriod(int planet, long startTime, long endTime);
};

typedef Singleton<DataProvider> DataProviderSingleton;
