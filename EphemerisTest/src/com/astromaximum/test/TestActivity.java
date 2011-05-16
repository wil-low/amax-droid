package com.astromaximum.test;

import com.astromaximum.util.SwissEphemeris;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class TestActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Log.d("TestActivity", "onCreate");
        TextView  tv = new TextView(this);
        int       x  = 1000;
        int       y  = 42;
        
        SwissEphemeris eph = new SwissEphemeris();
        eph.setEphePath(getFilesDir().getAbsolutePath());
        int planet = 3;
        
        long start = System.currentTimeMillis();
        double julday = 0;
        for (int i = 0; i < 100; ++i)
        	julday = eph.getJulday(2010, 5, 8, 11.3, true);
        long stop = System.currentTimeMillis();
        long elapsed0 = stop - start;
        start = stop;
        
        double[] lat_lon_rad = null;
        for (int i = 0; i < 100; ++i)
        	lat_lon_rad = eph.calcUT(julday, planet, 0);
        stop = System.currentTimeMillis();
        long elapsed1 = stop - start;
        start = stop;

        double[] houses = null;
        for (int i = 0; i < 100; ++i)
        	houses = eph.calcHouses(julday, 40.53, 34.2, 'P');
        stop = System.currentTimeMillis();
        long elapsed2 = stop - start;
        start = stop;

        double[] eclipse = null;
        for (int i = 0; i < 1; ++i)
        	eclipse = eph.solEclipseWhenGlob(julday, 0, 0, false);
        stop = System.currentTimeMillis();
        long elapsed3 = stop - start;
        start = stop;

        String latLonRad = new String();
        for (int i = 0; i < lat_lon_rad.length; ++i)
        	latLonRad += i + ": " + lat_lon_rad[i] + "\n";

        String housesStr = new String();
        for (int i = 0; i < 5; ++i)
        	housesStr += i + ": " + houses[i] + "\n";
        
        String eclipseStr = new String();
        for (int i = 0; i < 5; ++i)
        	eclipseStr += i + ": " + eclipse[i] + "\n";

        tv.setText( getFilesDir().getAbsolutePath() + "\nElapsed: " + elapsed0 + ", " + elapsed1 + ", " + elapsed2 + ", " + elapsed3 + "\n" +
        		"Planet name of " +  planet + " is " + eph.getPlanetName(planet) + "\n"
        		+ julday + "\n\n" + latLonRad + "\n\n" + housesStr + "\n\n" + eclipseStr);
        setContentView(tv);
        eph.close();
    }
}
