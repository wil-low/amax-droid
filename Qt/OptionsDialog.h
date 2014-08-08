#pragma once

#include <QDialog>

namespace Ui {
class OptionsDialog;
}

class OptionsDialog : public QDialog
{
	Q_OBJECT
	
public:
	explicit OptionsDialog(QWidget *parent = 0);
	~OptionsDialog();
	
private:
	Ui::OptionsDialog *ui;
};
