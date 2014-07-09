#-------------------------------------------------
#
# Project created by QtCreator 2014-07-03T19:48:05
#
#-------------------------------------------------

QT       += core gui

greaterThan(QT_MAJOR_VERSION, 4): QT += widgets

TARGET = amax-qt
TEMPLATE = app


SOURCES += main.cpp\
    PreferenceWindow.cpp \
    MainWindow.cpp

HEADERS  += \
    MainWindow.h \
    PreferenceWindow.h

FORMS    += \
    MainWindow.ui \
    PreferenceWindow.ui

CONFIG += mobility
MOBILITY = 

RESOURCES += \
    assets/assets.qrc

