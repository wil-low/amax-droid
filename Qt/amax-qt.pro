#-------------------------------------------------
#
# Project created by QtCreator 2014-07-03T19:48:05
#
#-------------------------------------------------

QT       += core gui sql

greaterThan(QT_MAJOR_VERSION, 4): QT += widgets

TARGET = amax-qt
TEMPLATE = app


SOURCES += main.cpp\
    PreferenceWindow.cpp \
    MainWindow.cpp \
    BaseEventListWindow.cpp \
    util/DataProvider.cpp \
    util/Event.cpp \
    util/StartPageItem.cpp \
    view/SummaryItem.cpp \
    util/LocationsDataFile.cpp \
    util/CommonDataFile.cpp \
    util/AmaxDatabase.cpp \
    util/AmaxSettings.cpp \
    util/DataReader.cpp \
    util/TimezoneTransition.cpp \
    util/LocationBundle.cpp \
    util/SummaryModel.cpp

HEADERS  += \
    MainWindow.h \
    PreferenceWindow.h \
    BaseEventListWindow.h \
    util/DataProvider.h \
    util/Singleton.h \
    util/Event.h \
    util/StartPageItem.h \
    view/SummaryItem.h \
    util/LocationsDataFile.h \
    util/CommonDataFile.h \
    util/AmaxDatabase.h \
    util/AmaxSettings.h \
    util/DataReader.h \
    util/TimezoneTransition.h \
    util/LocationBundle.h \
    util/SummaryModel.h

FORMS    += \
    MainWindow.ui \
    PreferenceWindow.ui

TRANSLATIONS = \
	Astromaximum_ru.ts

CONFIG += mobility
MOBILITY = 

RESOURCES += \
    assets/assets.qrc

