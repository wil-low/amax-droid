#pragma once

#include <QString>
#include <QMetaType>

class StartPageItem
{
public:
	QString mCaption;
	int mIndex;
	bool mIsEnabled;

	StartPageItem();
	StartPageItem(const QString& caption, int index, bool isEnabled);
};

QDataStream& operator<<(QDataStream& out, const StartPageItem& myObj);
QDataStream& operator>>(QDataStream& in, StartPageItem& myObj);

Q_DECLARE_METATYPE(StartPageItem)
