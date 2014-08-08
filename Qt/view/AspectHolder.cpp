#include "AspectHolder.h"
#include "ui_AspectHolder.h"
#include "../util/Event.h"

AspectHolder::AspectHolder()
: ui(new Ui::AspectHolder)
{
	ui->setupUi(this);
}

AspectHolder::~AspectHolder()
{
	delete ui;
}

void AspectHolder::fillLayout()
{
	Event* e = activeEvent();
	if (e) {
		ui->mPlanet0->setText(astroSymbol(TYPE_PLANET, e->mPlanet0));
		ui->mAspect->setText(astroSymbol(TYPE_ASPECT, e->getDegree()));
		ui->mPlanet1->setText(astroSymbol(TYPE_PLANET, e->mPlanet1));
		if (mIsSummaryMode) {
			ui->mText0->setVisible(false);
		}
		else {
			ui->mText0->setText(e->long2String(e->mDate[0], Event::mMonthAbbrDayDateFormat, false));
			setColorByEventMode(ui->mText0, e);
		}
	}
}
