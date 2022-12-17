package com.fss.aeps.services;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fss.aeps.constants.ContextKey;
import com.fss.aeps.jaxb.Ack;
import com.fss.aeps.jaxb.HeadType;
import com.fss.aeps.jaxb.PayConstant;
import com.fss.aeps.jaxb.PayTrans;
import com.fss.aeps.jaxb.PayerType;
import com.fss.aeps.jaxb.ReqPay;
import com.fss.aeps.jaxb.RespPay;
import com.fss.aeps.jaxb.TxnSubType;
import com.fss.aeps.jpa.issuer.IssuerReversal;
import com.fss.aeps.jpa.issuer.IssuerReversalRef;
import com.fss.aeps.jpa.issuer.IssuerReversalRefId;
import com.fss.aeps.repository.IssuerRepositories.IssuerReversalRepository;
import com.fss.aeps.util.AadharAccount;
import com.fss.aeps.util.AadharAccountCollector;

@Service
public class IssuerReversalService {

	@Autowired
	private IssuerReversalRepository repository;

	public IssuerReversal populateRequestData(IssuerReversal transaction, ReqPay request) {
		final Ack reqAck = (Ack) request.context.get(ContextKey.REQUEST_ACK);

		final HeadType head = request.getHead();
		final PayTrans txn = request.getTxn();
		final PayerType payer = request.getPayer();

		transaction.setMsgId(head.getMsgId());
		transaction.setMsgTs(head.getTs());
		transaction.setMsgVer(head.getVer());
		transaction.setProdType(head.getProdType());
		transaction.setOrgId(head.getOrgId());
		transaction.setCallbackEndpointIp(head.getCallbackEndpointIP());

		transaction.setTxnId(txn.getId());
		transaction.setTxnType(txn.getType());
		transaction.setTxnSubType(txn.getSubType());
		transaction.setCustRef(txn.getCustRef());
		transaction.setRefId(txn.getRefId());
		transaction.setInitiationMode(txn.getInitiationMode());
		transaction.setNote(txn.getNote());
		transaction.setPurpose(txn.getPurpose());
		transaction.setTxnTs(txn.getTs());
		transaction.setDepositId(txn.getDepositId());
		transaction.setRefUrl(txn.getRefUrl());

		transaction.setPayerAddr(payer.getAddr());
		transaction.setPayerCode(payer.getCode());
		transaction.setPayerName(payer.getName());
		transaction.setPayerSeqNum(payer.getSeqNum());
		transaction.setPayerType(payer.getType());

		if(payer.getInfo() != null && payer.getInfo().getIdentity() != null) {
			transaction.setPayerIdentityType(payer.getInfo().getIdentity().getType());
			transaction.setPayerIdentityVerifiedName(payer.getInfo().getIdentity().getVerifiedName());
		}

		if(payer.getInfo() != null && payer.getInfo().getRating() != null) {
			transaction.setPayerRatingVerifiedAddress(payer.getInfo().getRating().getVerifiedAddress());
		}

		transaction.setPayerAcAddrType(payer.getAc().getAddrType());
		AadharAccount payerAc = payer.getAc().getDetail().stream().collect(AadharAccountCollector.getInstance());
		transaction.setPayerAcIin(payerAc.iin);
		transaction.setPayerAcUidnumVid(payerAc.uidVid);
		transaction.setPayerCurrency(payer.getAmount().getCurr());
		transaction.setPayerAmount(payer.getAmount().getValue());

		if(reqAck != null) {
			String errorCdDtl = reqAck.getErrorMessages().stream().map(e -> e.getErrorCd()+"|"+e.getErrorDtl()).collect(Collectors.joining("||"));
			transaction.setReqAckTs(reqAck.getTs());
			transaction.setReqAckErr(reqAck.getErr());
			transaction.setReqAckErrCdDtl(errorCdDtl);
		}
		return transaction;
	}

	public IssuerReversal registerRequest(IssuerReversal transaction, ReqPay request) {
		populateRequestData(transaction, request);
		return repository.save(transaction);
	}

	public IssuerReversal registerResponse(IssuerReversal transaction, RespPay response) {
		final Ack respAck = (Ack) response.context.get(ContextKey.RESPONSE_ACK);
		transaction.setDepositId(response.getTxn().getDepositId());
		transaction.setRespMsgId(response.getHead().getMsgId());
		transaction.setRespMsgTs(response.getHead().getTs());
		transaction.setRespOrgId(response.getHead().getOrgId());
		transaction.setRespResult(response.getResp().getResult());
		transaction.setRespErrCode(response.getResp().getErrCode());
		response.getResp().getRef().forEach(ref -> {
			final IssuerReversalRef acqRef = new IssuerReversalRef();
			final IssuerReversalRefId acqRefId = new IssuerReversalRefId(transaction.getMsgId(), ref.getType(), ref.getSeqNum());
			acqRef.setId(acqRefId);
			acqRef.setAddr(ref.getAddr());
			acqRef.setSettAmount(ref.getSettAmount());
			acqRef.setSettCurrency(ref.getSettCurrency());
			acqRef.setOrgAmount(ref.getOrgAmount());
			acqRef.setCode(ref.getCode());
			acqRef.setRespCode(ref.getRespCode());
			transaction.getRefs().add(acqRef);
		});
		if(respAck != null) {
			String errorCdDtl = respAck.getErrorMessages().stream().map(e -> e.getErrorCd()+"|"+e.getErrorDtl()).collect(Collectors.joining("||"));
			transaction.setRespAckTs(respAck.getTs());
			transaction.setRespAckErr(respAck.getErr());
			transaction.setRespAckErrCdDtl(errorCdDtl);
		}
		return repository.save(transaction);
	}

	public IssuerReversal registerResponse(RespPay response) {
		IssuerReversal transaction = repository.findById(response.getResp().getReqMsgId())
		.orElseGet(() -> repository.findFirstByTxnIdAndTxnTypeAndTxnSubType(response.getTxn().getId(), response.getTxn().getType(), response.getTxn().getSubType()));
		if(transaction != null) {
			return registerResponse(transaction, response);
		}
		else {
			throw new RuntimeException("Transaction not found for msgId : "+response.getResp().getReqMsgId()+
					" or txnId : "+response.getTxn().getId());
		}
	}

	public IssuerReversal findIssuerReversal(final String msgId) {
		return repository.findById(msgId).orElse(null);
	}

	public IssuerReversal findIssuerReversal(final String txnId, TxnSubType type, String purpose) {
		return repository.findFirstByTxnIdAndTxnTypeAndPurpose(txnId, Enum.valueOf(PayConstant.class, type.name()), purpose).orElse(null);
	}

	public IssuerReversal updateIssuerReversal(IssuerReversal issuerTransaction) {
		return repository.save(issuerTransaction);
	}
}
