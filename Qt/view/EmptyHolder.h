#pragma once

#include "ViewHolder.h"

namespace Ui {
class EmptyHolder;
}

class EmptyHolder : public ViewHolder
{
public:
	EmptyHolder();
	~EmptyHolder();
	virtual void fillLayout();

private:
	Ui::EmptyHolder *ui;
};

