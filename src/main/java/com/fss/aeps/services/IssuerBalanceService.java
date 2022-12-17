package com.fss.aeps.services;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fss.aeps.constants.ContextKey;
import com.fss.aeps.jaxb.Ack;
import com.fss.aeps.jaxb.HeadType;
import com.fss.aeps.jaxb.PayTrans;
import com.fss.aeps.jaxb.PayerType;
import com.fss.aeps.jaxb.ReqBalEnq;
import com.fss.aeps.jaxb.RespBalEnq;
import com.fss.aeps.jpa.issuer.IssuerBalanceEnquiry;
import com.fss.aeps.repository.IssuerRepositories.IssuerBalanceRepository;
import com.fss.aeps.util.AadharAccount;
import com.fss.aeps.util.AadharAccountCollector;
import com.fss.aeps.util.DeviceTagMap;

@Service
public class IssuerBalanceService {

	@Autowired
	private IssuerBalanceRepository repositiry;

	public IssuerBalanceEnquiry findIssuerBalanceEnquiry(final String msgId) {
		return repositiry.findById(msgId).orElse(null);
	}

	public IssuerBalanceEnquiry updateIssuerBalanceEnquiry(IssuerBalanceEnquiry IssuerBalanceEnquiry) {
		return repositiry.save(IssuerBalanceEnquiry);
	}


	public IssuerBalanceEnquiry registerRequestResponse(IssuerBalanceEnquiry transaction, ReqBalEnq request, RespBalEnq response) {
		final Ack reqAck = (Ack) request.context.get(ContextKey.REQUEST_ACK);
		final Ack respAck = (Ack) response.context.get(ContextKey.RESPONSE_ACK);

		final HeadType head = request.getHead();
		final PayTrans txn = request.getTxn();
		final PayerType payer = request.getPayer();

		transaction.setMsgId(head.getMsgId());
		transaction.setMsgTs(head.getTs());
		transaction.setProdType(head.getProdType());
		transaction.setOrgId(head.getOrgId());
		transaction.setMsgVer(head.getVer());
		transaction.setCallbackEndpointIp(head.getCallbackEndpointIP());

		transaction.setTxnId(txn.getId());
		transaction.setTxnType(txn.getType());
		transaction.setCustRef(txn.getCustRef());
		transaction.setRefId(txn.getRefId());
		transaction.setRefUrl(txn.getRefUrl());
		transaction.setNote(txn.getNote());
		transaction.setTxnTs(txn.getTs());

		transaction.setPayerAddr(payer.getAddr());
		transaction.setPayerName(payer.getName());
		transaction.setPayerSeqNum(payer.getSeqNum());
		transaction.setPayerCode(payer.getCode());
		transaction.setPayerType(payer.getType());

		if(payer.getDevice() != null) transaction.setPayerDeviceDetails(DeviceTagMap.toTlvString(payer.getDevice().getTag()));
		if(payer.getAc() != null) {
			transaction.setPayerAcAddrType(payer.getAc().getAddrType());
			AadharAccount ac = payer.getAc().getDetail().stream().collect(AadharAccountCollector.getInstance());
			transaction.setPayerAcIin(ac.iin);
			transaction.setPayerAcUidnumVid(ac.uidVid);
		}

		if(payer.getCreds() != null) {
			payer.getCreds().getCred().stream().forEach(cred -> {
				transaction.setPayerCredType(cred.getType());
				transaction.setPayerCredSubType(cred.getSubType());
				if(cred.getData() != null) transaction.setPayerCredData(cred.getData().getValue());
			});
		}

		if(reqAck != null) {
			String errorCdDtl = reqAck.getErrorMessages().stream().map(e -> e.getErrorCd()+"|"+e.getErrorDtl()).collect(Collectors.joining("||"));
			transaction.setReqAckTs(reqAck.getTs());
			transaction.setReqAckErr(reqAck.getErr());
			transaction.setReqAckErrCdDtl(errorCdDtl);
		}

		if(response != null) {
			transaction.setRespMsgId(response.getHead().getMsgId());
			transaction.setRespMsgTs(response.getHead().getTs());
			transaction.setRespOrgId(response.getHead().getOrgId());
			transaction.setRespResult(response.getResp().getResult());
			transaction.setRespErrCode(response.getResp().getErrCode());
			transaction.setRespAuthCode(response.getResp().getAuthCode());
		}

		if(respAck != null) {
			String errorCdDtl = respAck.getErrorMessages().stream().map(e -> e.getErrorCd()+"|"+e.getErrorDtl()).collect(Collectors.joining("||"));
			transaction.setRespAckTs(respAck.getTs());
			transaction.setRespAckErr(respAck.getErr());
			transaction.setRespAckErrCdDtl(errorCdDtl);
		}
		return repositiry.save(transaction);
	}
}
