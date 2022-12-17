package com.fss.aeps.cbsclient;

import com.fss.aeps.jaxb.ReqBioAuth;
import com.fss.aeps.jaxb.ReqPay;
import com.fss.aeps.jaxb.RespBioAuth;
import com.fss.aeps.jpa.acquirer.AcquirerTransaction;

import reactor.core.publisher.Mono;

public interface AcquirerCbsClient {

	public Mono<CBSResponse> debitFT(ReqPay request, RespBioAuth respBioAuth);

	public Mono<CBSResponse> accountingCW(ReqPay request);

	public Mono<CBSResponse> accountingCW(AcquirerTransaction transaction);

	public Mono<CBSResponse> accountingPT(ReqPay request);

	public Mono<CBSResponse> accountingCD(AcquirerTransaction transaction);

	public Mono<CBSResponse> debitCreditOnus(ReqBioAuth request, RespBioAuth response);

	public Mono<CBSResponse> debitCreditOnusReversal(ReqBioAuth request, RespBioAuth response);

	public Mono<CBSResponse> accountingReversal(AcquirerTransaction transaction);

	public Mono<CBSResponse> debitFTReversal(ReqPay request, RespBioAuth respBioAuth);


}
