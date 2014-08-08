#pragma once

#include "ViewHolder.h"

namespace Ui {
class RetrogradeScrollHolder;
}

class RetrogradeScrollHolder : public ViewHolder
{
public:
	RetrogradeScrollHolder();
	~RetrogradeScrollHolder();
	virtual void fillLayout();

private:
	Ui::RetrogradeScrollHolder *ui;
};

