#include "MainWindow.h"
#include <QApplication>
#include <QDebug>
#include <QFontDatabase>

int main(int argc, char *argv[])
{
	QApplication a(argc, argv);
	int font_id = QFontDatabase::addApplicationFont (":/font.ttf");
	qDebug() << "addFont: " << font_id;
	qDebug() << QFontDatabase::applicationFontFamilies (font_id);
	MainWindow w;
	w.show();
	
	return a.exec();
}
