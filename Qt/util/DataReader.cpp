#include "DataReader.h"
#include <QIODevice>

DataReader::DataReader()
{
}

short DataReader::swapShort(short var) 
{
    var = (var & 0xff) << 8 | ((var >> 8) & 0xff);
    return var;
}

int DataReader::swapInt(int var) 
{
    int res = 0, i = 0;
    for(i=0; i<4; i++){
        res <<= 8;
        res |= (var & 0xff);
        var >>= 8;
    }
    return res;
}

bool DataReader::skip(QIODevice& fn, int offset)
{
	return fn.seek (fn.pos() + offset);
}

int DataReader::readInt(QIODevice& fn)
{
    int res = 0;
    fn.read((char*)&res, sizeof(res));
    return swapInt(res);
}

int DataReader::readShort(QIODevice& fn)
{
    short res = 0;
    fn.read((char*)&res, sizeof(res));
    return swapShort(res);
}

int DataReader::readUnsignedByte(QIODevice& fn)
{
    unsigned char res = 0;
    fn.read((char*)&res, sizeof(res));
    return res;
}

int DataReader::readByte(QIODevice& fn)
{
    char res = 0;
    fn.read(&res, sizeof(res));
    return res;
}

QString DataReader::readUTF(QIODevice& fn)
{
	char dest[MAX_READ_SIZE];
    int len = readShort(fn);
    fn.read(dest, len);
	dest[len] = 0;
    return dest;
}
