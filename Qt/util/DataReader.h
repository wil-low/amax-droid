#pragma once

#include <QString>

class QIODevice;

const int MAX_READ_SIZE = 100000;

class DataReader
{
public:
	DataReader();

	static short swapShort(short var);
	static int swapInt(int var);
	static bool skip(QIODevice& fn, int offset);
	static int readInt(QIODevice& fn);
	static int readShort(QIODevice& fn);
	static int readUnsignedByte(QIODevice& fn);
	static int readByte(QIODevice& fn);
	static QString readUTF(QIODevice& fn);
};
