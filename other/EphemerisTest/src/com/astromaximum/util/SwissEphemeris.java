package com.astromaximum.util;

public class SwissEphemeris {
	static {
		System.loadLibrary("swephdroid");
	}
	
	public native void setEphePath(String path);
	public native void close();
	public native String getPlanetName(int planet);
	public native double getJulday(int year, int month, int day, double hour, boolean gregFlag);
	public native double[] calcUT(double julday, int planetId, int flag);
	public native double[] calcHouses(double julday, double geoLat, double geoLon, char houseSystem);
	public native double[] solEclipseWhenGlob(double juldayUTStart, int ephFlag, int eclipseType,
			boolean isBackwardSearch);
	
}
