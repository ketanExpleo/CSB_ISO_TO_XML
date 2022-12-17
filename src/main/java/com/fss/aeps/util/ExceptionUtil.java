package com.fss.aeps.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.Blob;

import org.hibernate.engine.jdbc.BlobProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ExceptionUtil {

	private static final Logger logger = LoggerFactory.getLogger(ExceptionUtil.class);

	public static final byte[] toString(final Throwable throwable) {
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		throwable.printStackTrace(new PrintStream(baos));
		return baos.toByteArray();
	}

	public static final Blob appendBlob(final Blob blob, final Throwable throwable) {
		try {
			final ByteArrayOutputStream baos = new ByteArrayOutputStream();
			if(blob != null) baos.write(blob.getBytes(0, (int) blob.length()));
			baos.write("\r\n\r\n\r\n".getBytes());
			throwable.printStackTrace(new PrintStream(baos));
			return BlobProxy.generateProxy(baos.toByteArray());
		} catch (Exception e) {
			logger.error("error creating blob.");
		}
		return blob;
	}
}
