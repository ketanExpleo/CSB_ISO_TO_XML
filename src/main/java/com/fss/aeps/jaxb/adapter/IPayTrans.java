package com.fss.aeps.jaxb.adapter;

import com.fss.aeps.jaxb.HeadType;
import com.fss.aeps.jaxb.PayTrans;
import com.fss.aeps.jaxb.PayerType;

public interface IPayTrans {

	public HeadType getHead();

	public void setHead(HeadType value);

	public PayTrans getTxn();

	public void setTxn(PayTrans value);

	public PayerType getPayer();

	public void setPayer(PayerType value);

}