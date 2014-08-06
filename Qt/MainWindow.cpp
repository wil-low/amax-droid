#include "MainWindow.h"
#include "ui_MainWindow.h"
#include "util/DataProvider.h"
#include "util/SummaryModel.h"

#include "util/AmaxSettings.h"

MainWindow::MainWindow(QWidget *parent)
: BaseEventListWindow(parent)
, ui(new Ui::MainWindow)
{
	ui->setupUi(this);
	//ui->lblDate->setFont(QFont("Astronom"));
	AmaxSettings* settings = SettingsSingleton::instance();
//	InterpretationProvider.getInstance(this);
	ui->lstEvents->setModel(mDataProvider->mSummaryModel);
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

//	SummaryAdapter adapter = new SummaryAdapter(this,
//			mDataProvider->mEventCache, mDataProvider->getCustomTime(),
//			mDataProvider->getCurrentTime());
//	mEventList.setAdapter(adapter);
}

void MainWindow::on_tbnNext_clicked()
{	
	nextDate();
}
