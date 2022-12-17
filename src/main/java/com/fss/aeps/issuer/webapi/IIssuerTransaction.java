package com.fss.aeps.issuer.webapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.fss.aeps.util.Mapper;

public abstract class IIssuerTransaction<R, S> implements Runnable {

	@Autowired
	@Qualifier("cbsToNpciResponseMapper")
	protected Mapper cbsToNpciResponseMapper;

	protected final R request;
	protected final S response;

	public IIssuerTransaction(R request, S response) {
		this.request = request;
		this.response = response;
	}
	public R getRequest() {
		return request;
	}
	public S getResponse() {
		return response;
	}

}
