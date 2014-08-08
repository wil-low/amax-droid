#pragma once

#include "ViewHolder.h"

namespace Ui {
class MoonSignHolder;
}

class MoonSignHolder : public ViewHolder
{
public:
	MoonSignHolder();
	~MoonSignHolder();
	virtual void fillLayout();

private:
	Ui::MoonSignHolder *ui;
};

