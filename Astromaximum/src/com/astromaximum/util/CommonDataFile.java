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
 * @noinspection CastToConcreteClass
 */
final public class CommonDataFile extends DataFile {

	private short mStartYear;

	/**
     * DataFile
     */
    public CommonDataFile(InputStream stream) {
        try {
            DataInputStream is = new DataInputStream(stream);
            mStartYear = is.readShort();
            calendar.set(Calendar.YEAR, mStartYear);
            calendar.set(Calendar.MONTH, is.readUnsignedByte() - 1);
            calendar.set(Calendar.DAY_OF_MONTH, is.readUnsignedByte());
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            mCustomData = null;

            int count = is.readUnsignedShort(); // customData length
            mStartJD = calendar.getTime().getTime();
            mDayCount = is.readShort();
            mFinalJD = mStartJD + mDayCount * MSECINDAY;
            if (count > 0) {
                mCustomData = new byte[count];
                is.read(mCustomData);
            }
            mBuffer = new byte[is.available()];
            is.read(mBuffer);
            is.close();
        }
        catch (IOException e) {
        	e.printStackTrace();
        }
    }

    @Override
    public int readSubData(EventConsumer consumer) throws IOException {
    	final DataInputStream is = new DataInputStream(new ByteArrayInputStream(mBuffer));
    	int eventCount = 0;
    	eventCount += super.readEvents(is, consumer);
    	is.close();
    	return eventCount;
    }

	@Override
    protected void addEvent(EventConsumer consumer, Event last) {
		consumer.addEvent(mStartYear, last);
	}
}

