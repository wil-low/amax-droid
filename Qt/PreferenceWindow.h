#ifndef PREFERENCEWINDOW_H
#define PREFERENCEWINDOW_H

#include <QDialog>

namespace Ui {
class PreferenceWindow;
}

class PreferenceWindow : public QDialog
{
	Q_OBJECT
	
public:
	explicit PreferenceWindow(QWidget *parent = 0);
	~PreferenceWindow();
	
private:
	Ui::PreferenceWindow *ui;
};

#endif // PREFERENCEWINDOW_H
