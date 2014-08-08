#pragma once

#include "ViewHolder.h"

namespace Ui {
class VcHolder;
}

class VcHolder : public ViewHolder
{
public:
	VcHolder();
	~VcHolder();
	virtual void fillLayout();

private:
	Ui::VcHolder *ui;
};

