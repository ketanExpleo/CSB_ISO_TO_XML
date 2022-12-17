package com.fss.aeps.services;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fss.aeps.acquirer.AcquirerChannel;
import com.fss.aeps.constants.ContextKey;
import com.fss.aeps.jaxb.Ack;
import com.fss.aeps.jaxb.HeadType;
import com.fss.aeps.jaxb.PayConstant;
import com.fss.aeps.jaxb.PayTrans;
import com.fss.aeps.jaxb.PayeeType;
import com.fss.aeps.jaxb.PayerType;
import com.fss.aeps.jaxb.ReqPay;
import com.fss.aeps.jaxb.RespPay;
import com.fss.aeps.jpa.acquirer.AcquirerReversal;
import com.fss.aeps.jpa.acquirer.AcquirerReversalPayee;
import com.fss.aeps.jpa.acquirer.AcquirerReversalPayeeId;
import com.fss.aeps.jpa.acquirer.AcquirerReversalRef;
import com.fss.aeps.jpa.acquirer.AcquirerReversalRefId;
import com.fss.aeps.repository.AcquirerRepositories.AcquirerReversalRepository;
import com.fss.aeps.util.AadharAccount;
import com.fss.aeps.util.AadharAccountCollector;
import com.fss.aeps.util.DeviceTagMap;

@Service
public class AcquirerReversalService {

	@Autowired
	private AcquirerReversalRepository repository;

	public void populateRequestData(AcquirerReversal reversal, ReqPay request) {
		final HeadType head = request.getHead();
		final PayTrans txn = request.getTxn();
		final PayerType payer = request.getPayer();

		reversal.setMsgId(head.getMsgId());
		reversal.setMsgTs(head.getTs());
		reversal.setProdType(head.getProdType());
		reversal.setOrgId(head.getOrgId());
		reversal.setMsgVer(head.getVer());

		reversal.setTxnId(txn.getId());
		reversal.setTxnType(txn.getType());
		reversal.setTxnSubType(txn.getSubType());
		reversal.setPurpose(txn.getPurpose());
		reversal.setCustRef(txn.getCustRef());
		reversal.setRefId(txn.getRefId());
		reversal.setRefUrl(txn.getRefUrl());
		reversal.setNote(txn.getNote());
		reversal.setTxnTs(txn.getTs());
		reversal.setInitiationMode(txn.getInitiationMode());

		reversal.setOrgRespCode(txn.getOrgRespCode());
		reversal.setOrgTxnId(txn.getOrgTxnId());
		reversal.setOrgTxnTs(txn.getOrgTxnDate());

		reversal.setPayerAddr(payer.getAddr());
		reversal.setPayerName(payer.getName());
		reversal.setPayerSeqNum(payer.getSeqNum());
		reversal.setPayerCode(payer.getCode());
		reversal.setPayerType(payer.getType());

		if(payer.getInfo() != null) {
			if(payer.getInfo().getIdentity() != null) {
				reversal.setPayerIdentityType(payer.getInfo().getIdentity().getType());
				reversal.setPayerIdentityVerifiedName(payer.getInfo().getIdentity().getVerifiedName());
				reversal.setPayerInfoIdentityId(payer.getInfo().getIdentity().getId());
			}
			if(payer.getInfo().getRating() != null) {
				reversal.setPayerRatingVerifiedAddress(payer.getInfo().getRating().getVerifiedAddress());
			}
		}
		if(payer.getDevice() != null) reversal.setPayerDeviceDetails(DeviceTagMap.toTlvString(payer.getDevice().getTag()));
		if(payer.getAc() != null) {
			reversal.setPayerAcAddrType(payer.getAc().getAddrType());
			AadharAccount ac = payer.getAc().getDetail().stream().collect(AadharAccountCollector.getInstance());
			reversal.setPayerAcIin(ac.iin);
			reversal.setPayerAcUidnumVid(ac.uidVid);
		}

		if(payer.getAmount() != null) {
			reversal.setPayerCurrency(payer.getAmount().getCurr());
			reversal.setPayerAmount(payer.getAmount().getValue());
		}

		final AcquirerChannel channel = (AcquirerChannel) request.context.get(ContextKey.CHANNEL);
		final String agentDetails = (String) request.context.get(ContextKey.AGENT_DETAILS);
		final String reconIndicator = (String) request.context.get(ContextKey.RECON_INDICATOR);
		final String orgTxnMsgId = (String) request.context.get(ContextKey.ORG_TXN_MSG_ID);

		reversal.setChannel(channel);
		reversal.setAgentDetails(agentDetails);
		reversal.setReconIndicator(reconIndicator);
		reversal.setOrgTxnMsgId(orgTxnMsgId);

		if(request.getPayees() != null) {
			final List<PayeeType> payees = request.getPayees().getPayee();
			for (final PayeeType payeeType : payees) {
				final AcquirerReversalPayee payee = new AcquirerReversalPayee();
				final AcquirerReversalPayeeId payeeId = new AcquirerReversalPayeeId();
				payee.setId(payeeId);
				payeeId.setMsgId(head.getMsgId());
				payeeId.setSeqnum(payeeType.getSeqNum());
				payee.setAddr(payeeType.getAddr());
				payee.setType(payeeType.getType());
				payee.setCode(payeeType.getCode());
				if(payeeType.getAmount() != null) {
					payee.setAmount(payeeType.getAmount().getValue());
					payee.setCurrency(payeeType.getAmount().getCurr());
				}
				reversal.getPayees().add(payee);
			}
		}

	}

