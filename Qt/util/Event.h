#pragma once

#include <QString>
#include <QHash>
#include <QDateTime>
#include <QDebug>

class QTimeZone;

const QString DEFAULT_DATE_FORMAT = "yyyy-MMM-dd";

const QString CONSTELL_STR[] = { "Ari", "Tau", "Gem", "Cnc",
	"Leo", "Vir", "Lib", "Sco", "Sgr", "Cap", "Aqu", "Psc" };

const QString PLANET_STR[] = { "??", "SO", "MO", "ME", "VE",
	"MA", "JU", "SA", "UR", "NE", "PL", "TN", "AP", "WM" };

const QString EVENT_TYPE_STR[] = {
		"EV_VOC", // 0; // void of course
		"EV_SIGN_ENTER", // 1; // enter into sign
		"EV_ASP_EXACT", // 2; // exact aspect
		"EV_RISE", // 3; // rising & setting
		"EV_DEGREE_PASS", // 4; // entering degree
		"EV_VIA_COMBUSTA", // 5; // good & bad degrees
		"EV_RETROGRADE", // 6;
		"EV_ECLIPSE", // 7;
		"EV_TITHI", // 8;
		"EV_NAKSHATRA", // 9;
		"EV_SET", // 10; // rising & setting
		"EV_DECL_EXACT", // 11; // declination
		"EV_NAVROZ", // 12; // Navroz
		"EV_TOP_DAY", // 13; // week days
		"EV_PLANET_HOUR", // 14; // planetary hours
		"EV_STATUS", // 15;
		"EV_SUN_RISE", // 16;
		"EV_MOON_RISE", // 17;
		"EV_MOON_MOVE", // 18;
		"EV_SEL_DEGREES", // 19;
		"EV_DAY_HOURS", // 20;
		"EV_NIGHT_HOURS", // 21;
		"EV_SUN_DAY", // 22;
		"EV_MOON_DAY", // 23;
		"EV_TOP_MONTH", // 24;
		"EV_MOON_PHASE", // 25;
		"EV_ZODIAC_SIGN", // 26;
		"EV_PANEL", // 27;
		"EV_TOPIC_BUTTON", // 28;
		"EV_DEG_2ND", // 29; // degrees on second page
		"EV_WEEK_GRID", // 30;
		"EV_MONTH_GRID", // 31;
		"EV_DECUMBITURE", // 32;
		"EV_DECUMB_ASPECT", // 33;
		"EV_DECUMB_BEGIN", // 34;
		"EV_SUN_DEGREE_LARGE", // 35;
		"EV_MOON_SIGN_LARGE", // 36;
		"EV_HELP", // 37;
		"EV_ASP_EXACT_MOON", // 38;
		"EV_DEGPASS0", // 39;
		"EV_DEGPASS1", // 40;
		"EV_DEGPASS2", // 41;
		"EV_DEGPASS3", // 42;
		"EV_HELP0", // 43;
		"EV_HELP1", // 44;
		"EV_ASTRORISE", // 45;
		"EV_ASTROSET", // 46;
		"EV_APHETICS", // 47;
		"EV_FAST", // 48;
		"EV_ASCAPHETICS", // 49;
		"EV_MSG", // 50;
		"EV_BACK", // 51;
		"EV_TATTVAS", // 52;
		"EV_SUN_DEGREE", // 53;
		"EV_MOON_SIGN", // 54;
		"EV_SUN_RISESET", // 55;
		"EV_MOON_RISESET", // 56;
		"EV_LAST", // 57; // last - do not use
};

class Event
{
public:
	enum Planet {
		SE_SUN = 0,
		SE_MOON = 1,
		SE_MERCURY = 2,
		SE_VENUS = 3,
		SE_MARS = 4,
		SE_JUPITER = 5,
		SE_SATURN = 6,
		SE_URANUS = 7,
		SE_NEPTUNE = 8,
		SE_PLUTO = 9,
		SE_TRUE_NODE = 10,
		SE_MEAN_APOG = 11,
		SE_WHITE_MOON = 12,
	};

