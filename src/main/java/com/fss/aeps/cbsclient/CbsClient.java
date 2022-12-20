package com.fss.aeps.cbsclient;

import com.fss.aeps.jaxb.ReqBalEnq;
import com.fss.aeps.jaxb.ReqPay;
import com.fss.aeps.jpa.acquirer.AcquirerTransaction;
import com.fss.aeps.jpa.issuer.IssuerTransaction;

import reactor.core.publisher.Mono;

public interface CbsClient {

	public Mono<CBSResponse> balance(final ReqBalEnq reqBalEnq);

	public Mono<CBSResponse> miniStatement(final ReqBalEnq reqBalEnq);

	public Mono<CBSResponse> debit(ReqPay request);

	public Mono<CBSResponse> debitReversal(final ReqPay request, IssuerTransaction original);

	public Mono<CBSResponse> accountingReversal(final AcquirerTransaction transaction);
    
	public Mono<CBSResponse> accountingCW(final ReqPay request);

}
