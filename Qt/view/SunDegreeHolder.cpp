#include "SunDegreeHolder.h"
#include "ui_SunDegreeHolder.h"
#include "../util/Event.h"
#include <QString>

SunDegreeHolder::SunDegreeHolder()
: ui(new Ui::SunDegreeHolder)
{
	ui->setupUi(this);
}

SunDegreeHolder::~SunDegreeHolder()
{
	delete ui;
}

void SunDegreeHolder::fillLayout()
{
	Event* e = activeEvent();
	if (e) {
		ui->mText0->setText(e->normalizedRangeString());
		ui->mText1->setText(QString::number(e->getDegree() % 30 + 1) + "\u00b0");
		ui->mZodiac->setText(astroSymbol(TYPE_ZODIAC, e->getDegree() / 30));
		ui->mPlanet0->setText(astroSymbol(TYPE_PLANET, e->mPlanet0));
		setColorByEventMode(ui->mText0, e);
	}
	else {
		ui->mText0->setText("");
		ui->mText1->setText("");
		ui->mZodiac->setText("");
		ui->mPlanet0->setText("");
	}
}
