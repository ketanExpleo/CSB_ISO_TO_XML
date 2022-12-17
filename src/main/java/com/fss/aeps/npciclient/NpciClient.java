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

import reactor.core.publisher.Mono;

public interface NpciClient {

	public Mono<Ack> heartbeatResponse(RespHbt response);

	public Mono<Ack> paymentResponse(RespPay response);

	public Mono<Ack> balanceEnquiryResponse(RespBalEnq response);

	public Mono<Ack> verificationResponse(RespChkTxn response);

	public Mono<Ack> heartbeat(ReqHbt request);

	public Mono<Ack> payment(ReqPay request);

	public Mono<Ack> advice(ReqChkTxn request);

	public Mono<Ack> balanceEnquiry(ReqBalEnq request);

	public Mono<Ack> bioAuth(ReqBioAuth request);

}