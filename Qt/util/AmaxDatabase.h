#pragma once

#include "Singleton.h"
#include <QtSql/QSqlDatabase>

class CommonDataFile;
class LocationsDataFile;
class QSqlQuery;

class AmaxDatabase
{
public:
	AmaxDatabase();
	~AmaxDatabase();
	QSqlQuery* getPeriodAndCity(int periodId, const QString& cityKey);
	QSqlQuery* getAvailablePeriods(int exceptId);
	QSqlQuery* getCity(const QString& cityKey);
	QSqlQuery* getSortedCities();
	QSqlQuery* getLocationsForPeriod(int periodId);
	int addCity(const LocationsDataFile* ldf, const QString& cityKey);
	void addLocation(int periodId, int cityId);
	int addPeriod(const CommonDataFile* cdf, const QString& periodKey);
	int getPeriodIdByKey(const QString& periodKey);
	void getPeriodStringAndKey(int periodId, QString& period, QString& key);
	QSqlQuery* getPeriodByDate(int year, int month);
	void deleteLocation(int periodId, int cityId);

private:
	QSqlDatabase mDB;
	QSqlQuery* mQuery;
};

typedef Singleton<AmaxDatabase> AmaxDatabaseSingleton;
