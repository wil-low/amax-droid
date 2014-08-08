#pragma once
#include "BaseEventListWindow.h"

namespace Ui {
class MainWindow;
}

class MainWindow : public BaseEventListWindow
{
	Q_OBJECT
	
public:
	explicit MainWindow(QWidget *parent = 0);
	~MainWindow();

protected:
	virtual void updateEventList();
	virtual void updateTitle();
	
private slots:
	void on_tbnPrev_clicked();
	
	void on_tbnNext_clicked();
	
	void on_actionToday_triggered();
	
	void on_actionOptions_triggered();
	
private:
	Ui::MainWindow *ui;
	QString mTitleDate;
};
