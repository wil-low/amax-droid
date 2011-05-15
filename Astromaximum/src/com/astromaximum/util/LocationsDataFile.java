package com.astromaximum.util;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.TimeZone;

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
final public class LocationsDataFile extends DataFile {
    private static final int EF_DATE = 0x1; // contains 2nd date - 4b
    private static final int EF_PLANET1 = 0x2; // contains 1nd planet - 1b
    private static final int EF_PLANET2 = 0x4; // contains 2nd planet - 1b
    private static final int EF_DEGREE = 0x8; // contains degree or angle - 2b
    private static final int EF_CUMUL_DATE_B = 0x10; // date are cumulative from 1st 4b - 1b
    private static final int EF_CUMUL_DATE_W = 0x20; // date are cumulative from 1st 4b - 2b
    private static final int EF_SHORT_DEGREE = 0x40; // contains angle 0..180 - 1b
    private static final int EF_NEXT_DATE2 = 0x80; // 2nd date is 1st in next event
    public static final long MSECINDAY = 86400 * 1000;
    private long mStartJD, mFinalJD, mCurrentTimeZoneId;
    private int mDayCount;
    //  private final Vector cache=new Vector();
    private byte[] mBuffer;
    byte[] mCustomData;
	private short mStartYear;
	private int mRecordCount;
	private int[] mRecordLengths;
	private DataInputStream mLocStream = null;
    public static final Calendar calendar = getUtcCalendar();
    /**
     * DataFile
     */
    public LocationsDataFile(InputStream stream) {
        try {
            DataInputStream is = new DataInputStream(stream);
            mStartYear = is.readShort();
            mRecordCount = is.readUnsignedShort();
            mRecordLengths = new int[mRecordCount];
            for (int i = 0; i < mRecordCount; ++i)
            	mRecordLengths[i] = is.readUnsignedShort();
            mBuffer = new byte[is.available()];
            is.read(mBuffer);
            is.close();
        	mLocStream = new DataInputStream(new ByteArrayInputStream(mBuffer));
        }
        catch (IOException e) {
        	e.printStackTrace();
        }
    }

    @Override
    public int readSubData(EventConsumer consumer) throws IOException {
    	int eventCount = 0;
        for (int i = 0; i < mRecordCount; ++i) {
        	final DataInputStream is = new DataInputStream(new ByteArrayInputStream(extractLocation(i)));
        	is.skip(2);
            calendar.set(Calendar.YEAR, mStartYear);
            calendar.set(Calendar.MONTH, is.readUnsignedByte() - 1);
            calendar.set(Calendar.DAY_OF_MONTH, is.readUnsignedByte());
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            mCustomData = null;

            int customDataLength = is.readUnsignedShort(); // customData length
            mStartJD = calendar.getTime().getTime();
            mDayCount = is.readShort();
            mFinalJD = mStartJD + mDayCount * MSECINDAY;
            
            Location location = new Location();
            location.mYear = mStartYear;
            location.mName = is.readUTF();
            System.out.println(location.mName);
            int tzOffset = is.readUnsignedShort();
            boolean dstExists = (tzOffset & (1 << 15)) == 0;
            tzOffset &= (1 << 15) - 1;
            tzOffset -= 16 * 60;
            tzOffset *= 60000L;
            location.mTzOffset = tzOffset;
            long d_1, d_2;
            if (dstExists) {
                d_1 = is.readInt() * 60000L - tzOffset;
                d_2 = is.readInt() * 60000L - tzOffset - 3600000L;
                if (d_1 < d_2) { // N hemisphere
                    location.mIsSouthern = false;
                	location.mDstStart = d_1;
                	location.mDstEnd = d_2;
                } else {
                	location.mDstStart = d_2;
                	location.mDstEnd = d_1;
                    location.mIsSouthern = true;
                }
            }
            if (customDataLength > 0) {
                mCustomData = new byte[customDataLength];
                is.read(mCustomData);
            }
            consumer.addLocation(location);
            mCurrentTimeZoneId = location.mTimeZoneId;
        	eventCount += super.readEvents(is, consumer);
        }
        mLocStream.close();
    	return eventCount;
    }

	@Override
    protected void addEvent(EventConsumer consumer, Event last) {
		consumer.addEvent(mStartYear, last, mCurrentTimeZoneId);
	}
	
    private byte[] extractLocation(int index) {
        byte[] res = null;
        try {
            mLocStream.reset();
            int off = 0;
            for (int i = 0; i < index; i++) {
                off += mRecordLengths[i];
            }
            final int len = mRecordLengths[index];
            mLocStream.skip(off);
            res = new byte[len + 1];
            mLocStream.read(res);
        } catch (IOException ex) {
//      ex.printStackTrace();
        }
        return res;
    }
}

