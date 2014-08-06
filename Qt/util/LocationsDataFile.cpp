#include "LocationsDataFile.h"
#include "TimezoneTransition.h"

#include <QBuffer>

LocationsDataFile::LocationsDataFile(QByteArray& ba)
: DataReader()
{
	QBuffer stream(&ba);
	stream.open(QIODevice::ReadOnly);
	skip(stream, 4); // signature
	char version = readByte(stream);
	mStartYear = readShort(stream);
	mStartMonth = readUnsignedByte(stream);
	mStartDay = readUnsignedByte(stream);
	if (version == 3) {
		mMonthCount = readUnsignedByte(stream);
	}
	else if (version == 2) {
		readShort(stream);
		mMonthCount = 12;
	}
	else {
		//System.out.println("Unknown version " + version);
	}        
	mCityKey = readInt(stream); // city key as number
	mCoords[0] = readShort(stream); // latitude
	mCoords[1] = readShort(stream); // longitude
	mCoords[2] = readShort(stream); // altitude
	mCity = readUTF(stream); // city
	mState = readUTF(stream); // state
	mCountry = readUTF(stream); // country
	mTimezone = readUTF(stream); // timezone
	mCustomData = readUTF(stream); // custom data
	int transitionCount = readByte(stream);
	mTransitions = new TimezoneTransition[transitionCount];
	for (int i = 0; i < transitionCount; ++i) {
		TimezoneTransition transition;
		transition.mTime = (long)readInt(stream); // start_date
		transition.mOffset = (long)readShort(stream) * 60000; // gmt_ofs_min
		transition.mName = readUTF(stream); // name
		//MyLog.d(TAG, transition.mTime + ", " + new Date(transition.mTime) + " > " + transition.mOffset + " " + transition.mName);
		mTransitions[i] = transition;
	}
	mData = stream.read(MAX_READ_SIZE);
	stream.close();
}

