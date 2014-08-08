#pragma once

#include "ViewHolder.h"

namespace Ui {
class MoonTransitionHolder;
}

class MoonTransitionHolder : public ViewHolder
{
public:
	MoonTransitionHolder();
	~MoonTransitionHolder();
	virtual void fillLayout();

private:
	Ui::MoonTransitionHolder *ui;
	QString mSunSign;
};

