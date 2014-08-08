#include "SummaryDelegate.h"
#include "SummaryModel.h"
#include "DataProvider.h"
#include <QPainter>
#include <QTextDocument>
#include <QAbstractTextDocumentLayout>
#include "../view/ViewHolder.h"

SummaryDelegate::SummaryDelegate()
{
	mDataProvider = DataProviderSingleton::instance();
}

void SummaryDelegate::paint(QPainter* painter, const QStyleOptionViewItem& option, const QModelIndex& index) const
{
	QStyleOptionViewItemV4 options = option;
    initStyleOption(&options, index);

    painter->save();
	painter->translate(options.rect.x(), options.rect.y());
	
	SummaryItem* si = (SummaryItem*)(mDataProvider->mSummaryModel->data(index).value<void*>());
	ViewHolder* h = ViewHolder::holder(si, true);
	h->calculateActiveEvent(mDataProvider->getCustomTime(), mDataProvider->getCurrentTime());
	h->fillLayout();
	h->render(painter);

	painter->restore();
}

QSize SummaryDelegate::sizeHint(const QStyleOptionViewItem& option, const QModelIndex& index) const
{
	return QSize(60, 60);
}
