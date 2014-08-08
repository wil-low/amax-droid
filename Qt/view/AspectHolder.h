#pragma once

#include "ViewHolder.h"

namespace Ui {
class AspectHolder;
}

class AspectHolder : public ViewHolder
{
public:
	AspectHolder();
	~AspectHolder();
	virtual void fillLayout();

private:
	Ui::AspectHolder *ui;
};

