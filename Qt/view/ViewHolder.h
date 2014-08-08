#pragma once

#include <QWidget>
#include <QMap>

class SummaryItem;
class QLabel;
class Event;

class ViewHolder : public QWidget
{
public:
	static void initialize();
	static ViewHolder* holder(SummaryItem* si, bool isSummaryMode);

	virtual void fillLayout() = 0;
	void calculateActiveEvent(long customTime, long currentTime);
	Event* activeEvent() const;
	bool mIsSummaryMode;

protected:
	static QString mDefaultTextColor;
	static QString mBlueMarkColor;
	static QString mRedMarkColor;

	Event* mActiveEvent;
	SummaryItem* mSummaryItem;

	void setColorByEventMode(QLabel* label, Event* e);
	
	enum AstroType {
		TYPE_PLANET = 0,
		TYPE_ASPECT = 1,
		TYPE_ZODIAC = 2,
		TYPE_RETROGRADE = 3,
	};

	static QString astroSymbol(AstroType type, int id);

private:
	static QMap<int, ViewHolder*> mHolders;
};
