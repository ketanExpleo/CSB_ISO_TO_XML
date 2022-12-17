package com.fss.aeps.npciclient;

import com.fss.aeps.jaxb.Ack;
import com.fss.aeps.jaxb.ReqBalEnq;
import com.fss.aeps.jaxb.ReqBioAuth;
import com.fss.aeps.jaxb.ReqChkTxn;
import com.fss.aeps.jaxb.ReqHbt;
import com.fss.aeps.jaxb.ReqPay;
import com.fss.aeps.jaxb.RespBalEnq;
import com.fss.aeps.jaxb.RespChkTxn;
import com.fss.aeps.jaxb.RespHbt;
import com.fss.aeps.jaxb.RespPay;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface NpciApi {

	@Headers({"Accept: application/xml"})
	@POST("ReqHbt/{version}/urn:txnid:{id}")
	public Call<Ack> heartbeat(@Path("version") String version, @Path("id") final String txnid, @Body final ReqHbt request);

	@Headers({"Accept: application/xml"})
	@POST("ReqBalEnq/{version}/urn:txnid:{id}")
	public Call<Ack> balanceEnquiry(@Path("version") String version, @Path("id") final String txnid, @Body final ReqBalEnq request);

	@Headers({"Accept: application/xml"})
	@POST("ReqBioAuth/{version}/urn:txnid:{id}")
	public Call<Ack> bioAuth(@Path("version") String version, @Path("id") final String txnid, @Body final ReqBioAuth request);

	@Headers({"Accept: application/xml"})
	@POST("ReqPay/{version}/urn:txnid:{id}")
	public Call<Ack> paymentRequest(@Path("version") String version, @Path("id") final String txnid, @Body final ReqPay request);

	@Headers({"Accept: application/xml"})
	@POST("ReqChkTxn/{version}/urn:txnid:{id}")
	public Call<Ack> verificationRequest(@Path("version") String version, @Path("id") final String txnid, @Body final ReqChkTxn request);

	@Headers({"Accept: application/xml"})
	@POST("RespHbt/{version}/urn:txnid:{id}")
	public Call<Ack> heartbeatResponse(@Path("version") String version, @Path("id") final String txnid, @Body final RespHbt response);

	@Headers({"Accept: application/xml"})
	@POST("RespBalEnq/{version}/urn:txnid:{id}")
	public Call<Ack> balanceEnquiryResponse(@Path("version") String version, @Path("id") final String txnid, @Body final RespBalEnq response);

	@Headers({"Accept: application/xml"})
	@POST("RespPay/{version}/urn:txnid:{id}")
	public Call<Ack> paymentResponse(@Path("version") String version, @Path("id") final String txnid, @Body final RespPay response);

	@Headers({"Accept: application/xml"})
	@POST("RespChkTxn/{version}/urn:txnid:{id}")
	public Call<Ack> verificationResponse(@Path("version") String version, @Path("id") final String txnid, @Body final RespChkTxn response);

}