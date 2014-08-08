#include "MoonTransitionHolder.h"
#include "ui_MoonTransitionHolder.h"
#include "../util/Event.h"

MoonTransitionHolder::MoonTransitionHolder()
: ui(new Ui::MoonTransitionHolder)
{
	ui->setupUi(this);
	mSunSign = astroSymbol(TYPE_PLANET, Event::SE_SUN);
}

MoonTransitionHolder::~MoonTransitionHolder()
{
	delete ui;
}

void MoonTransitionHolder::fillLayout()
{
	Event* e = activeEvent();
	if (e) {
		ui->mText0->setStyleSheet("QLabel {color: rgb(" + mDefaultTextColor + ");}");
		switch (e->mEvtype) {
		case Event::EV_MOON_MOVE:
			ui->mText0->setText(e->normalizedRangeString());
			setColorByEventMode(ui->mText0, e);
			ui->mPlanet0->setText(mSunSign);
			ui->mPlanet0->setVisible(false);
			ui->mPlanet1->setText(mSunSign);
			ui->mPlanet1->setVisible(false);
			ui->mTransitionSignView->setVisible(true);
			break;
		case Event::EV_ASP_EXACT_MOON:
			ui->mPlanet0->setText(
					astroSymbol(TYPE_PLANET, e->mPlanet0)
					+ " "
					+ astroSymbol(TYPE_ASPECT, e->getDegree())
					+ " "
					+ astroSymbol(TYPE_PLANET, e->mPlanet1));
			ui->mPlanet1->setVisible(false);
/*			ui->mText0->setText(e->long2String(e->mDate[0], DataProvider
					->getInstance()->isInCurrentDay(e->mDate[0]) ? null
					: Event::mMonthAbbrDayDateFormat, true));*/
			ui->mPlanet0->setVisible(true);
			ui->mTransitionSignView->setVisible(false);
			break;
		case Event::EV_SIGN_ENTER:
			ui->mPlanet0->setText(mSunSign);
			ui->mPlanet0->setVisible(false);
			ui->mPlanet1->setText(astroSymbol(TYPE_ZODIAC,
					e->getDegree()));
			ui->mPlanet1->setVisible(true);
/*			ui->mText0->setText(e->long2String(e->mDate[0], DataProvider
					->getInstance()->isInCurrentDay(e->mDate[0]) ? null
					: Event::mMonthAbbrDayDateFormat, true));*/
			ui->mTransitionSignView->setVisible(false);
			break;
		}
	}
	else {
		ui->mPlanet0->setText("");
		ui->mPlanet1->setText("");
		ui->mText0->setText("");
		ui->mPlanet1->setVisible(false);
		ui->mTransitionSignView->setVisible(false);
	}
}
