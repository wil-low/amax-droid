#pragma once

#include <QList>
#include <QDebug>

class Event;

class SummaryItem
{
public:
	int mKey;
	int mEventMode;
	QList<Event*> mEvents;
	
	// how is active event selected
	enum {
		EVENT_MODE_NONE = 0,
		EVENT_MODE_CURRENT_TIME = 1,
		EVENT_MODE_CUSTOM_TIME = 2,
	};

	SummaryItem(int key, const QList<Event*>& events);
	SummaryItem(int key, Event* e);
	int getActiveEventPosition(long customTime, long currentTime);
};

QDebug operator<<(QDebug dbg, const SummaryItem& si);
