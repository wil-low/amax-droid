#include "MainWindow.h"
#include "ui_MainWindow.h"
#include "OptionsDialog.h"
#include "util/DataProvider.h"
#include "util/SummaryModel.h"
#include "util/SummaryDelegate.h"
#include "util/AmaxSettings.h"
#include "view/ViewHolder.h"
#include <QMenuBar>

MainWindow::MainWindow(QWidget *parent)
: BaseEventListWindow(parent)
, ui(new Ui::MainWindow)
{
	ui->setupUi(this);
//	InterpretationProvider.getInstance(this);
	ViewHolder::initialize();
	SummaryDelegate* delegate = new SummaryDelegate();
	ui->lstEvents->setModel(mDataProvider->mSummaryModel);
	ui->lstEvents->setItemDelegate(delegate);
}

MainWindow::~MainWindow()
{
	delete ui;
}

void MainWindow::on_tbnPrev_clicked()
{
	previousDate();
}

void MainWindow::updateTitle()
{
	mTitleDate = mDataProvider->getCurrentDateString();
	ui->lblDate->setText(mTitleDate);
	ui->lblTimeLocation->setText(mDataProvider->getHighlightTimeString()
		+ ", " + mDataProvider->getCityName());
}

void MainWindow::updateEventList()
{
	mDataProvider->prepareCalculation();
	mDataProvider->calculateAll();
}

void MainWindow::on_tbnNext_clicked()
{	
	nextDate();
}

void MainWindow::on_actionToday_triggered()
{
    mDataProvider->setTodayDate();
	updateDisplay();
}

void MainWindow::on_actionOptions_triggered()
{
    OptionsDialog d;
	d.exec();
}
