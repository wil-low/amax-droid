#include "StartPageItem.h"
#include <QDataStream>

StartPageItem::StartPageItem()
: mIndex(-1)
, mIsEnabled(false)
{
}

StartPageItem::StartPageItem(const QString& caption, int index, bool isEnabled)
: mCaption(caption)
, mIndex(index)
, mIsEnabled(isEnabled)
{
}

QDataStream& operator<<(QDataStream& out, const StartPageItem& myObj)
{
	out << myObj.mCaption << myObj.mIndex << myObj.mIsEnabled;
	return out;
}

QDataStream& operator>>(QDataStream& in, StartPageItem& myObj)
{
	in >> myObj.mCaption >> myObj.mIndex >> myObj.mIsEnabled;
	return in;
}
