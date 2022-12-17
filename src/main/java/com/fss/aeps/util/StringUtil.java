package com.fss.aeps.util;

public final class StringUtil {

	public static final int parseIntElseZero(String string) {
		try {
			return Integer.parseInt(string);
		} catch (Exception e) {}
		return 0;
	}
}
