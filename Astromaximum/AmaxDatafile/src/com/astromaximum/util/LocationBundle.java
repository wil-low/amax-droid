package com.astromaximum.util;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * <p>
 * Title: Astromaximum
 * </p>
 * <p/>
 * <p>
 * Description:
 * </p>
 * <p/>
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * <p/>
 * <p>
 * Company: Wiland Inc.
 * </p>
 * 
 * @author Andrei Ivushkin
 * @version 1.0
 */
final public class LocationBundle {
	public int mRecordCount;
	private int[] mRecordLengths;
	private DataInputStream mLocStream;

	public LocationBundle(InputStream stream) {
		try {
			DataInputStream is = new DataInputStream(stream);
			is.readShort(); // skip year
			mRecordCount = is.readUnsignedShort();
			mRecordLengths = new int[mRecordCount];
			for (int i = 0; i < mRecordCount; ++i)
				mRecordLengths[i] = is.readUnsignedShort();
			byte[] buffer = new byte[is.available()];
			is.read(buffer);
			is.close();
			mLocStream = new DataInputStream(new ByteArrayInputStream(buffer));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public byte[] extractLocation(int index) {
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
			// ex.printStackTrace();
		}
		return res;
	}
}
