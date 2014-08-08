#include "AspectScrollHolder.h"
#include "ui_AspectScrollHolder.h"

AspectScrollHolder::AspectScrollHolder()
: ui(new Ui::AspectScrollHolder)
{
	ui->setupUi(this);
}

AspectScrollHolder::~AspectScrollHolder()
{
	delete ui;
}

void AspectScrollHolder::fillLayout()
{
    	
}
