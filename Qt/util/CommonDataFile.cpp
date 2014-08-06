#include "CommonDataFile.h"
#include <QFile>

CommonDataFile::CommonDataFile(const QString& filename, bool isLegacy)
: DataReader()
{
	QFile stream(filename);
	stream.open(QIODevice::ReadOnly);
	mStartYear = readShort(stream);
	mStartMonth = readUnsignedByte(stream);
	mStartDay = readUnsignedByte(stream);
	
	int customDataLen = readShort(stream); // customData length
	if (isLegacy)
		mDayCount = readShort(stream);
	else
		mMonthCount = readUnsignedByte(stream);
	if (customDataLen > 0) {
		mCustomData = stream.read(customDataLen);
	}
	mData = stream.read(MAX_READ_SIZE);
	stream.close();
}
