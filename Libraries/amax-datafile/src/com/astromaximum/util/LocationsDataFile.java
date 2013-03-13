package com.astromaximum.util;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * <p>Title: Astromaximum</p>
 * <p/>
 * <p>Description: </p>
 * <p/>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p/>
 * <p>Company: Wiland Inc.</p>
 *
 * @author Andrei Ivushkin
 * @version 1.0
 * @noinspection CastToConcreteClass
 */
final public class LocationsDataFile {
	static final String TAG = "LocationsDataFile";
	public int mStartYear;
	public int mStartMonth;
	public int mStartDay;
	public int mMonthCount;
	public int mCityId;
	int[] mCoords = new int[3];
	public String mCity;
	String mState;
	String mCountry;
	public String mTimezone;
	String mCustomData;
	TimezoneTransition[] mTransitions;
	public DataInputStream mData;

    /**
     * DataFile
     */
    public LocationsDataFile(InputStream stream) {
        try {
        	DataInputStream dis = new DataInputStream(stream);
        	dis.skip(4); // signature
            byte version = dis.readByte();
            mStartYear = dis.readShort();
            mStartMonth = dis.readUnsignedByte() - 1;
            mStartDay = dis.readUnsignedByte();
            if (version == 3) {
            	mMonthCount = dis.readUnsignedByte();
            }
            else if (version == 2) {
            	dis.readShort();
            	mMonthCount = 12;
            }
            else {
                System.out.println("Unknown version " + version);
            }        
            mCityId = dis.readInt(); // city id
            mCoords[0] = dis.readShort(); // latitude
            mCoords[1] = dis.readShort(); // longitude
            mCoords[2] = dis.readShort(); // altitude
            mCity = dis.readUTF(); // city
            mState = dis.readUTF(); // state
            mCountry = dis.readUTF(); // country
            mTimezone = dis.readUTF(); // timezone
            mCustomData = dis.readUTF(); // custom data
            int transitionCount = dis.readByte();
            mTransitions = new TimezoneTransition[transitionCount];
            for (int i = 0; i < transitionCount; ++i) {
            	TimezoneTransition transition = new TimezoneTransition();
            	transition.mTime = (long)dis.readInt() * 1000; // start_date
            	transition.mOffset = (long)dis.readShort() * 60000; // gmt_ofs_min
            	transition.mName = dis.readUTF(); // name
                //MyLog.d(TAG, transition.mTime + ", " + new Date(transition.mTime) + " > " + transition.mOffset + " " + transition.mName);
                mTransitions[i] = transition;
            }
            byte[] buffer = new byte[dis.available()];
            dis.read(buffer);
            dis.close();
            mData = new DataInputStream(new ByteArrayInputStream(buffer));
        }
        catch (IOException e) {
        	e.printStackTrace();
        }
    }
}

