#include "SummaryModel.h"
#include "DataProvider.h"
#include "../view/SummaryItem.h"
#include <QDebug>
#include <QSize>
#include <QVariant>

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
	if (role == Qt::SizeHintRole)
		return QSize(100, 20);
	return QVariant::fromValue((void*)mProvider->mEventCache[index.row()]);
}
