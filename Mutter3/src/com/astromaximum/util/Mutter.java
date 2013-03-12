package com.astromaximum.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.Scanner;
import java.util.Vector;

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
		Vector<String> filenames = new Vector<String>();
		if ((argsLen == 6) && args[0].equals("location")) {
			int year = Integer.parseInt(args[1]);
			int startMonth = Integer.parseInt(args[2]);
			int monthCount = Integer.parseInt(args[3]);
			String city = args[4];
			filenames.add(calculationsDir + "/archive/" + year + "/" + city + ".dat");
			String outFile = args[5];
			LocationFilter filter = new LocationFilter(year, filenames);
			filter.dumpToFile(startMonth, monthCount, delta, outFile);
			return;
		}
		if ((argsLen == 6) && args[0].equals("locations")) {
			int year = Integer.parseInt(args[1]);
			int startMonth = Integer.parseInt(args[2]);
			int monthCount = Integer.parseInt(args[3]);
			String locationsList = args[4];
			String outFile = args[5];
			Scanner input;
			try {
				input = new Scanner(new FileInputStream(locationsList));
				while (input.hasNext()) {
					String fn = input.nextLine();
					int spacePos = fn.indexOf(" ");
					if (spacePos != 1)
						fn = fn.substring(0, spacePos);
					filenames.add(calculationsDir + "/archive/" + year + "/" + fn + ".dat");
				}
				input.close();
				LocationFilter filter = new LocationFilter(year, filenames);
				filter.dumpToFile(startMonth, monthCount, delta, outFile);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
		System.out.println("Usage:\n\tcommon <year> <start month> <month count> <output> - generate common file");
		System.out.println("\tlocation <year> <start month> <end month> <country/city id> <output> - generate location file");
		System.out.println("\tlocations <year> <start month> <end month> <location list file> <output> - generate multi-location file");
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
			for (int i = 0; i < eventCount; i++) {
				BaseEvent ev = events[i];
				ev.mDate[0] /= 1000;
				ev.mDate[1] /= 1000;
				if (((info.mFlags & SubDataProcessor.EF_CUMUL_DATE_W) != 0) && (i > 0)) {
					int delta = (int) ((ev.mDate[0] - cumul - PERIOD) / 60);
					if (Math.abs(delta) > 32767) {
						//System.out.println("Error overflow EF_CUMUL_DATE_W " + delta);
						raf.setLength(start);
						raf.close();
						return false;
					}
					cumul = ev.mDate[0];
					raf.writeShort(delta);
				} else if (((info.mFlags & SubDataProcessor.EF_CUMUL_DATE_B) != 0) && (i > 0)) {
					int delta = (int) ((ev.mDate[0] - cumul - PERIOD) / 60);
					if (Math.abs(delta) > 127) {
						//System.out.println("Error overflow EF_CUMUL_DATE_B " + delta);
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
					else
						raf.writeShort(ev.getFullDegree());
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
