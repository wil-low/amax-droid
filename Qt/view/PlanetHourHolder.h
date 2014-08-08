#pragma once

#include "ViewHolder.h"

namespace Ui {
class PlanetHourHolder;
}

class PlanetHourHolder : public ViewHolder
{
public:
	PlanetHourHolder();
	~PlanetHourHolder();
	virtual void fillLayout();

private:
	Ui::PlanetHourHolder *ui;
};

