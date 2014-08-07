#include "SummaryModel.h"
#include "DataProvider.h"
#include "../view/SummaryItem.h"
#include <QDebug>

SummaryModel::SummaryModel(DataProvider* provider, QObject* parent)
: QAbstractListModel(parent) 
, mProvider(provider)
{
	
}

void SummaryModel::beginChange()
{
	beginResetModel();
}

void SummaryModel::endChange()
{
	endResetModel();
}

int SummaryModel::rowCount (const QModelIndex& parent) const
{
	return mProvider->mEventCache.size();
}

QVariant SummaryModel::data (const QModelIndex& index, int role) const
{
	if (role != Qt::DisplayRole)
		return QVariant();
	QString s("No events");
	int row = index.row();
	SummaryItem* si = mProvider->mEventCache[row];
	if (!si->mEvents.empty()) {
		s = si->mEvents.first().toString();
	}
	return s;
}
