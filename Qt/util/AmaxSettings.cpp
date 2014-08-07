#include "AmaxSettings.h"
#include "Event.h"
#include <QDateTime>
#include <QFileInfo>
#include <QTimeZone>
#include <QDebug>
#include <QStandardPaths>
#include <QDir>

const char KEY_PERIOD_ID[] = "period_id";
const char KEY_CITY_KEY[] = "city_key";
const char KEY_START_TIME[] = "start_time";
const char KEY_CUSTOM_HOUR[] = "custom_hour";
const char KEY_CUSTOM_MINUTE[] = "custom_minute";
const char LISTKEY_EVENT_KEY[] = "eventKey";
const char LISTKEY_INTERPRETER_TEXT[] = "interpreterCode";
const char LISTKEY_INTERPRETER_EVENT[] = "event";
const char PERIOD_STRING_KEY[] = "periodString";
const char MODE_KEY[] = "mode";

const char COUNTRY_ID_KEY[] = "countryId";
const char STATE_ID_KEY[] = "stateId";
const char CITY_ID_KEY[] = "cityId";
const char COUNTRY_NAME_KEY[] = "countryName";
const char STATE_NAME_KEY[] = "stateName";
const char CITY_NAME_KEY[] = "cityName";

const char KEY_USE_CUSTOM_TIME[] = "use_custom_time";
const char KEY_CUSTOM_TIME[] = "custom_time";
const char KEY_START_PAGE_LAYOUT[] = "start_page_layout";
const char KEY_USE_VOLUME_BUTTONS[] = "use_volume_buttons";
const char KEY_DOWNLOAD_MORE[] = "download_more";
const char STARTPAGE_ITEM_INDEX[] = "startpage_item_index";
const char KEY_STARTPAGE_ITEM_ENABLED[] = "startpage_item_enabled";

AmaxSettings::AmaxSettings()
: QSettings("S&W Axis", "Astromaximum")
{
	setDefaultFormat(QSettings::IniFormat);

	mWritableLocation = QStandardPaths::writableLocation (QStandardPaths::DataLocation) + "/";
	QDir dir;
	dir.mkpath(mWritableLocation);
	
	qRegisterMetaType<StartPageItemList>("StartPageItemList");
	qRegisterMetaTypeStreamOperators<StartPageItemList>("StartPageItemList");
	
	QVariant items = value(STARTPAGE_ITEM_INDEX);
	mItems = items.value<StartPageItemList>();
	if (mItems.empty()) {
		mItems.append(StartPageItem("EV_VOC", 0, true));
		mItems.append(StartPageItem("EV_MOON_MOVE", 1, true));
		mItems.append(StartPageItem("EV_PLANET_HOUR", 2, true));
		mItems.append(StartPageItem("EV_MOON_SIGN", 3, true));
		mItems.append(StartPageItem("EV_RETROGRADE", 4, true));
		mItems.append(StartPageItem("EV_ASP_EXACT", 5, true));
		mItems.append(StartPageItem("EV_VIA_COMBUSTA", 6, true));
		mItems.append(StartPageItem("EV_SUN_DEGREE", 7, true));
		mItems.append(StartPageItem("EV_TITHI", 8, true));
		QVariant var;
		var.setValue(mItems);
		setValue(STARTPAGE_ITEM_INDEX, var);
	}
}

const QString& AmaxSettings::writableLocation() const
{
	return mWritableLocation;	
}

int AmaxSettings::getPeriodId()
{
	return value(KEY_PERIOD_ID, 0).toInt();
}

void AmaxSettings::setPeriodId(int periodId)
{
	setValue(KEY_PERIOD_ID, periodId);
}

QString AmaxSettings::getCityKey()
{
	return value(KEY_CITY_KEY, "").toString();
}

void AmaxSettings::setCityKey(const QString& cityKey)
{
	setValue(KEY_CITY_KEY, cityKey);
}

bool AmaxSettings::getUseCustomTime()
{
	return value(KEY_USE_CUSTOM_TIME, false).toBool();
}

bool AmaxSettings::getUseVolumeButtons()
{
	return value(KEY_USE_VOLUME_BUTTONS, false).toBool();
}

QList<StartPageItem>* AmaxSettings::getStartPageLayout()
{
	return &mItems;
	/*
	ArrayList<StartPageItem> result = new ArrayList<StartPageItem>();
	String[] captions = context.getResources().getStringArray(
			R.array.startpage_items);
	for (int i = 0; i < captions.length; ++i)
		result.add(null);

	SharedPreferences settings = PreferenceManager
			.getDefaultSharedPreferences(context);
	for (int i = 0; i < captions.length; ++i) {
		int index = settings.getInt(KEY_STARTPAGE_ITEM_INDEX + i, i);
		boolean isEnabled = settings.getBoolean(KEY_STARTPAGE_ITEM_ENABLED
				+ i, true);
		StartPageItem item = new StartPageItem(captions[i], i, isEnabled);
		result.set(index, item);
	}
	*/
}

int AmaxSettings::getStartTime(QTimeZone& timezone)
{
	int result = value(KEY_START_TIME, 0).toInt();
	if (result <= 0) {
		QDateTime date = QDateTime::currentDateTime();
		qDebug() << date << date.toTime_t();
//		QTimeZone tz = date.timeZone();
//		date.setTimeZone(timezone);
		result = date.toTime_t();
	}
	return result;
}

int AmaxSettings::getCustomHour()
{
	return value(KEY_CUSTOM_HOUR, 0).toInt();
}

int AmaxSettings::getCustomMinute()
{
	return value(KEY_CUSTOM_MINUTE, 0).toInt();
}

void AmaxSettings::setStartTime(int startTime)
{
	setValue(KEY_START_TIME, startTime);
}

void AmaxSettings::setCustomHour(int customHour)
{
	setValue(KEY_CUSTOM_HOUR, customHour);
}

void AmaxSettings::setCustomMinute(int customMinute)
{
	setValue(KEY_CUSTOM_MINUTE, customMinute);
}
