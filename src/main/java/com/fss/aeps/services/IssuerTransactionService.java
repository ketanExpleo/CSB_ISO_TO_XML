package com.fss.aeps.services;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fss.aeps.constants.ContextKey;
import com.fss.aeps.constants.Purpose;
import com.fss.aeps.jaxb.Ack;
import com.fss.aeps.jaxb.CredsType.Cred;
import com.fss.aeps.jaxb.HeadType;
import com.fss.aeps.jaxb.PayConstant;
import com.fss.aeps.jaxb.PayTrans;
import com.fss.aeps.jaxb.PayeeType;
import com.fss.aeps.jaxb.PayerType;
import com.fss.aeps.jaxb.ReqPay;
import com.fss.aeps.jaxb.RespPay;
import com.fss.aeps.jaxb.RiskScoresType.Score;
import com.fss.aeps.jaxb.TxnSubType;
import com.fss.aeps.jpa.issuer.IssuerTransaction;
import com.fss.aeps.jpa.issuer.IssuerTransactionPayee;
import com.fss.aeps.jpa.issuer.IssuerTransactionPayeeId;
import com.fss.aeps.jpa.issuer.IssuerTransactionRef;
import com.fss.aeps.jpa.issuer.IssuerTransactionRefId;
import com.fss.aeps.repository.IssuerRepositories.IssuerTransactionRepository;
import com.fss.aeps.util.AadharAccount;
import com.fss.aeps.util.AadharAccountCollector;
import com.fss.aeps.util.DeviceTagMap;


@Service
public class IssuerTransactionService {

	private static final Logger logger = LoggerFactory.getLogger(IssuerTransactionService.class);

	@Autowired
	private IssuerTransactionRepository repository;

	public IssuerTransaction populateRequestData(IssuerTransaction transaction, ReqPay request) {
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

		if(txn.getRiskScores() != null && txn.getRiskScores().getScore().size() > 0) {
			final Score score =  txn.getRiskScores().getScore().get(0);
			transaction.setRiskProvider(score.getProvider());
			transaction.setRiskType(score.getType());
			transaction.setRiskValue(score.getValue());
		}
		transaction.setPayerAddr(payer.getAddr());
		transaction.setPayerCode(payer.getCode());
		transaction.setPayerName(payer.getName());
		transaction.setPayerSeqNum(payer.getSeqNum());
		transaction.setPayerType(payer.getType());

		if(payer.getInfo() != null && payer.getInfo().getIdentity() != null) {
			transaction.setPayerIdentityType(payer.getInfo().getIdentity().getType());
			transaction.setPayerIdentityVerifiedName(payer.getInfo().getIdentity().getVerifiedName());
			transaction.setPayerInfoIdentityId(payer.getInfo().getIdentity().getId());
		}

		if(payer.getInfo() != null && payer.getInfo().getRating() != null) {
			transaction.setPayerRatingVerifiedAddress(payer.getInfo().getRating().getVerifiedAddress());
		}

		if(payer.getDevice() != null) transaction.setPayerDeviceDetails(DeviceTagMap.toTlvString(payer.getDevice().getTag()));
		transaction.setPayerAcAddrType(payer.getAc().getAddrType());
		AadharAccount payerAc = payer.getAc().getDetail().stream().collect(AadharAccountCollector.getInstance());
		transaction.setPayerAcIin(payerAc.iin);
		transaction.setPayerAcUidnumVid(payerAc.uidVid);

		if(payer.getCreds() != null && payer.getCreds().getCred().size() > 0) {
			Cred cred = payer.getCreds().getCred().get(0);
			transaction.setPayerCredType(cred.getType());
			transaction.setPayerCredSubType(cred.getSubType());
			if(cred.getData() != null) transaction.setPayerCredData(cred.getData().getValue());
		}

		transaction.setPayerCurrency(payer.getAmount().getCurr());
		transaction.setPayerAmount(payer.getAmount().getValue());

		if(request.getPayees() != null) {
			final List<PayeeType> payees = request.getPayees().getPayee();
			for (PayeeType payeeType : payees) {
				IssuerTransactionPayee payee = new IssuerTransactionPayee();
				IssuerTransactionPayeeId payeeId = new IssuerTransactionPayeeId();
				payee.setId(payeeId);
				payeeId.setMsgId(head.getMsgId());
				payeeId.setSeqnum(payeeType.getSeqNum());
				payee.setAddr(payeeType.getAddr());
				payee.setCode(payeeType.getCode());
				payee.setType(payeeType.getType());

				if(payeeType.getAc()!= null) {
					payee.setAcAddrtype(payeeType.getAc().getAddrType());
					AadharAccount payeeAc = payeeType.getAc().getDetail().stream().collect(AadharAccountCollector.getInstance());
					payee.setAcIin(payeeAc.iin);
					payee.setAcUidnumVid(payeeAc.uidVid);
				}
				payee.setAmount(payeeType.getAmount().getValue());
				payee.setCurrency(payeeType.getAmount().getCurr());
				if(payeeType.getCreds() != null && payeeType.getCreds().getCred().size() > 0) {
					Cred cred = payeeType.getCreds().getCred().get(0);
					payee.setCredType(cred.getType());
					payee.setCredSubType(cred.getSubType());
					if(cred.getData() != null) payee.setCredData(cred.getData().getValue());
				} else {
					logger.info("payee creds not found.");
				}
				transaction.getPayees().add(payee);
			}
		}

		if(reqAck != null) {
			String errorCdDtl = reqAck.getErrorMessages().stream().map(e -> e.getErrorCd()+"|"+e.getErrorDtl()).collect(Collectors.joining("||"));
			transaction.setReqAckTs(reqAck.getTs());
			transaction.setReqAckErr(reqAck.getErr());
			transaction.setReqAckErrCdDtl(errorCdDtl);
		}
		return transaction;
	}

