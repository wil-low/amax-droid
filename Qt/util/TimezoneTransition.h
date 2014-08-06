#pragma once

#include <QString>

class TimezoneTransition
{
public:
	TimezoneTransition();
	long mTime;
	long mOffset;
	QString mName;
};
