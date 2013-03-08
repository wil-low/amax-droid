package com.astromaximum.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map;

public class Mutter {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int argsLen = args.length;
		Map<String, String> env = System.getenv();
		String calculationsDir = env.get("CALCULATIONS_DIR");
		long delta = SubDataProcessor.MSECINDAY * 3;
		if ((argsLen == 5) && args[0].equals("common")) {
			int year = Integer.parseInt(args[1]);
			int startMonth = Integer.parseInt(args[2]);
			int monthCount = Integer.parseInt(args[3]);
			String inputFile = calculationsDir + "/commons/" + year + ".comm";
			String outFile = args[4];
			System.out.println("common " + inputFile);
			CommonFilter filter = new CommonFilter(year, inputFile);
			filter.dumpToFile(startMonth, monthCount, delta, outFile);
			return;
		}
		if ((argsLen == 7) && args[0].equals("location")) {
			int year = Integer.parseInt(args[1]);
			int startMonth = Integer.parseInt(args[2]);
			int monthCount = Integer.parseInt(args[3]);
			String country = args[4];
			String cityId = args[5];
			String inputFile = calculationsDir + "/archive/" + year + "/"
					+ country + "/" + cityId + ".dat";
			String outFile = args[6];
			System.out.println("location " + inputFile);
			LocationFilter filter = new LocationFilter(year, inputFile);
			filter.dumpToFile(startMonth, monthCount, delta, outFile);
			return;
		} 
		System.out.println("Usage:\n\tcommon <year> <start month> <month count> <output> - generate common file");
		System.out.println("\tlocation <year> <start month> <end month> <country> <city id> <output> - generate location file");
	}
	
	static boolean writeToTempFile(String path, SubDataInfo info,
			BaseEvent[] events, int eventCount) {
		RandomAccessFile raf;
		try {
			raf = new RandomAccessFile(path + "/tmp_"
					+ info.mEventType + "_" + info.mPlanet, "rw");
			raf.writeByte(info.mEventType);
			long start = raf.getFilePointer();
			long cumul = events[0].mDate[0] / 1000;
			raf.writeShort(0); // data size placeholder
			raf.writeShort(info.mFlags);
			raf.writeByte(info.mPlanet);
			raf.writeShort(eventCount);
			//System.out.println(eventCount + " records");
			int PERIOD = 24 * 60 * 60;
			if (info.mEventType == BaseEvent.EV_ASCAPHETICS)
				PERIOD = 2 * 60 * 60;
			// if(evtype==EV_ASTRORISE) PERIOD=6*60*60;
			// v[0]->dump();
			// v[1]->dump();
			for (int i = 0; i < eventCount; i++) {
				BaseEvent ev = events[i];
				ev.mDate[0] /= 1000;
				ev.mDate[1] /= 1000;
				if (((info.mFlags & SubDataProcessor.EF_CUMUL_DATE_W) != 0) && (i > 0)) {
					int delta = (int) ((ev.mDate[0] - cumul - PERIOD) / 60);
					if (Math.abs(delta) > 32767) {
						System.out.println("Error overflow EF_CUMUL_DATE_W " + delta);
						raf.setLength(start);
						raf.close();
						return false;
					}
					cumul = ev.mDate[0];
					raf.writeShort(delta);
				} else if (((info.mFlags & SubDataProcessor.EF_CUMUL_DATE_B) != 0) && (i > 0)) {
					int delta = (int) ((ev.mDate[0] - cumul - PERIOD) / 60);
					if (Math.abs(delta) > 127) {
						System.out.println("Error overflow EF_CUMUL_DATE_B " + delta);
						raf.setLength(start);
						raf.close();
						return false;
					}
					cumul = ev.mDate[0];
					raf.writeByte(delta);
				} else {
					raf.writeInt((int) ev.mDate[0]);
				}
				if ((info.mFlags & SubDataProcessor.EF_DATE) != 0) {
					raf.writeInt((int) ev.mDate[1]);
				}
				if ((info.mFlags & SubDataProcessor.EF_PLANET1) != 0)
					raf.writeByte(ev.mPlanet0);
				if ((info.mFlags & SubDataProcessor.EF_PLANET2) != 0)
					raf.writeByte(ev.mPlanet1);
				if ((info.mFlags & SubDataProcessor.EF_DEGREE) != 0)
					if ((info.mFlags & SubDataProcessor.EF_SHORT_DEGREE) != 0)
						raf.writeByte(ev.getFullDegree());
					else {
						// TODO error - strange degree in event #7 - invalid fwrite?
						raf.writeShort(ev.getFullDegree());
					}
			}
			int fsize = (int) raf.getFilePointer();
			raf.seek(start);
			raf.writeShort(fsize);
			raf.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
}
