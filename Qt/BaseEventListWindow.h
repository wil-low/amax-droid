#pragma once
#include <QMainWindow>

class DataProvider;

class BaseEventListWindow : public QMainWindow
{
	Q_OBJECT

public:
	BaseEventListWindow (QWidget *parent);
	void updateDisplay();

protected:
	void previousDate();
	void nextDate();
	void downloadPeriod(const QString& periodStr);
	void buyPeriod(const QString& periodStr);

	virtual void updateEventList() = 0;
	virtual void updateTitle() = 0;

	DataProvider* mDataProvider;
	
	/*
	Context mContext;
	bool mUseVolumeButtons;
	ListView mEventList;
	RelativeLayout mNoPeriodLayout;
	Button mMissingDataButton;
	TextView mMissingDataMessage;*/

private:
	static QString PREMIUM_KEY;

};
