#include "MoonSignHolder.h"
#include "ui_MoonSignHolder.h"
#include "../util/Event.h"

MoonSignHolder::MoonSignHolder()
: ui(new Ui::MoonSignHolder)
{
	ui->setupUi(this);
}

MoonSignHolder::~MoonSignHolder()
{
	delete ui;
}

void MoonSignHolder::fillLayout()
{
	Event* e = activeEvent();
	if (e) {
		ui->mText0->setText(e->normalizedRangeString());
		ui->mZodiac->setText(astroSymbol(TYPE_ZODIAC, e->getDegree()));
		ui->mPlanet0->setText(astroSymbol(TYPE_PLANET, e->mPlanet0));
		setColorByEventMode(ui->mText0, e);
	}
	else {
		ui->mText0->setText("");
		ui->mZodiac->setText("");
		ui->mPlanet0->setText("");
	}
}