	enum EventType {
		EV_VOC = 0, // void of course
		EV_SIGN_ENTER = 1, // enter into sign
		EV_ASP_EXACT = 2, // exact aspect
		EV_RISE = 3, // rising & setting
		EV_DEGREE_PASS = 4, // entering degree
		EV_VIA_COMBUSTA = 5, // good & bad degrees
		EV_RETROGRADE = 6,
		EV_ECLIPSE = 7,
		EV_TITHI = 8,
		EV_NAKSHATRA = 9,
		EV_SET = 10, // rising & setting
		EV_DECL_EXACT = 11, // declination
		//EV_NAVROZ = 12, // Navroz
		EV_TOP_DAY = 13, // week days
		EV_PLANET_HOUR = 14, // planetary hours
		EV_STATUS = 15,
		EV_SUN_RISE = 16,
		EV_MOON_RISE = 17,
		EV_MOON_MOVE = 18,
		EV_SEL_DEGREES = 19,
		EV_DAY_HOURS = 20,
		EV_NIGHT_HOURS = 21,
		//EV_SUN_DAY = 22,
		//EV_MOON_DAY = 23,
		EV_TOP_MONTH = 24,
		EV_MOON_PHASE = 25,
		EV_ZODIAC_SIGN = 26,
		EV_PANEL = 27,
		EV_TOPIC_BUTTON = 28,
		EV_DEG_2ND = 29, // degrees on second page
		EV_WEEK_GRID = 30,
		EV_MONTH_GRID = 31,
		EV_DECUMBITURE = 32,
		EV_DECUMB_ASPECT = 33,
		EV_DECUMB_BEGIN = 34,
		EV_SUN_DEGREE_LARGE = 35,
		EV_MOON_SIGN_LARGE = 36,
		EV_HELP = 37,
		EV_ASP_EXACT_MOON = 38,
		EV_HELP0 = 43,
		EV_HELP1 = 44,
		EV_ASTRORISE = 45,
		EV_ASTROSET = 46,
		EV_APHETICS = 47,
		EV_FAST = 48,
		EV_ASCAPHETICS = 49,
		EV_MSG = 50,
		EV_BACK = 51,
		EV_TATTVAS = 52,
		EV_SUN_DEGREE = 53,
		EV_MOON_SIGN = 54,
		EV_SUN_RISESET = 55,
		EV_MOON_RISESET = 56,
		EV_LAST = 57, // last - do not use
	};

	// Any changes above must be synched with %eventType in tools.pm
	// and EventType in mutter2/events.h !!!

	static const long ROUNDING_MSEC = 60;

	int mEvtype;
	char mPlanet0;
	char mPlanet1;
	long mDate[2];

	Event();
	Event(long date, int planet);
	Event(int evType, long date0, long date1, int planet0, int planet1, int degree);
	int getDegree() const;
	int getDegType() const;
	short getFullDegree() const;
	void setFullDegree(short degree);
	QString getEvTypeStr() const;
	static int dateBetween(long date0, long start, long end);
	bool isDateBetween(int index, long start, long end) const;
	bool isInPeriod(long start, long end, bool special) const;
	QString toString() const;
	static QString getPlanetName(char planet);
	QString long2String(long date0, const QString& dateFormat, bool h24) const;
	static void setTimeZone(const QTimeZone& timezone);
	static QString formatDate(const QString& dateFormat, long date);

//	static class EventDate0Comparator implements Comparator<Event> {
//		int compare(Event o1, Event o2) {
//			return (int) (o1.mDate[0] - o2.mDate[0]);
//		}
//	}

	static void setTimeRange(long date0, long date1);
	QString normalizedRangeString() const;

	static void initialize();
	static QHash<int, int> ASPECT_GOODNESS;
	static QHash<int, int> ASPECT_MAP;
	static QString mMonthAbbrDayDateFormat;

private:
	static QDateTime mCalendar;
	// TODO Move USE_EXACT_RANGE to settings
	static const bool USE_EXACT_RANGE = false;
	static long mPeriod0;
	static long mPeriod1;
	short mDegree;
};

QDebug operator<<(QDebug dbg, const Event& event);
