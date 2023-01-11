package com.fss.aeps.services;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fss.aeps.acquirer.AcquirerChannel;
import com.fss.aeps.constants.ContextKey;
import com.fss.aeps.constants.Purpose;
import com.fss.aeps.jaxb.Ack;
import com.fss.aeps.jaxb.HeadType;
import com.fss.aeps.jaxb.PayConstant;
import com.fss.aeps.jaxb.PayTrans;
import com.fss.aeps.jaxb.PayeeType;
import com.fss.aeps.jaxb.PayerType;
import com.fss.aeps.jaxb.RefType;
import com.fss.aeps.jaxb.ReqPay;
import com.fss.aeps.jaxb.RespPay;
import com.fss.aeps.jpa.acquirer.AcquirerTransaction;
import com.fss.aeps.jpa.acquirer.AcquirerTransactionPayee;
import com.fss.aeps.jpa.acquirer.AcquirerTransactionPayeeId;
import com.fss.aeps.jpa.acquirer.AcquirerTransactionRef;
import com.fss.aeps.jpa.acquirer.AcquirerTransactionRefId;
import com.fss.aeps.repository.AcquirerRepositories.AcquirerTransactionRepository;
import com.fss.aeps.util.AadharAccount;
import com.fss.aeps.util.AadharAccountCollector;
import com.fss.aeps.util.DeviceTagMap;

@Service
public class AcquirerTransactionService {

	private static final Logger logger = LoggerFactory.getLogger(AcquirerTransactionService.class);
	
	@Autowired
	private AcquirerTransactionRepository repository;

