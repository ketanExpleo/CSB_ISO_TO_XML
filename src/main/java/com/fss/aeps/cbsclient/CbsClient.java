package com.fss.aeps.cbsclient;

import com.fss.aeps.jaxb.ReqBalEnq;
import com.fss.aeps.jaxb.ReqPay;
import com.fss.aeps.jaxb.RespPay;
import com.fss.aeps.jpa.acquirer.AcquirerTransaction;
import com.fss.aeps.jpa.issuer.IssuerTransaction;

import reactor.core.publisher.Mono;

public interface CbsClient {

	public Mono<CBSResponse> issuerBE(final ReqBalEnq reqBalEnq);

	public Mono<CBSResponse> issuerMS(final ReqBalEnq reqBalEnq);
	
	public Mono<CBSResponse> issuerDebit(ReqPay request);

	public Mono<CBSResponse> issuerDebitReversal(final ReqPay request, IssuerTransaction original);

	public Mono<CBSResponse> acqAccountingCW(final ReqPay request);

	public Mono<CBSResponse> acqAccountingCWReversal(final AcquirerTransaction transaction, final RespPay respPay);
}
