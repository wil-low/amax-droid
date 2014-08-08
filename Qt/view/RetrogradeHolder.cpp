#include "RetrogradeHolder.h"
#include "ui_RetrogradeHolder.h"
#include "../util/Event.h"

RetrogradeHolder::RetrogradeHolder()
: ui(new Ui::RetrogradeHolder)
{
	ui->setupUi(this);
}

RetrogradeHolder::~RetrogradeHolder()
{
	delete ui;
}

void RetrogradeHolder::fillLayout()
{
    	
}
