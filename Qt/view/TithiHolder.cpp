#include "TithiHolder.h"
#include "ui_TithiHolder.h"
#include "../util/Event.h"

TithiHolder::TithiHolder()
: ui(new Ui::TithiHolder)
{
	ui->setupUi(this);
}

TithiHolder::~TithiHolder()
{
	delete ui;
}

void TithiHolder::fillLayout()
{
	Event* e = activeEvent();
	if (e) {
		ui->mDegree->setText(QString::number(e->getDegree()));
		ui->mText0->setText(e->normalizedRangeString());
		setColorByEventMode(ui->mText0, e);
	} 
	else {
		ui->mDegree->setText("");
		ui->mText0->setText("");
	}
}
