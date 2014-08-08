#include "VocHolder.h"
#include "ui_VocHolder.h"
#include "../util/Event.h"

VocHolder::VocHolder()
: ui(new Ui::VocHolder)
{
	ui->setupUi(this);
}

VocHolder::~VocHolder()
{
	delete ui;
}

void VocHolder::fillLayout()
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
