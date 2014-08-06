#pragma once

template <class T>
class Singleton
{
public:
    static T* instance()
    {
        static T mInstance; // create static instance of our class
        return &mInstance;   // return it
    }
 
private:
    Singleton();	// hide constructor
    ~Singleton();	// hide destructor
    Singleton(const Singleton &); // hide copy constructor
    Singleton& operator=(const Singleton &); // hide assign op
};
