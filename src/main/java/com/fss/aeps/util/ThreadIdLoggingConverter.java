package com.fss.aeps.util;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

public final class ThreadIdLoggingConverter extends ClassicConverter {

	@Override
	public final String convert(ILoggingEvent arg0) {
		return String.format("%04d", Thread.currentThread().getId());
	}

}
