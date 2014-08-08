#pragma once

#include "ViewHolder.h"

namespace Ui {
class SunDegreeHolder;
}

class SunDegreeHolder : public ViewHolder
{
public:
	SunDegreeHolder();
	~SunDegreeHolder();
	virtual void fillLayout();

private:
	Ui::SunDegreeHolder *ui;
};

