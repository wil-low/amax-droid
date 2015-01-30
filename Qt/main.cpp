#include "MainWindow.h"
#include "util/AmaxDatabase.h"
#include "util/AmaxSettings.h"
#include "util/DataProvider.h"
#include <QApplication>
#include <QDebug>
#include <QFontDatabase>
#include <QDesktopWidget>
#include <QTimeZone>

int main(int argc, char *argv[])
{
	QApplication a(argc, argv);
	
//	QList<QByteArray> ids = QTimeZone::availableTimeZoneIds();
//	foreach (const QByteArray& id, ids)
//		qDebug() << id;
//	return 0;
		
	int font_id = QFontDatabase::addApplicationFont (":/font.ttf");
	qDebug() << "addFont: " << font_id;
	qDebug() << QFontDatabase::applicationFontFamilies (font_id);
	SettingsSingleton::instance();
	AmaxDatabaseSingleton::instance();
	DataProvider* dataProvider = DataProviderSingleton::instance();
	dataProvider->restoreState();
	dataProvider->saveState();
	MainWindow w;
	w.updateDisplay();
	w.move(QApplication::desktop()->screen()->rect().center() - w.rect().center());
	w.show();
	
	return a.exec();
}
