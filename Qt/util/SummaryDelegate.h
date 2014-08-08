#pragma once

#include <QStyledItemDelegate>

class DataProvider;

class SummaryDelegate : public QStyledItemDelegate
{
public:
	SummaryDelegate();
protected:
	virtual void paint (QPainter * painter, const QStyleOptionViewItem& option, const QModelIndex& index) const;
	virtual QSize sizeHint(const QStyleOptionViewItem& option, const QModelIndex& index) const;
	
private:
	DataProvider* mDataProvider;
};
