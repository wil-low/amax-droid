package com.astromaximum.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;

public class Mutter {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Map<String, String> env = System.getenv();
		String calculationsDir = env.get("CALCULATIONS_DIR");
		if (args[0].equals("common")) {
			int year = Integer.parseInt(args[1]);
			int startMonth = Integer.parseInt(args[2]);
			int monthCount = Integer.parseInt(args[3]);
			String inputFile = calculationsDir + "/commons/" + year + ".comm";
			String outFile = args[4];
			System.out.println("common " + inputFile);
			CommonFilter filter = new CommonFilter(year, inputFile);
			filter.dumpToFile(startMonth, monthCount, outFile);
		}
		else if (args[0].equals("location")) {
			System.out.println("location " + args[4]);
		}
		else {
			System.out.println("Usage:\n\tcommon <year> <start month> <month count> <output> - generate common file");
			System.out.println("\tlocation <year> <start month> <end month> <city id> <output> - generate location file");
		}
	}

}
