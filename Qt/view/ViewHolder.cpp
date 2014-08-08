#include "ViewHolder.h"
#include "SummaryItem.h"
#include "EmptyHolder.h"
#include "VocHolder.h"
#include "VcHolder.h"
#include "SunDegreeHolder.h"
#include "MoonSignHolder.h"
#include "AspectScrollHolder.h"
#include "AspectHolder.h"
#include "RetrogradeScrollHolder.h"
#include "RetrogradeHolder.h"
#include "PlanetHourHolder.h"
#include "MoonTransitionHolder.h"
#include "TithiHolder.h"
#include "../util/Event.h"
#include <QLabel>

const int EMPTY_KEY = 1000;

QString ViewHolder::mDefaultTextColor("200,200,200");
QString ViewHolder::mBlueMarkColor("0,0,255");
QString ViewHolder::mRedMarkColor("255,0,0");
QMap<int, ViewHolder*> ViewHolder::mHolders;

void ViewHolder::initialize()
{
	mHolders.insert(EMPTY_KEY, new EmptyHolder);
	mHolders.insert(Event::EV_VOC, new VocHolder);
	mHolders.insert(Event::EV_VIA_COMBUSTA, new VcHolder);
	mHolders.insert(Event::EV_SUN_DEGREE, new SunDegreeHolder);
	mHolders.insert(Event::EV_MOON_SIGN, new MoonSignHolder);
	mHolders.insert(Event::EV_ASP_EXACT + EMPTY_KEY, new AspectScrollHolder);
	mHolders.insert(Event::EV_ASP_EXACT, new AspectHolder);
	mHolders.insert(Event::EV_RETROGRADE + EMPTY_KEY, new RetrogradeScrollHolder);
	mHolders.insert(Event::EV_RETROGRADE, new RetrogradeHolder);
	mHolders.insert(Event::EV_PLANET_HOUR, new PlanetHourHolder);
	mHolders.insert(Event::EV_MOON_MOVE, new MoonTransitionHolder);
	mHolders.insert(Event::EV_TITHI, new TithiHolder);
}

QString ViewHolder::astroSymbol(ViewHolder::AstroType type, int id)
{
	char result = '?';
	switch (type) {
	case TYPE_PLANET:
		result = 0x50 + id;
		break;
	case TYPE_ASPECT:
		switch (id) {
		case 0: result = 0x60; break;
		case 180: result = 0x64; break;
		case 120: result = 0x63; break;
		case 90: result = 0x62; break;
		case 60: result = 0x61; break;
		case 45: result = 0x65; break;
		}
		break;
	case TYPE_ZODIAC:
		result = 0x40 + id;
		break;
	case TYPE_RETROGRADE:
		result = 0x24;
		break;
	}
	return QString(result);
}

ViewHolder* ViewHolder::holder(SummaryItem* si, bool isSummaryMode)
{
	ViewHolder* h = NULL;
	if (si->mEvents.empty())
		h = mHolders[EMPTY_KEY];
	else if (isSummaryMode && (si->mKey == Event::EV_ASP_EXACT || si->mKey == Event::EV_RETROGRADE))
		h = mHolders[si->mKey + EMPTY_KEY];
	else
		h = mHolders[si->mKey];

	h->mIsSummaryMode = isSummaryMode;
	h->mSummaryItem = si;
	return h;
}

void ViewHolder::calculateActiveEvent(long customTime, long currentTime)
{
	int pos = mSummaryItem->activeEventPosition(customTime, currentTime);
	mActiveEvent = (pos == -1) ? NULL : &mSummaryItem->mEvents[pos];
}

Event* ViewHolder::activeEvent() const
{
	return mIsSummaryMode ? mActiveEvent : &mSummaryItem->mEvents[0];
}

void ViewHolder::setColorByEventMode(QLabel* label, Event* e)
{
	QString& color = mDefaultTextColor;
	if (e == mActiveEvent) {
		switch (mSummaryItem->mEventMode) {
		case SummaryItem::EVENT_MODE_CURRENT_TIME:
			color = mRedMarkColor;
			break;
		case SummaryItem::EVENT_MODE_CUSTOM_TIME:
			color = mBlueMarkColor;
			break;
		}
	}
	label->setStyleSheet("QLabel {color: rgb(" + color + ");}");
}

/*
	public boolean onLongClick(View v) {
		Object obj = v.getTag();
		if (obj != null) {
			ViewHolder holder = (ViewHolder) obj;
			Event e = holder.getActiveEvent();
			String text = InterpretationProvider.getInstance().getText(e);
			if (text != null) {
				Intent intent = new Intent(mContext,
						InterpreterActivity.class);
				intent.putExtra(PreferenceUtils.LISTKEY_INTERPRETER_TEXT,
						text);
				intent.putExtra(PreferenceUtils.LISTKEY_INTERPRETER_EVENT,
						e);
				mContext.startActivity(intent);
			}
		}
		return true;
	}

	public void onClick(View v) {
		Object obj = v.getTag();
		if (obj != null) {
			ViewHolder holder = (ViewHolder) obj;
			if (!holder.mIsSummaryMode) {
				onLongClick(v);
			}
			else if (holder.mSummaryItem != null) {
				Intent intent = new Intent(mContext, EventListActivity.class);
				intent.putExtra(PreferenceUtils.LISTKEY_EVENT_KEY, holder.mSummaryItem.mKey);
				mContext.startActivity(intent);
			}
		}
	}
*/
