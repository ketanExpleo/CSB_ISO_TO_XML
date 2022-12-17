package com.fss.aeps.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

public class ServletUtils {

	public static final HttpServletRequestWrapper getWrappedRequest(HttpServletRequest request, byte[] requestBytes) {
		return new HttpServletRequestWrapper(request) {

			@Override
			public ServletInputStream getInputStream() throws IOException {
				return getServletInputStream(requestBytes);
			}

			@Override
			public BufferedReader getReader() throws IOException {
				return new BufferedReader(new InputStreamReader(new ByteArrayInputStream(requestBytes)));
			}
		};
	}

	private static final ServletInputStream getServletInputStream(final byte[] bytes) {
		return new ServletInputStream() {
			private ByteArrayInputStream baos = new ByteArrayInputStream(bytes);

			@Override
			public int read() throws IOException {
				return baos.read();
			}

			@Override
			public void setReadListener(ReadListener listener) {
			}

			@Override
			public boolean isReady() {
				return false;
			}

			@Override
			public boolean isFinished() {
				return false;
			}
		};
	}

	public static final HttpServletResponseWrapper getWrappedResponse(HttpServletResponse response, ByteArrayOutputStream baos) {
		return new HttpServletResponseWrapper(response) {

			@Override
			public PrintWriter getWriter() {
				return new PrintWriter(baos);
			}

			@Override
			public ServletOutputStream getOutputStream() throws IOException {
				return getServletOutputStream(baos);
			}

		};
	}

	private static final ServletOutputStream getServletOutputStream(ByteArrayOutputStream baos) {
		return new ServletOutputStream() {

			@Override
			public void write(int b) throws IOException {
				baos.write(b);
			}

			@Override
			public void setWriteListener(WriteListener listener) {
			}

			@Override
			public boolean isReady() {
				return true;
			}
		};
	}



}
