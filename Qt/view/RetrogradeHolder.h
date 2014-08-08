#pragma once

#include "ViewHolder.h"

namespace Ui {
class RetrogradeHolder;
}

class RetrogradeHolder : public ViewHolder
{
public:
	RetrogradeHolder();
	~RetrogradeHolder();
	virtual void fillLayout();

private:
	Ui::RetrogradeHolder *ui;
};

