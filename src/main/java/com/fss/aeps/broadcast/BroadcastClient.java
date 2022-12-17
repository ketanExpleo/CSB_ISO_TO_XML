package com.fss.aeps.broadcast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fss.aeps.AppConfig;
import com.fss.aeps.http.filters.LoggingInterceptor;
import com.fss.aeps.jaxb.Ack;
import com.fss.aeps.jaxb.RespBalEnq;
import com.fss.aeps.jaxb.RespBioAuth;
import com.fss.aeps.jaxb.RespChkTxn;
import com.fss.aeps.jaxb.RespHbt;
import com.fss.aeps.jaxb.RespPay;
import com.fss.aeps.npciclient.UnsafeOkHttp;
import com.fss.aeps.util.NetworkUtil;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import reactor.core.publisher.Mono;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.jaxb.JaxbConverterFactory;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class BroadcastClient {

	private static final Logger logger = LoggerFactory.getLogger(BroadcastClient.class);
	private static final MediaType mediaType = MediaType.parse("application/xml");

	private List<Retrofit> retroclients = new ArrayList<>();

	private final String protocol;
	private final int destinationPort;

	public BroadcastClient(@Autowired AppConfig appConfig) {
		TomcatServletWebServerFactory tomcat = appConfig.context.getBean(TomcatServletWebServerFactory.class);
		protocol = tomcat.getSsl() == null ? "http" : "https";
		destinationPort = tomcat.getPort();
		logger.info("initialized broadcaster with protocol : "+protocol);
		logger.info("initialized broadcaster with destinationPort : "+destinationPort);
		final List<String> switchNodes = new ArrayList<>(Arrays.asList(appConfig.switchNodeIps.split(",")));
		switchNodes.removeAll(NetworkUtil.getSystemIpAddresses());
		final List<String> switchNodeIps = switchNodes.stream().map(ip -> ip.trim()).filter(ip -> ip.length() > 0)
				.collect(Collectors.toList());
		logger.info("initialized broadcaster with switchNodeIps : "+switchNodeIps);
		final OkHttpClient.Builder builder = UnsafeOkHttp.Builder();
		builder.addNetworkInterceptor(appConfig.context.getBean(LoggingInterceptor.class));
		builder.connectTimeout(appConfig.npciConnectTimeout, TimeUnit.MILLISECONDS);
		builder.readTimeout(appConfig.npciReadTimeout, TimeUnit.MILLISECONDS);
		switchNodeIps.forEach(ip -> {
			Retrofit client = new Retrofit.Builder().client(builder.build())
					.addConverterFactory(JaxbConverterFactory.create()).baseUrl(getBaseURL(protocol, ip, destinationPort)).build();
			retroclients.add(client);
		});
	}

	public final Mono<Ack> broadcast(final RespHbt response, final byte[] responseBytes) {
		for(Retrofit client : retroclients) {
			try {
				final BroadcastApi service = client.create(BroadcastApi.class);
				final RequestBody body = RequestBody.create(responseBytes, mediaType);
				final Call<Ack> call = service.heartbeatResponse(response.getHead().getVer(), response.getTxn().getId(), body);
				call.execute();
			} catch (Exception e) {logger.error("error broadcasting response to : "+client.baseUrl(), e);}
		}
		return null;
	}

	public final Mono<Ack> broadcast(final RespBioAuth response, final byte[] responseBytes) {
		for(Retrofit client : retroclients) {
			try {
				final BroadcastApi service = client.create(BroadcastApi.class);
				final RequestBody body = RequestBody.create(responseBytes, mediaType);
				final Call<Ack> call = service.bioAuthResponse(response.getHead().getVer(), response.getTxn().getId(), body);
				call.execute();
			} catch (Exception e) {logger.error("error broadcasting response to : "+client.baseUrl(), e);}
		}
		return null;
	}


	public final Mono<Ack> broadcast(final RespPay response, final byte[] responseBytes) {
		for(Retrofit client : retroclients) {
			try {
				final BroadcastApi service = client.create(BroadcastApi.class);
				final RequestBody body = RequestBody.create(responseBytes, mediaType);
				final Call<Ack> call = service.paymentResponse(response.getHead().getVer(), response.getTxn().getId(), body);
				call.execute();
			} catch (Exception e) {logger.error("error broadcasting response to : "+client.baseUrl(), e);}
		}
		return null;
	}

	public final Mono<Ack> broadcast(final RespBalEnq response, final byte[] responseBytes) {
		for(Retrofit client : retroclients) {
			try {
				final BroadcastApi service = client.create(BroadcastApi.class);
				final RequestBody body = RequestBody.create(responseBytes, mediaType);
				final Call<Ack> call = service.balanceEnquiryResponse(response.getHead().getVer(), response.getTxn().getId(), body);
				call.execute();
			} catch (Exception e) {logger.error("error broadcasting response to : "+client.baseUrl(), e);}
		}
		return null;
	}

	public final Mono<Ack> broadcast(final RespChkTxn response, final byte[] responseBytes) {
		for(Retrofit client : retroclients) {
			try {
				final BroadcastApi service = client.create(BroadcastApi.class);
				final RequestBody body = RequestBody.create(responseBytes, mediaType);
				final Call<Ack> call = service.verificationResponse(response.getHead().getVer(), response.getTxn().getId(), body);
				call.execute();
			} catch (Exception e) {logger.error("error broadcasting response to : "+client.baseUrl(), e);}
		}
		return null;
	}

	private static final String getBaseURL(String protocol, String ip, int serverPort) {
		return protocol+"://"+ip+":"+serverPort+"/aeps/";
	}
}
