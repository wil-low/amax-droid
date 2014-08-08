#pragma once

#include "ViewHolder.h"

namespace Ui {
class VocHolder;
}

class VocHolder : public ViewHolder
{
public:
	VocHolder();
	~VocHolder();
	virtual void fillLayout();

private:
	Ui::VocHolder *ui;
};

