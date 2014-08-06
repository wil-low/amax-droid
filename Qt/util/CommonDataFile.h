#pragma once

#include "DataReader.h"
#include <QByteArray>

class CommonDataFile : public DataReader
{
public:
	int mStartYear;
	int mStartMonth;
	int mStartDay;
	int mDayCount, mMonthCount;
	QByteArray mData;

	CommonDataFile(const QString& filename, bool isLegacy);

private:
	QByteArray mCustomData;	
};

