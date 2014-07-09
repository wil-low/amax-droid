#include "PreferenceWindow.h"
#include "ui_PreferenceWindow.h"

PreferenceWindow::PreferenceWindow(QWidget *parent) :
	QDialog(parent),
	ui(new Ui::PreferenceWindow)
{
	ui->setupUi(this);
}

PreferenceWindow::~PreferenceWindow()
{
	delete ui;
}
