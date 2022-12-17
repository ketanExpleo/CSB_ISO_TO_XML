package com.fss.aeps.broadcast;

import com.fss.aeps.jaxb.Ack;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface BroadcastApi {

	@Headers({"Accept: application/xml", "BROADCASTED_RESPONSE: true"})
	@POST("RespHbt/{version}/urn:txnid:{id}")
	public Call<Ack> heartbeatResponse(@Path("version") String version, @Path("id") final String txnid, @Body final RequestBody response);

	@Headers({"Accept: application/xml", "BROADCASTED_RESPONSE: true"})
	@POST("RespBalEnq/{version}/urn:txnid:{id}")
	public Call<Ack> balanceEnquiryResponse(@Path("version") String version, @Path("id") final String txnid, @Body final RequestBody response);

	@Headers({"Accept: application/xml", "BROADCASTED_RESPONSE: true"})
	@POST("RespPay/{version}/urn:txnid:{id}")
	public Call<Ack> paymentResponse(@Path("version") String version, @Path("id") final String txnid, @Body final RequestBody response);

	@Headers({"Accept: application/xml", "BROADCASTED_RESPONSE: true"})
	@POST("RespChkTxn/{version}/urn:txnid:{id}")
	public Call<Ack> verificationResponse(@Path("version") String version, @Path("id") final String txnid, @Body final RequestBody response);

	@Headers({"Accept: application/xml", "BROADCASTED_RESPONSE: true"})
	@POST("RespBioAuth/{version}/urn:txnid:{id}")
	public Call<Ack> bioAuthResponse(@Path("version") String version, @Path("id") final String txnid, @Body final RequestBody response);

}