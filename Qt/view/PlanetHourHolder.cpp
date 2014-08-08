#include "PlanetHourHolder.h"
#include "ui_PlanetHourHolder.h"
#include "../util/Event.h"

PlanetHourHolder::PlanetHourHolder()
: ui(new Ui::PlanetHourHolder)
{
	ui->setupUi(this);
}

PlanetHourHolder::~PlanetHourHolder()
{
	delete ui;
}

void PlanetHourHolder::fillLayout()
{
	Event* e = activeEvent();
	if (e) {
		ui->mPlanet0->setText(astroSymbol(TYPE_PLANET, e->mPlanet0));
		ui->mText0->setText(e->normalizedRangeString());
		setColorByEventMode(ui->mText0, e);
	}
	else {
		ui->mPlanet0->setText("");
		ui->mText0->setText("");
	}
}
