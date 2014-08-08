#include "RetrogradeScrollHolder.h"
#include "ui_RetrogradeScrollHolder.h"

RetrogradeScrollHolder::RetrogradeScrollHolder()
: ui(new Ui::RetrogradeScrollHolder)
{
	ui->setupUi(this);
}

RetrogradeScrollHolder::~RetrogradeScrollHolder()
{
	delete ui;
}

void RetrogradeScrollHolder::fillLayout()
{
    	
}
