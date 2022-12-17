package com.fss.aeps.services;

import java.util.Date;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fss.aeps.constants.ContextKey;
import com.fss.aeps.jaxb.Ack;
import com.fss.aeps.jaxb.HeadType;
import com.fss.aeps.jaxb.PayTrans;
import com.fss.aeps.jaxb.PayerType;
import com.fss.aeps.jaxb.ReqChkTxn;
import com.fss.aeps.jaxb.RespChkTxn;
import com.fss.aeps.jpa.acquirer.AcquirerAdvice;
import com.fss.aeps.repository.AcquirerRepositories.AcquirerAdviceRepository;
import com.fss.aeps.util.AadharAccount;
import com.fss.aeps.util.AadharAccountCollector;
import com.fss.aeps.util.DeviceTagMap;

@Service
public class AcquirerAdviceService {

	@Autowired
	private AcquirerAdviceRepository repositiry;

	public AcquirerAdvice populateRequestData(AcquirerAdvice advice, ReqChkTxn request) {
		final HeadType head = request.getHead();
		final PayTrans txn = request.getTxn();
		final PayerType payer = request.getPayer();

		advice.setMsgId(head.getMsgId());
		advice.setMsgTs(head.getTs());
		advice.setProdType(head.getProdType());
		advice.setOrgId(head.getOrgId());
		advice.setMsgVer(head.getVer());

		advice.setTxnId(txn.getId());
		advice.setTxnType(txn.getType());
		advice.setTxnSubType(txn.getSubType());
		advice.setPurpose(txn.getPurpose());
		advice.setCustRef(txn.getCustRef());
		advice.setRefId(txn.getRefId());
		advice.setRefUrl(txn.getRefUrl());
		advice.setNote(txn.getNote());
		advice.setTxnTs(txn.getTs());
		advice.setInitiationMode(txn.getInitiationMode());
		advice.setDepositId(txn.getDepositId());

		advice.setOrgRrn(txn.getOrgRrn());
		advice.setOrgTxnId(txn.getOrgTxnId());
		advice.setOrgTxnDate(txn.getOrgTxnDate());
		advice.setOrgRespCode(txn.getOrgRespCode());

		advice.setPayerAddr(payer.getAddr());
		advice.setPayerName(payer.getName());
		advice.setPayerSeqNum(payer.getSeqNum());
		advice.setPayerCode(payer.getCode());
		advice.setPayerType(payer.getType());

		if(payer.getInfo() != null) {
			if(payer.getInfo().getIdentity() != null) {
				advice.setPayerIdentityType(payer.getInfo().getIdentity().getType());
				advice.setPayerIdentityVerifiedName(payer.getInfo().getIdentity().getVerifiedName());
				advice.setPayerInfoIdentityId(payer.getInfo().getIdentity().getId());
			}
			if(payer.getInfo().getRating() != null) {
				advice.setPayerRatingVerifiedAddress(payer.getInfo().getRating().getVerifiedAddress());
			}
		}
		if(payer.getDevice() != null) advice.setPayerDeviceDetails(DeviceTagMap.toTlvString(payer.getDevice().getTag()));
		if(payer.getAc() != null) {
			advice.setPayerAcAddrType(payer.getAc().getAddrType());
			AadharAccount ac = payer.getAc().getDetail().stream().collect(AadharAccountCollector.getInstance());
			advice.setPayerAcIin(ac.iin);
			advice.setPayerAcUidnumVid(ac.uidVid);
		}

		if(payer.getAmount() != null) {
			advice.setPayerCurrency(payer.getAmount().getCurr());
			advice.setPayerAmount(payer.getAmount().getValue());
		}
		return advice;

	}

	public AcquirerAdvice registerTransactionRequest(AcquirerAdvice advice, ReqChkTxn request) {
		populateRequestData(advice, request);
		return repositiry.save(advice);
	}

	public AcquirerAdvice registerTransactionResponse(AcquirerAdvice advice, RespChkTxn response) {
		if(response != null) {
			final Ack respAck = (Ack) response.context.get(ContextKey.RESPONSE_ACK);
			advice.setRespMsgId(response.getHead().getMsgId());
			advice.setRespMsgTs(response.getHead().getTs());
			advice.setRespOrgId(response.getHead().getOrgId());
			advice.setRespResult(response.getResp().getResult());
			advice.setRespErrCode(response.getResp().getErrCode());
			advice.setRespTxnTs(response.getTxn().getTs());
			advice.setRespTime(new Date());
			if(respAck != null) {
				String errorCdDtl = respAck.getErrorMessages().stream().map(e -> e.getErrorCd()+"|"+e.getErrorDtl()).collect(Collectors.joining("||"));
				advice.setRespAckTs(respAck.getTs());
				advice.setRespAckErr(respAck.getErr());
				advice.setRespAckErrCdDtl(errorCdDtl);
			}
		}
		return repositiry.save(advice);
	}
}
