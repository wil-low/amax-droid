#pragma once

#include <QSettings>
#include "Singleton.h"

#include "StartPageItem.h"

class QTimeZone;

class AmaxSettings : public QSettings
{
	Q_OBJECT
public:
	explicit AmaxSettings();
	int getPeriodId();
	void setPeriodId(int periodId);
	QString getCityKey();
	void setCityKey(const QString& cityKey);
	bool getUseCustomTime();
	bool getUseVolumeButtons();
	QList<StartPageItem>* getStartPageLayout();
	int getStartTime(QTimeZone& timezone);
	int getCustomHour();
	int getCustomMinute();
	void setStartTime(int startTime);
	void setCustomHour(int customHour);
	void setCustomMinute(int customMinute);
	const QString& dir() const;
private:
	typedef QList<StartPageItem> StartPageItemList;
	StartPageItemList mItems;
	QString mDir;
signals:
	
public slots:
	
};

typedef Singleton<AmaxSettings> SettingsSingleton;
