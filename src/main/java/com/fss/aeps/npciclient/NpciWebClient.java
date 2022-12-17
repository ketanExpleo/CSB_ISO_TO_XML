package com.fss.aeps.npciclient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fss.aeps.AppConfig;
import com.fss.aeps.http.filters.ClientSignatureInterceptor;
import com.fss.aeps.http.filters.LoggingInterceptor;
import com.fss.aeps.jaxb.Ack;
import com.fss.aeps.jaxb.ErrorMessage;
import com.fss.aeps.jaxb.ReqBalEnq;
import com.fss.aeps.jaxb.ReqBioAuth;
import com.fss.aeps.jaxb.ReqChkTxn;
import com.fss.aeps.jaxb.ReqHbt;
import com.fss.aeps.jaxb.ReqPay;
import com.fss.aeps.jaxb.RespBalEnq;
import com.fss.aeps.jaxb.RespChkTxn;
import com.fss.aeps.jaxb.RespHbt;
import com.fss.aeps.jaxb.RespPay;
import com.fss.aeps.util.BiometricUtil;

import okhttp3.OkHttpClient;
import reactor.core.publisher.Mono;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.jaxb.JaxbConverterFactory;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class NpciWebClient implements NpciClient {

	private static final Logger logger = LoggerFactory.getLogger(NpciWebClient.class);
	private ReentrantLock lock = new ReentrantLock();
	private List<Retrofit> retroclients = new ArrayList<>();
	private Map<String, Retrofit> callbackClientMap = new HashMap<>();

	private Retrofit retroclient;

	@Autowired
	private AppConfig appConfig;

	private int clientIndex = 0;
	private final String protocol;
	private final int npciPort;

	public NpciWebClient(@Autowired AppConfig appConfig, @Autowired ClientSignatureInterceptor clientSignatureInterceptor) {
		protocol = appConfig.npciProtocol;
		npciPort = appConfig.npciPort;
		final List<String> npciIps = Arrays.asList(appConfig.npciIps.split(",")).stream().map(ip -> ip.trim())
				.filter(ip -> ip.length() > 0).collect(Collectors.toList());
		final OkHttpClient.Builder builder = UnsafeOkHttp.Builder();
		builder.addNetworkInterceptor(appConfig.context.getBean(LoggingInterceptor.class));
		builder.addInterceptor(clientSignatureInterceptor);
		builder.connectTimeout(appConfig.npciConnectTimeout, TimeUnit.MILLISECONDS);
		builder.readTimeout(appConfig.npciReadTimeout, TimeUnit.MILLISECONDS);
		npciIps.forEach(npciIp -> {
			Retrofit client = new Retrofit.Builder().client(builder.build())
					.addConverterFactory(JaxbConverterFactory.create()).baseUrl(getBaseURL(protocol, npciIp, npciPort)).build();
			retroclients.add(client);
			callbackClientMap.put(npciIp, client);
		});
		retroclient = retroclients.get(clientIndex);
	}

	@Override
	public final Mono<Ack> heartbeat(final ReqHbt request) {
		final Retrofit client = retroclient;
		final NpciApi service = retroclient.create(NpciApi.class);
		final Call<Ack> call = service.heartbeat(request.getHead().getVer(), request.getTxn().getId(), request);
		return Mono.just(getSuppressedResponse(call, client));
	}

	@Override
	public final Mono<Ack> payment(final ReqPay request) {
		final Retrofit client = retroclient;
		final NpciApi service = retroclient.create(NpciApi.class);
		final Call<Ack> call = service.paymentRequest(request.getHead().getVer(), request.getTxn().getId(), request);
		return Mono.just(getSuppressedResponse(call, client));
	}

	@Override
	public final Mono<Ack> advice(final ReqChkTxn request) {
		BiometricUtil.setAUADetails(request, appConfig);
		final Retrofit client = retroclient;
		final NpciApi service = retroclient.create(NpciApi.class);
		final Call<Ack> call = service.verificationRequest(request.getHead().getVer(), request.getTxn().getId(), request);
		return Mono.just(getSuppressedResponse(call, client));
	}

	@Override
	public final Mono<Ack> balanceEnquiry(final ReqBalEnq request) {
		BiometricUtil.setAUADetails(request, appConfig);
		final Retrofit client = retroclient;
		final NpciApi service = retroclient.create(NpciApi.class);
		final Call<Ack> call = service.balanceEnquiry(request.getHead().getVer(), request.getTxn().getId(), request);
		return Mono.just(getSuppressedResponse(call, client));
	}

	@Override
	public final Mono<Ack> bioAuth(final ReqBioAuth request) {
		BiometricUtil.setAUADetails(request, appConfig);
		final Retrofit client = retroclient;
		final NpciApi service = retroclient.create(NpciApi.class);
		final Call<Ack> call = service.bioAuth(request.getHead().getVer(), request.getTxn().getId(), request);
		return Mono.just(getSuppressedResponse(call, client));
	}

	@Override
	public final Mono<Ack> heartbeatResponse(final RespHbt response) {
		final String callbackIp = response.getHead().getCallbackEndpointIP();
		final Retrofit client = getCallbackClient(callbackIp);
		final NpciApi service = client.create(NpciApi.class);
		final Call<Ack> call = service.heartbeatResponse(response.getHead().getVer(), response.getTxn().getId(), response);
		return Mono.just(getSuppressedResponse(call, client));
	}

	@Override
	public final Mono<Ack> paymentResponse(final RespPay response) {
		final String callbackIp = response.getHead().getCallbackEndpointIP();
		final Retrofit client = getCallbackClient(callbackIp);
		final NpciApi service = client.create(NpciApi.class);
		final Call<Ack> call = service.paymentResponse(response.getHead().getVer(), response.getTxn().getId(), response);
		return Mono.just(getSuppressedResponse(call, client));
	}

	@Override
	public final Mono<Ack> balanceEnquiryResponse(final RespBalEnq response) {
		final String callbackIp = response.getHead().getCallbackEndpointIP();
		final Retrofit client = getCallbackClient(callbackIp);
		final NpciApi service = client.create(NpciApi.class);
		final Call<Ack> call = service.balanceEnquiryResponse(response.getHead().getVer(), response.getTxn().getId(), response);
		return Mono.just(getSuppressedResponse(call, client));
	}

	@Override
	public final Mono<Ack> verificationResponse(final RespChkTxn response) {
		final String callbackIp = response.getHead().getCallbackEndpointIP();
		final Retrofit client = getCallbackClient(callbackIp);
		final NpciApi service = client.create(NpciApi.class);
		final Call<Ack> call = service.verificationResponse(response.getHead().getVer(), response.getTxn().getId(), response);
		return Mono.just(getSuppressedResponse(call, client));
	}

	private static Ack getSuppressedResponse(final Call<Ack> call, Retrofit retroclient) {
		try {
			return call.execute().body();
		} catch (Exception e) {
			logger.error("error on endpoint : "+retroclient.baseUrl(), e);
			final Ack ack = new Ack();
			ack.setErr("ack not received.");
			ack.getErrorMessages().add(new ErrorMessage("U18", e.getMessage()));
			return ack;
		}
	}

	private static final String getBaseURL(String protocol, String ip, int npciPort) {
		return protocol+"://"+ip+":"+npciPort+"/aeps/";
	}

	private final Retrofit getCallbackClient(String callbackIp) {
		if(callbackIp == null) return retroclient;
		Retrofit client = callbackClientMap.get(callbackIp);
		if(client != null) return client;
		try {
			lock.lock();
			client = retroclient.newBuilder().baseUrl(getBaseURL(protocol, callbackIp, npciPort)).build();
			callbackClientMap.put(callbackIp, client);
			return client;
		} finally {
			lock.unlock();
		}
	}

	public int getClientIndex() {
		return clientIndex;
	}

	private int getNextClientIndex() {
		return (clientIndex+1) % getClientCount();
	}

	public int getClientCount() {
		return retroclients.size();
	}

	public void changeClientToNextIp() {
		if(getClientCount() < 2) return;
		try {
			lock.lock();
			final int nextIndex = getNextClientIndex();
			this.clientIndex = nextIndex;
			Retrofit oldClient = retroclient;
			this.retroclient = retroclients.get(nextIndex);
			logger.info("Npci client changed to : "+retroclient.baseUrl()+" from : "+oldClient.baseUrl());
		} finally {
			lock.unlock();
		}
	}

}
