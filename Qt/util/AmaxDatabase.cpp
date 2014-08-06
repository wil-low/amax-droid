#include "AmaxDatabase.h"
#include "CommonDataFile.h"
#include "LocationsDataFile.h"
#include "AmaxSettings.h"

#include <QVariant>
#include <QDebug>
#include <QFile>
#include <QtSql/QSqlQuery>
#include <QtSql/QSqlError>
#include <stdio.h>

AmaxDatabase::AmaxDatabase()
{
	mDB = QSqlDatabase::addDatabase("QSQLITE");
	QString dbName = SettingsSingleton::instance()->dir() + "/amax.db";
	QFile in(":/amax.db");
	in.copy (dbName);
    mDB.setDatabaseName(dbName);
    bool ok = mDB.open();
	mQuery = new QSqlQuery(mDB);
	qDebug() << "mDB open: " << ok;
}

AmaxDatabase::~AmaxDatabase()
{
	delete mQuery;
}

QSqlQuery* AmaxDatabase::getPeriodAndCity(int periodId, const QString& cityKey)
{
	mQuery->exec(QString(
		"select commons._id as _id, year, start_month, month_count, commons.key, name"
		" from commons, cities where commons._id = %1 and cities.key = '%2'")
		.arg(periodId).arg(cityKey));
	return mQuery;
}

QSqlQuery* AmaxDatabase::getAvailablePeriods(int exceptId)
{
	mQuery->exec(QString(
		"select _id, year, start_month, month_count, key"
		" from commons where _id <> %1"
		" order by year, start_month, month_count")
		.arg(exceptId));
	return mQuery;
}

QSqlQuery* AmaxDatabase::getCity(const QString& cityKey)
{
	mQuery->exec(QString(
		"select _id, name, state, country"
		" from cities where key = '%1'")
		.arg(cityKey));
	return mQuery;
}

QSqlQuery* AmaxDatabase::getSortedCities()
{
	mQuery->exec(
		"select _id, name, state, country, key"
		" from cities order by name, state, country");
	return mQuery;

}

QSqlQuery* AmaxDatabase::getLocationsForPeriod(int periodId)
{
	mQuery->exec(QString(
		"select lo._id as _id, ci.name, ci.state, ci.country, ci.key"
		" from cities ci, locations lo, commons co"
		" where ci._id = lo.city_id and co._id = %1"
		" and lo.common_id = co._id"
		" order by ci.name, ci.state, ci.country")
		.arg(periodId));
	return mQuery;
}

int AmaxDatabase::addCity(const LocationsDataFile* ldf, const QString& cityKey)
{
	mQuery->prepare("insert into cities (name, state, country, key) values (?, ?, ?, ?)");
	mQuery->bindValue(0, ldf->mCity);
	mQuery->bindValue(1, ldf->mState);
	mQuery->bindValue(2, ldf->mCountry);
	mQuery->bindValue(3, cityKey);

	if (mQuery->exec()) {
		return mQuery->lastInsertId().toInt();
	}
	else {
		if (getCity(cityKey)->next())
			return mQuery->value(0).toInt();
	}
	return -1;	
}

void AmaxDatabase::addLocation(int periodId, int cityId)
{
	mQuery->prepare("insert into locations (common_id, city_id) values (?, ?)");
	mQuery->bindValue(0, periodId);
	mQuery->bindValue(1, cityId);
	mQuery->exec();
}

int AmaxDatabase::addPeriod(const CommonDataFile* cdf, const QString& periodKey)
{
	mQuery->prepare("insert into commons (year, start_month, month_count, key) values (?, ?, ?, ?)");
	mQuery->bindValue(0, cdf->mStartYear);
	mQuery->bindValue(1, cdf->mStartMonth);
	mQuery->bindValue(2, cdf->mMonthCount);
	mQuery->bindValue(3, periodKey);
	mQuery->exec();
	return getPeriodIdByKey(periodKey);
}

int AmaxDatabase::getPeriodIdByKey(const QString& periodKey)
{
	mQuery->exec(QString(
		"select _id from commons where key = '%1'")
		.arg(periodKey));
	if (mQuery->next())
		return mQuery->value(0).toInt();
	return -1;
	
}

void AmaxDatabase::getPeriodStringAndKey(int periodId, QString& period, QString& key)
{
	mQuery->exec(QString(
		"select year, start_month, month_count, key"
		" from commons where _id = %1")
		.arg(periodId));
	if (mQuery->next()) {
		char buf[20];
		sprintf (buf, "%04d%02d%02d", mQuery->value(0).toInt(), mQuery->value(1).toInt(), mQuery->value(2).toInt());
		period = buf;
		key = mQuery->value(3).toString();
	}
	mQuery->finish();
}

QSqlQuery* AmaxDatabase::getPeriodByDate(int year, int month)
{
	mQuery->exec (QString(
		"select _id from commons where year = %1 and %2 between start_month and start_month + month_count - 1")
		.arg(year).arg(month));
	return mQuery;	
}

void AmaxDatabase::deleteLocation(int periodId, int cityId)
{
	mDB.exec("delete from locations where common_id = ? and city_id = ?");
	mQuery->bindValue(0, periodId);
	mQuery->bindValue(1, cityId);
	mQuery->exec();
}
