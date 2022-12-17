package com.fss.aeps.npciclient;

import java.security.cert.CertificateException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;

public class UnsafeOkHttp {

	private static final X509TrustManager[] trustManagers = new X509TrustManager[] { new X509TrustManager() {

		@Override
		public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
		}

		@Override
		public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
		}

		@Override
		public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			return new java.security.cert.X509Certificate[] {};
		}
	} };

	public static final OkHttpClient.Builder Builder() {
		try {
			// Install the all-trusting trust manager
			final SSLContext sslContext = SSLContext.getInstance("TLS");
			sslContext.init(null, trustManagers, new java.security.SecureRandom());

			// Create an ssl socket factory with our all-trusting manager
			final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

			OkHttpClient.Builder builder = new OkHttpClient.Builder();
			builder.sslSocketFactory(sslSocketFactory, trustManagers[0]);
			builder.hostnameVerifier((h, s) -> true);
			return builder;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}