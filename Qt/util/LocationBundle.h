#pragma once

#include "DataReader.h"
#include <QList>
#include <QByteArray>

class LocationBundle : public DataReader
{
public:
	LocationBundle(const QString& filename);
	QByteArray extractLocation(int index);

	int mRecordCount;
private:
	QList<int> mRecordLengths;
	QByteArray mLocStream;
};
