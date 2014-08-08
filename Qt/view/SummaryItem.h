#pragma once

#include <QList>
#include <QDebug>
#include <QMetaType>

class Event;

class SummaryItem
{
public:
	int mKey;
	int mEventMode;
	QList<Event> mEvents;
	
	// how is active event selected
	enum {
		EVENT_MODE_NONE = 0,
		EVENT_MODE_CURRENT_TIME = 1,
		EVENT_MODE_CUSTOM_TIME = 2,
	};

	SummaryItem() {}
	SummaryItem(int key, const QList<Event>& events);
	SummaryItem(int key, const Event& e);
	int activeEventPosition(long customTime, long currentTime);
};

QDebug operator<<(QDebug dbg, const SummaryItem& si);

Q_DECLARE_METATYPE(SummaryItem)