	public AcquirerReversal registerReversalRequest(AcquirerReversal reversal, ReqPay request) {
		populateRequestData(reversal, request);
		return repository.save(reversal);
	}

	public AcquirerReversal registerReversalResponse(AcquirerReversal reversal, ReqPay request, RespPay response) {
		final Ack reqAck = (Ack) request.context.get(ContextKey.REQUEST_ACK);
		final Ack respAck = (Ack) response.context.get(ContextKey.RESPONSE_ACK);
		final Boolean isStaticResponse = ContextKey.toBoolean(response.context.get(ContextKey.IS_STATIC_RESPONSE));
		reversal.setRespMsgId(response.getHead().getMsgId());
		reversal.setRespMsgTs(response.getHead().getTs());
		reversal.setRespOrgId(response.getHead().getOrgId());
		reversal.setRespResult(response.getResp().getResult());
		reversal.setRespErrCode(response.getResp().getErrCode());
		reversal.setRespTime(new Date());
		reversal.setRespReceived(isStaticResponse ? "N" : "Y");
		response.getResp().getRef().forEach(ref -> {
			final AcquirerReversalRef acqRef = new AcquirerReversalRef();
			final AcquirerReversalRefId acqRefId = new AcquirerReversalRefId(reversal.getMsgId(), ref.getSeqNum(), ref.getType());
			acqRef.setId(acqRefId);
			acqRef.setAddr(ref.getAddr());
			acqRef.setSettAmount(ref.getSettAmount());
			acqRef.setSettCurrency(ref.getSettCurrency());
			acqRef.setApprovalNo(ref.getApprovalNum());
			acqRef.setOrgAmount(ref.getOrgAmount());
			acqRef.setCode(ref.getCode());
			acqRef.setRespCode(ref.getRespCode());
			reversal.getRefs().add(acqRef);
		});

		if(reqAck != null) {
			String errorCdDtl = reqAck.getErrorMessages().stream().map(e -> e.getErrorCd()+"|"+e.getErrorDtl()).collect(Collectors.joining("||"));
			reversal.setReqAckTs(reqAck.getTs());
			reversal.setReqAckErr(reqAck.getErr());
			reversal.setReqAckErrCdDtl(errorCdDtl);
		}

		if(respAck != null) {
			String errorCdDtl = respAck.getErrorMessages().stream().map(e -> e.getErrorCd()+"|"+e.getErrorDtl()).collect(Collectors.joining("||"));
			reversal.setRespAckTs(respAck.getTs());
			reversal.setRespAckErr(respAck.getErr());
			reversal.setRespAckErrCdDtl(errorCdDtl);
		}
		return repository.save(reversal);
	}

	public AcquirerReversal findAcquirerReversal(final String txnId) {
		return repository.findById(txnId).orElse(null);
	}

	public AcquirerReversal updateAcquirerReversal(AcquirerReversal AcquirerReversal) {
		return repository.save(AcquirerReversal);
	}

	public AcquirerReversal findAcquirerReversal(final String custRef, PayConstant txnType, String purpose) {
		return repository.findFirstByCustRefAndTxnTypeAndPurpose(custRef, txnType, purpose);
	}

	public AcquirerReversal findFirstByCustRef(final String custRef) {
		return repository.findFirstByCustRefAndTxnType(custRef, PayConstant.PAY);
	}

}
