package com.fss.aeps.cbsclient;

import com.fss.aeps.jaxb.ReqBalEnq;
import com.fss.aeps.jaxb.ReqChkTxn;
import com.fss.aeps.jaxb.ReqPay;
import com.fss.aeps.jpa.issuer.IssuerTransaction;

import reactor.core.publisher.Mono;

public interface IssuerCbsClient {

	public Mono<CBSResponse> balance(final ReqBalEnq reqBalEnq);

	public Mono<CBSResponse> miniStatement(final ReqBalEnq reqBalEnq);

	public Mono<CBSResponse> debit(ReqPay request);

	public Mono<CBSResponse> creditFundTransfer(final ReqPay reqPay);

	public Mono<CBSResponse> deposit(final ReqPay reqPay);

	public Mono<CBSResponse> depositAdvice(ReqChkTxn reqChkTxn, IssuerTransaction original);

	public Mono<CBSResponse> depositRepeatAdvice(ReqChkTxn reqChkTxn, IssuerTransaction original);

	public Mono<CBSResponse> debitReversal(final ReqPay request, IssuerTransaction original);



}
