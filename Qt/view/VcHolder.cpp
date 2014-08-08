#include "VcHolder.h"
#include "ui_VcHolder.h"
#include "../util/Event.h"

VcHolder::VcHolder()
: ui(new Ui::VcHolder)
{
	ui->setupUi(this);
}

VcHolder::~VcHolder()
{
	delete ui;
}

void VcHolder::fillLayout()
{
	Event* e = activeEvent();
	if (e) {
		ui->mText0->setText(e->normalizedRangeString());
		setColorByEventMode(ui->mText0, e);
	}
	else {
		ui->mText0->setText("");
	}
}
