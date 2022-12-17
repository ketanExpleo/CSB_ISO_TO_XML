package com.fss.aeps.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public final class Converter {

	public static final LocalDateTime dateToLocalDateTime(Date date) {
		return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
	}
}
