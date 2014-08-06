#include "LocationBundle.h"
#include <QFile>
#include <QBuffer>

LocationBundle::LocationBundle(const QString& filename)
: DataReader()
{
	QFile in(filename);
	in.open(QIODevice::ReadOnly);
	readShort(in); // skip year
	mRecordCount = readShort(in);
	for (int i = 0; i < mRecordCount; ++i)
		mRecordLengths.append(readShort(in));
	mLocStream = in.readAll();
	in.close();
}

QByteArray LocationBundle::extractLocation(int index)
{
	int off = 0;
	for (int i = 0; i < index; i++) {
		off += mRecordLengths[i];
	}
	int len = mRecordLengths[index];
	QBuffer stream(&mLocStream);
	stream.open(QIODevice::ReadOnly);
	skip(stream, off);
	QByteArray result = stream.peek(len);
	stream.close();
	return result;
}
