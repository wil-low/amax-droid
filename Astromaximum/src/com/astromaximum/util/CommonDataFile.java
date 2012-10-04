package com.astromaximum.util;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

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
 */
final public class CommonDataFile {

	int mStartYear;
	int mStartMonth;
	int mStartDay;
	byte[] mCustomData;
	int mDayCount;
	DataInputStream mData;

	public CommonDataFile(InputStream stream, Calendar calendar) {
        try {
            DataInputStream is = new DataInputStream(stream);
            mStartYear = is.readShort();
            mStartMonth = is.readUnsignedByte();
            mStartDay = is.readUnsignedByte();
            
            calendar.set(Calendar.YEAR, mStartYear);
            calendar.set(Calendar.MONTH, mStartMonth - 1);
            calendar.set(Calendar.DAY_OF_MONTH, mStartDay);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            int customDataLen = is.readUnsignedShort(); // customData length
            mDayCount = is.readShort();
            if (customDataLen > 0) {
                mCustomData = new byte[customDataLen];
                is.read(mCustomData);
            }
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();
            mData = new DataInputStream(new ByteArrayInputStream(buffer));
        }
        catch (IOException e) {
        	e.printStackTrace();
        }
    }
}

