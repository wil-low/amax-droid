#pragma once

#include <QAbstractTableModel>

class DataProvider;

class SummaryModel : public QAbstractListModel
{
public:
	SummaryModel(DataProvider* provider, QObject* parent = NULL);
	virtual int rowCount (const QModelIndex& parent = QModelIndex()) const;
	virtual QVariant data (const QModelIndex& index, int role = Qt::DisplayRole) const;
	
	void beginChange();
	void endChange();

private:
	DataProvider* mProvider;
};
