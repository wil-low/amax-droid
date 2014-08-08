#pragma once

#include "ViewHolder.h"

namespace Ui {
class TithiHolder;
}

class TithiHolder : public ViewHolder
{
public:
	TithiHolder();
	~TithiHolder();
	virtual void fillLayout();

private:
	Ui::TithiHolder *ui;
};

