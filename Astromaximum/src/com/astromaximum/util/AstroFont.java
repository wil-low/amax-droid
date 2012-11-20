package com.astromaximum.util;

public class AstroFont {
	public static final int TYPE_PLANET = 0;
	public static final int TYPE_ASPECT = 1;
	public static final int TYPE_ZODIAC = 2;
	public static final int TYPE_RETROGRADE = 3;
	
	public static String getSymbol(int type, int id) {
		byte[] result = new byte[]{'?'};
		switch (type) {
		case TYPE_PLANET:
			result[0] = (byte) (0x50 + id);
			break;
		case TYPE_ASPECT:
			switch (id) {
			case 0: result[0] = 0x60; break;
			case 180: result[0] = 0x64; break;
			case 120: result[0] = 0x63; break;
			case 90: result[0] = 0x62; break;
			case 60: result[0] = 0x61; break;
			case 45: result[0] = 0x65; break;
			}
			break;
		case TYPE_ZODIAC:
			result[0] = (byte) (0x40 + id);
			break;
		case TYPE_RETROGRADE:
			result[0] = 0x24;
			break;
		}
		return new String(result);
	}
}
