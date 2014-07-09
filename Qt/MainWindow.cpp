#include "MainWindow.h"
#include "ui_MainWindow.h"
#include "PreferenceWindow.h"

MainWindow::MainWindow(QWidget *parent) :
	QMainWindow(parent),
	ui(new Ui::MainWindow)
{
	ui->setupUi(this);
	ui->lblDate->setFont(QFont("Astronom"));
}

MainWindow::~MainWindow()
{
	delete ui;
}

void MainWindow::on_tbnBack_clicked()
{
	PreferenceWindow w(this);
	w.exec();
}