	public IssuerTransaction registerRequest(IssuerTransaction transaction, ReqPay request) {
		populateRequestData(transaction, request);
		return repository.save(transaction);
	}

	public IssuerTransaction registerResponse(IssuerTransaction transaction, RespPay response) {
		final Ack respAck = (Ack) response.context.get(ContextKey.RESPONSE_ACK);
		transaction.setDepositId(response.getTxn().getDepositId());
		transaction.setRespMsgId(response.getHead().getMsgId());
		transaction.setRespMsgTs(response.getHead().getTs());
		transaction.setRespOrgId(response.getHead().getOrgId());
		transaction.setRespResult(response.getResp().getResult());
		transaction.setRespErrCode(response.getResp().getErrCode());
		transaction.setRespAuthCode(response.getResp().getAuthCode());
		response.getResp().getRef().forEach(ref -> {
			final IssuerTransactionRef acqRef = new IssuerTransactionRef();
			final IssuerTransactionRefId acqRefId = new IssuerTransactionRefId(transaction.getMsgId(), ref.getType(), ref.getSeqNum());
			acqRef.setId(acqRefId);
			acqRef.setAddr(ref.getAddr());
			acqRef.setSettAmount(ref.getSettAmount());
			acqRef.setSettCurrency(ref.getSettCurrency());
			acqRef.setApprovalNo(ref.getApprovalNum());
			acqRef.setRegName(ref.getRegName());
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

	public IssuerTransaction findIssuerTransaction(final String msgId) {
		return repository.findById(msgId).orElse(null);
	}

	public IssuerTransaction findIssuerTransaction(final String txnId, TxnSubType type, String purpose) {
		return repository.findFirstByTxnIdAndTxnTypeAndPurpose(txnId, Enum.valueOf(PayConstant.class, type.name()), purpose);
	}

	public IssuerTransaction findIssuerTransaction(final String txnId, PayConstant type, String purpose) {
		return repository.findFirstByTxnIdAndTxnTypeAndPurpose(txnId, type, purpose);
	}

	@Transactional(readOnly = true)
	public IssuerTransaction findDepositTransaction(final String txnId) {
		IssuerTransaction transaction = repository.findFirstByTxnIdAndTxnTypeAndPurpose(txnId, PayConstant.CREDIT, Purpose.DEPOSIT);
		if(transaction != null) transaction.getPayees().forEach(payee -> payee.getCode());
		return transaction;
	}

	public IssuerTransaction updateIssuerTransaction(IssuerTransaction issuerTransaction) {
		return repository.save(issuerTransaction);
	}

}