	public void populateRequestData(AcquirerTransaction transaction, ReqPay request) {
		logger.info("Inside [AcquirerTransactionService : populateRequestData] :: {}", request);
		final HeadType head = request.getHead();
		final PayTrans txn = request.getTxn();
		final PayerType payer = request.getPayer();

		transaction.setMsgId(head.getMsgId());
		transaction.setMsgTs(head.getTs());
		transaction.setProdType(head.getProdType());
		transaction.setOrgId(head.getOrgId());
		transaction.setMsgVer(head.getVer());

		transaction.setTxnId(txn.getId());
		transaction.setTxnType(txn.getType());
		transaction.setTxnSubType(txn.getSubType());
		transaction.setPurpose(txn.getPurpose());
		transaction.setCustRef(txn.getCustRef());
		transaction.setRefId(txn.getRefId());
		transaction.setRefUrl(txn.getRefUrl());
		transaction.setNote(txn.getNote());
		transaction.setTxnTs(txn.getTs());
		transaction.setInitiationMode(txn.getInitiationMode());
		transaction.setDepositId(txn.getDepositId());

		transaction.setPayerAddr(payer.getAddr());
		transaction.setPayerName(payer.getName());
		transaction.setPayerSeqNum(payer.getSeqNum());
		transaction.setPayerCode(payer.getCode());
		transaction.setPayerType(payer.getType());

		if(payer.getInfo() != null) {
			if(payer.getInfo().getIdentity() != null) {
				transaction.setPayerIdentityType(payer.getInfo().getIdentity().getType());
				transaction.setPayerIdentityVerifiedName(payer.getInfo().getIdentity().getVerifiedName());
				transaction.setPayerInfoIdentityId(payer.getInfo().getIdentity().getId());
			}
			if(payer.getInfo().getRating() != null) {
				transaction.setPayerRatingVerifiedAddress(payer.getInfo().getRating().getVerifiedAddress());
			}
		}
		if(payer.getDevice() != null) transaction.setPayerDeviceDetails(DeviceTagMap.toTlvString(payer.getDevice().getTag()));
		logger.info("@@@ payer.getAc().getDetail() :: {}", payer.getAc().getDetail());
		if(payer.getAc() != null) {
			transaction.setPayerAcAddrType(payer.getAc().getAddrType());
			logger.info("@@@@ AadharAccountCollector.getInstance() :: {}", AadharAccountCollector.getInstance());
			AadharAccount ac = (AadharAccount) payer.getAc().getDetail().stream().collect(AadharAccountCollector.getInstance());
			logger.info("@@@ ac.iin :: {}",ac.iin);
			logger.info("@@@ ac.uidVid :: {}",ac.uidVid);
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
		if(payer.getAmount() != null) {
			transaction.setPayerCurrency(payer.getAmount().getCurr());
			transaction.setPayerAmount(payer.getAmount().getValue());
		}

		final AcquirerChannel channel = (AcquirerChannel) request.context.get(ContextKey.CHANNEL);
		final String agentDetails = (String) request.context.get(ContextKey.AGENT_DETAILS);

		transaction.setChannel(channel);
		transaction.setAgentDetails(agentDetails);

		if(request.getPayees() != null) {
			final List<PayeeType> payees = request.getPayees().getPayee();
			for (final PayeeType payeeType : payees) {
				final AcquirerTransactionPayee payee = new AcquirerTransactionPayee();
				final AcquirerTransactionPayeeId payeeId = new AcquirerTransactionPayeeId();
				payee.setId(payeeId);
				payeeId.setMsgId(head.getMsgId());
				payeeId.setSeqnum(payeeType.getSeqNum());
				payee.setAddr(payeeType.getAddr());
				payee.setType(payeeType.getType());
				payee.setCode(payeeType.getCode());
				if(payeeType.getAc() != null) {
					payee.setAcAddrtype(payeeType.getAc().getAddrType());
					AadharAccount ac =  payer.getAc().getDetail().stream().collect(AadharAccountCollector.getInstance());
					payee.setAcIin(ac.iin);
					payee.setAcUidnumVid(ac.uidVid);
				}

				if(payeeType.getAmount() != null) {
					payee.setAmount(payeeType.getAmount().getValue());
					payee.setCurrency(payeeType.getAmount().getCurr());
				}
				if(payeeType.getCreds() != null) {
					payeeType.getCreds().getCred().forEach(cred -> {
						payee.setCredType(cred.getType());
						payee.setCredSubType(cred.getSubType());
						if(cred.getData() != null) payee.setCredData(cred.getData().getValue());
					});
				}
				transaction.getPayees().add(payee);
			}
		}

	}

	public AcquirerTransaction registerTransactionRequest(AcquirerTransaction transaction, ReqPay request) {
		logger.info("[AcquirerTransactionService: registerTransactionRequest] :: {}", request.toString());
		populateRequestData(transaction, request);
		return repository.save(transaction);
	}

	public AcquirerTransaction registerTransactionResponse(AcquirerTransaction transaction, ReqPay request, RespPay response) {
		final Ack reqAck = (Ack) request.context.get(ContextKey.REQUEST_ACK);
		final Ack respAck = (Ack) response.context.get(ContextKey.RESPONSE_ACK);
		final Boolean isStaticResponse = ContextKey.toBoolean(response.context.get(ContextKey.IS_STATIC_RESPONSE));
		transaction.setDepositId(response.getTxn().getDepositId());
		transaction.setRespMsgId(response.getHead().getMsgId());
		transaction.setRespMsgTs(response.getHead().getTs());
		transaction.setRespOrgId(response.getHead().getOrgId());
		transaction.setRespResult(response.getResp().getResult());
		transaction.setRespErrCode(response.getResp().getErrCode());
		transaction.setRespUidaiError(response.getResp().getUidaiError());
		transaction.setRespAuthCode(response.getResp().getAuthCode());
		transaction.setRespTime(new Date());
		transaction.setRespReceived(isStaticResponse ? "N" : "Y");
		response.getResp().getRef().forEach(ref -> {
			final AcquirerTransactionRef acqRef = new AcquirerTransactionRef();
			final AcquirerTransactionRefId acqRefId = new AcquirerTransactionRefId(transaction.getMsgId(), ref.getSeqNum(), ref.getType());
			acqRef.setId(acqRefId);
			acqRef.setAddr(ref.getAddr());
			acqRef.setSettAmount(ref.getSettAmount());
			acqRef.setSettCurrency(ref.getSettCurrency());
			acqRef.setApprovalNo(ref.getApprovalNum());
			acqRef.setRegName(ref.getRegName());
			acqRef.setOrgAmount(ref.getOrgAmount());
			acqRef.setCode(ref.getCode());
			acqRef.setRespCode(ref.getRespCode());
			final RefType refType = List.of(Purpose.CASH_WITHDRAWAL , Purpose.PURCHASE).contains(transaction.getPurpose()) ? RefType.PAYER : RefType.PAYEE;
			if(!"UIDAI_ERROR".equalsIgnoreCase(response.getResp().getErrCode())) { //response.getResp().getResult() != ResultType.SUCCESS &&
				if(ref.getType() == refType) {
					transaction.setRefRespCode(ref.getRespCode());
				}
			}
			transaction.getRefs().add(acqRef);
		});

		if(reqAck != null) {
			String errorCdDtl = reqAck.getErrorMessages().stream().map(e -> e.getErrorCd()+"|"+e.getErrorDtl()).collect(Collectors.joining("||"));
			transaction.setReqAckTs(reqAck.getTs());
			transaction.setReqAckErr(reqAck.getErr());
			transaction.setReqAckErrCdDtl(errorCdDtl);
		}

		if(respAck != null) {
			String errorCdDtl = respAck.getErrorMessages().stream().map(e -> e.getErrorCd()+"|"+e.getErrorDtl()).collect(Collectors.joining("||"));
			transaction.setRespAckTs(respAck.getTs());
			transaction.setRespAckErr(respAck.getErr());
			transaction.setRespAckErrCdDtl(errorCdDtl);
		}
		return repository.save(transaction);
	}

	public AcquirerTransaction findAcquirerTransaction(final String msgId) {
		return repository.findById(msgId).orElse(null);
	}

	public AcquirerTransaction findAcquirerTransactiontByTxnId(final String custRef, String uidVid) {
		return repository.findFirstByCustRefAndPayerAcUidnumVid(custRef, uidVid);
	}

	public AcquirerTransaction updateAcquirerTransaction(AcquirerTransaction acquirerTransaction) {
		return repository.save(acquirerTransaction);
	}

	public AcquirerTransaction findAcquirerTransaction(final String custRef, PayConstant txnType, String purpose) {
		return repository.findFirstByCustRefAndTxnTypeAndPurpose(custRef, txnType, purpose);
	}

	public AcquirerTransaction findFirstByCustRef(final String custRef) {
		return repository.findFirstByCustRefAndTxnType(custRef, PayConstant.PAY);
	}

}
