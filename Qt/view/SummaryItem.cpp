#include "SummaryItem.h"
#include "../util/Event.h"

SummaryItem::SummaryItem(int key, const QList<Event*>& events)
: mKey(key)
, mEventMode(EVENT_MODE_NONE)
, mEvents(events)
{
}

SummaryItem::SummaryItem(int key, Event* e)
: mKey(key)
, mEventMode(EVENT_MODE_NONE)
{
	mEvents.append(e);
}

int SummaryItem::getActiveEventPosition(long customTime, long currentTime)
{
	int index = 0;
	switch (mKey) {
	case Event::EV_MOON_MOVE:
		for (int i = 0; i < mEvents.size(); ++i) {
			Event* e = mEvents[i];
			if (e->mEvtype == Event::EV_MOON_MOVE) {
				if (Event::dateBetween(currentTime, e->mDate[0], e->mDate[1]) == 0) {
					mEventMode = EVENT_MODE_CURRENT_TIME;
					return index;
				} 
				else if (Event::dateBetween(customTime, e->mDate[0], e->mDate[1]) == 0) {
					mEventMode = currentTime == 0 ? EVENT_MODE_CUSTOM_TIME : EVENT_MODE_NONE;
					return index;
				}
			}
			++index;
		}
		break;
	case Event::EV_VOC:
	case Event::EV_VIA_COMBUSTA: {
		int prev = -1;
		for (int i = 0; i < mEvents.size(); ++i) {
			Event* e = mEvents[i];
			int between = Event::dateBetween(currentTime, e->mDate[0], e->mDate[1]);
			if (between == 0) {
				mEventMode = EVENT_MODE_CURRENT_TIME;
				return index;
			} 
			else if (Event::dateBetween(customTime, e->mDate[0], e->mDate[1]) == 0) {
				mEventMode = currentTime == 0 ? EVENT_MODE_CUSTOM_TIME : EVENT_MODE_NONE;
				return index;
			} 
			else if (between == 1) {
				mEventMode = EVENT_MODE_NONE;
				return index;
			}
			prev = index;
			++index;
		}
		return prev; }
	default:
		for (int i = 0; i < mEvents.size(); ++i) {
			Event* e = mEvents[i];
			if (Event::dateBetween(currentTime, e->mDate[0], e->mDate[1]) == 0) {
				mEventMode = EVENT_MODE_CURRENT_TIME;
				return index;
			} 
			else if (Event::dateBetween(customTime, e->mDate[0], e->mDate[1]) == 0) {
				mEventMode = currentTime == 0 ? EVENT_MODE_CUSTOM_TIME : EVENT_MODE_NONE;
				return index;
			}
			++index;
		}
		break;
	}
	return -1;
}

QDebug operator<<(QDebug dbg, const SummaryItem& si)
{
	dbg << "SummaryItem " << si.mKey << ":\n";
	foreach (const Event* event, si.mEvents) {
		dbg << "  " << *event << "\n";
	}
	return dbg;
}
