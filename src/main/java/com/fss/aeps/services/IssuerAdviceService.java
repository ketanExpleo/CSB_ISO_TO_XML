package com.fss.aeps.services;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fss.aeps.constants.ContextKey;
import com.fss.aeps.jaxb.Ack;
import com.fss.aeps.jaxb.HeadType;
import com.fss.aeps.jaxb.PayConstant;
import com.fss.aeps.jaxb.PayTrans;
import com.fss.aeps.jaxb.PayeeType;
import com.fss.aeps.jaxb.PayerType;
import com.fss.aeps.jaxb.ReqChkTxn;
import com.fss.aeps.jaxb.RespChkTxn;
import com.fss.aeps.jaxb.TxnSubType;
import com.fss.aeps.jpa.issuer.IssuerAdvice;
import com.fss.aeps.jpa.issuer.IssuerAdviceRef;
import com.fss.aeps.jpa.issuer.IssuerAdviceRefId;
import com.fss.aeps.repository.IssuerRepositories.IssuerAdviceRepository;
import com.fss.aeps.util.AadharAccount;
import com.fss.aeps.util.AadharAccountCollector;
import com.fss.aeps.util.DeviceTagMap;


@Service
public class IssuerAdviceService {

	@Autowired
	private IssuerAdviceRepository repository;

	public IssuerAdvice populateRequestData(IssuerAdvice advice, ReqChkTxn request) {
		final Ack reqAck = (Ack) request.context.get(ContextKey.REQUEST_ACK);

		final HeadType head = request.getHead();
		final PayTrans txn = request.getTxn();
		final PayerType payer = request.getPayer();

		advice.setMsgId(head.getMsgId());
		advice.setMsgTs(head.getTs());
		advice.setMsgVer(head.getVer());
		advice.setProdType(head.getProdType());
		advice.setOrgId(head.getOrgId());
		advice.setCallbackEndpointIp(head.getCallbackEndpointIP());

		advice.setTxnId(txn.getId());
		advice.setTxnType(txn.getType());
		advice.setTxnSubType(txn.getSubType());
		advice.setCustRef(txn.getCustRef());
		advice.setRefId(txn.getRefId());
		advice.setInitiationMode(txn.getInitiationMode());
		advice.setNote(txn.getNote());
		advice.setPurpose(txn.getPurpose());
		advice.setTxnTs(txn.getTs());
		advice.setDepositId(txn.getDepositId());
		advice.setRefUrl(txn.getRefUrl());

		advice.setOrgRrn(txn.getOrgRrn());
		advice.setOrgTxnDate(txn.getOrgTxnDate());
		advice.setOrgTxnId(txn.getOrgTxnId());

		advice.setPayerAddr(payer.getAddr());
		advice.setPayerCode(payer.getCode());
		advice.setPayerName(payer.getName());
		advice.setPayerSeqNum(payer.getSeqNum());
		advice.setPayerType(payer.getType());

		if(payer.getInfo() != null && payer.getInfo().getIdentity() != null) {
			advice.setPayerIdentityType(payer.getInfo().getIdentity().getType());
			advice.setPayerIdentityVerifiedName(payer.getInfo().getIdentity().getVerifiedName());
			advice.setPayerInfoIdentityId(payer.getInfo().getIdentity().getId());
		}

		if(payer.getInfo() != null && payer.getInfo().getRating() != null) {
			advice.setPayerRatingVerifiedAddress(payer.getInfo().getRating().getVerifiedAddress());
		}

		if(payer.getDevice() != null) advice.setPayerDeviceDetails(DeviceTagMap.toTlvString(payer.getDevice().getTag()));
		advice.setPayerAcAddrType(payer.getAc().getAddrType());
		AadharAccount payerAc = payer.getAc().getDetail().stream().collect(AadharAccountCollector.getInstance());
		advice.setPayerAcIin(payerAc.iin);
		advice.setPayerAcUidnumVid(payerAc.uidVid);
		advice.setPayerCurrency(payer.getAmount().getCurr());
		advice.setPayerAmount(payer.getAmount().getValue());

		final PayeeType payeeType = request.getPayee();
		if(payeeType != null) {
			advice.setPayeeSeqnum(payeeType.getSeqNum());
			advice.setPayeeType(payeeType.getType());
			advice.setPayeeAddr(payeeType.getAddr());
			advice.setPayeeCode(payeeType.getCode());
			advice.setPayeeName(payeeType.getName());

			if(payeeType.getAc()!= null) {
				AadharAccount payeeAc = payeeType.getAc().getDetail().stream().collect(AadharAccountCollector.getInstance());
				advice.setPayeeAcAddrtype(payeeType.getAc().getAddrType());
				advice.setPayeeAcIin(payeeAc.iin);
				advice.setPayeeAcUidnumVid(payeeAc.uidVid);
			}

			advice.setPayeeAmount(payeeType.getAmount().getValue());
			advice.setPayeeCurrency(payeeType.getAmount().getCurr());
		}

		if(reqAck != null) {
			String errorCdDtl = reqAck.getErrorMessages().stream().map(e -> e.getErrorCd()+"|"+e.getErrorDtl()).collect(Collectors.joining("||"));
			advice.setReqAckTs(reqAck.getTs());
			advice.setReqAckErr(reqAck.getErr());
			advice.setReqAckErrCdDtl(errorCdDtl);
		}
		return advice;
	}

