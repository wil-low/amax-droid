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
    util/SummaryModel.cpp \
    view/SunDegreeHolder.cpp \
    view/ViewHolder.cpp \
    util/SummaryDelegate.cpp \
    view/AspectHolder.cpp \
    view/AspectScrollHolder.cpp \
    view/EmptyHolder.cpp \
    view/MoonSignHolder.cpp \
    view/MoonTransitionHolder.cpp \
    view/PlanetHourHolder.cpp \
    view/RetrogradeHolder.cpp \
    view/RetrogradeScrollHolder.cpp \
    view/TithiHolder.cpp \
    view/VcHolder.cpp \
    view/VocHolder.cpp \
    OptionsDialog.cpp

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
    util/SummaryModel.h \
    view/SunDegreeHolder.h \
    view/ViewHolder.h \
    util/SummaryDelegate.h \
    view/AspectHolder.h \
    view/AspectScrollHolder.h \
    view/EmptyHolder.h \
    view/MoonSignHolder.h \
    view/MoonTransitionHolder.h \
    view/PlanetHourHolder.h \
    view/RetrogradeHolder.h \
    view/RetrogradeScrollHolder.h \
    view/TithiHolder.h \
    view/VcHolder.h \
    view/VocHolder.h \
    OptionsDialog.h

FORMS    += \
    MainWindow.ui \
    PreferenceWindow.ui \
    view/SunDegreeHolder.ui \
    view/AspectHolder.ui \
    view/AspectScrollHolder.ui \
    view/EmptyHolder.ui \
    view/MoonSignHolder.ui \
    view/MoonTransitionHolder.ui \
    view/PlanetHourHolder.ui \
    view/RetrogradeHolder.ui \
    view/RetrogradeScrollHolder.ui \
    view/TithiHolder.ui \
    view/VcHolder.ui \
    view/VocHolder.ui \
    OptionsDialog.ui

TRANSLATIONS = \
	Astromaximum_ru.ts

CONFIG += mobility
MOBILITY = 

RESOURCES += \
    assets/assets.qrc

ANDROID_PACKAGE_SOURCE_DIR = $$PWD/android

OTHER_FILES += \
    android/AndroidManifest.xml

