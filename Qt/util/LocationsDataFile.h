#pragma once

#include "DataReader.h"
#include <QByteArray>
#include <QString>

class TimezoneTransition;

class LocationsDataFile : public DataReader
{
public:
	int mStartYear;
	int mStartMonth;
	int mStartDay;
	int mMonthCount;
	int mCityKey;
	QString mCity;
	QString mState;
	QString mCountry;
	QString mTimezone;
	QByteArray mData;

    LocationsDataFile(QByteArray& ba);

private:
	int mCoords[3];
	QString mCustomData;
	TimezoneTransition* mTransitions;
};

