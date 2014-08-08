#pragma once

#include "ViewHolder.h"

namespace Ui {
class AspectScrollHolder;
}

class AspectScrollHolder : public ViewHolder
{
public:
	AspectScrollHolder();
	~AspectScrollHolder();
	virtual void fillLayout();

private:
	Ui::AspectScrollHolder *ui;
};

