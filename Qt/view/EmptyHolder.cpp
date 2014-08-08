#include "EmptyHolder.h"
#include "ui_EmptyHolder.h"

EmptyHolder::EmptyHolder()
: ui(new Ui::EmptyHolder)
{
	ui->setupUi(this);
}

EmptyHolder::~EmptyHolder()
{
	delete ui;
}

void EmptyHolder::fillLayout()
{
	/*int textId = 0;
	switch (mSummaryItem.mKey) {
	case Event.EV_VOC:
		textId = R.string.no_voc;
		break;
	case Event.EV_VIA_COMBUSTA:
		textId = R.string.no_vc;
		break;
	case Event.EV_ASP_EXACT:
		textId = R.string.no_aspects;
		break;
	case Event.EV_RETROGRADE:
		textId = R.string.no_retrograde;
		break;
	case Event.EV_MOON_SIGN:
		textId = R.string.no_moon_sign;
		break;
	case Event.EV_MOON_MOVE:
		textId = R.string.no_moon_move;
		break;
	case Event.EV_PLANET_HOUR:
		textId = R.string.no_planet_hours;
		break;
	case Event.EV_TITHI:
		textId = R.string.no_tithi;
		break;
	case Event.EV_SUN_DEGREE:
		textId = R.string.no_sun_degree;
		break;
	}
	if (textId == 0)
		mText0.setText("");
	else
		mText0.setText(textId);*/	
}