	public IssuerAdvice registerRequest(IssuerAdvice advice, ReqChkTxn request) {
		populateRequestData(advice, request);
		return repository.save(advice);
	}

	public IssuerAdvice registerResponse(IssuerAdvice advice, RespChkTxn response) {
		final Ack respAck = (Ack) response.context.get(ContextKey.RESPONSE_ACK);
		advice.setDepositId(response.getTxn().getDepositId());
		advice.setRespMsgId(response.getHead().getMsgId());
		advice.setRespMsgTs(response.getHead().getTs());
		advice.setRespOrgId(response.getHead().getOrgId());
		advice.setRespResult(response.getResp().getResult());
		advice.setRespErrCode(response.getResp().getErrCode());
		response.getResp().getRef().forEach(ref -> {
			final IssuerAdviceRef acqRef = new IssuerAdviceRef();
			final IssuerAdviceRefId acqRefId = new IssuerAdviceRefId(advice.getMsgId(), ref.getType(), ref.getSeqNum());
			acqRef.setId(acqRefId);
			acqRef.setAddr(ref.getAddr());
			acqRef.setSettAmount(ref.getSettAmount());
			acqRef.setSettCurrency(ref.getSettCurrency());
			acqRef.setApprovalNo(ref.getApprovalNum());
			acqRef.setRegName(ref.getRegName());
			acqRef.setOrgAmount(ref.getOrgAmount());
			acqRef.setCode(ref.getCode());
			acqRef.setRespCode(ref.getRespCode());
			advice.getRefs().add(acqRef);
		});
		if(respAck != null) {
			String errorCdDtl = respAck.getErrorMessages().stream().map(e -> e.getErrorCd()+"|"+e.getErrorDtl()).collect(Collectors.joining("||"));
			advice.setRespAckTs(respAck.getTs());
			advice.setRespAckErr(respAck.getErr());
			advice.setRespAckErrCdDtl(errorCdDtl);
		}
		return repository.save(advice);
	}

	public IssuerAdvice findIssuerAdvice(final String msgId) {
		return repository.findById(msgId).orElse(null);
	}

	public IssuerAdvice findIssuerAdvice(final String txnId, TxnSubType type, String purpose) {
		return repository.findFirstByTxnIdAndTxnTypeAndPurpose(txnId, Enum.valueOf(PayConstant.class, type.name()), purpose);
	}

	public IssuerAdvice findIssuerAdvice(final String txnId, PayConstant type, String purpose) {
		return repository.findFirstByTxnIdAndTxnTypeAndPurpose(txnId, type, purpose);
	}

	public IssuerAdvice updateIssuerAdvice(IssuerAdvice advice) {
		return repository.save(advice);
	}

	public IssuerAdvice findByTxnIdAndDepositId(String id, String depositId) {
		return repository.findFirstByTxnIdAndDepositId(id, depositId);
	}

	public IssuerAdvice findFirstByTxnIdAndOrgRrn(String id, String orgRrn) {
		return repository.findFirstByTxnIdAndOrgRrn(id, orgRrn);
	}

	public IssuerAdvice findFirstByOrgTxnId(String id) {
		return repository.findFirstByOrgTxnId(id);
	}

}
