package com.fss.aeps.services;

import java.util.Date;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fss.aeps.acquirer.AcquirerChannel;
import com.fss.aeps.constants.ContextKey;
import com.fss.aeps.jaxb.Ack;
import com.fss.aeps.jaxb.HeadType;
import com.fss.aeps.jaxb.PayTrans;
import com.fss.aeps.jaxb.PayerType;
import com.fss.aeps.jaxb.ReqBioAuth;
import com.fss.aeps.jaxb.RespBioAuth;
import com.fss.aeps.jpa.acquirer.AcquirerBioAuthPurchase;
import com.fss.aeps.repository.AcquirerRepositories.AcquirerBioAuthPurchaseRepository;
import com.fss.aeps.util.AadharAccount;
import com.fss.aeps.util.AadharAccountCollector;
import com.fss.aeps.util.DeviceTagMap;

@Service
public class AcquirerBioAuthPurchaseService {

	@Autowired
	private AcquirerBioAuthPurchaseRepository repositiry;

	public AcquirerBioAuthPurchase populateRequestData(AcquirerBioAuthPurchase transaction, ReqBioAuth request) {
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
		transaction.setPurpose(txn.getPurpose());
		transaction.setCustRef(txn.getCustRef());
		transaction.setRefId(txn.getRefId());
		transaction.setRefUrl(txn.getRefUrl());
		transaction.setNote(txn.getNote());
		transaction.setTxnTs(txn.getTs());
		transaction.setInitiationMode(txn.getInitiationMode());

		transaction.setPayerAddr(payer.getAddr());
		transaction.setPayerName(payer.getName());
		transaction.setPayerSeqNum(payer.getSeqNum());
		transaction.setPayerCode(payer.getCode());
		transaction.setPayerType(payer.getType());

		final AcquirerChannel channel = (AcquirerChannel) request.context.get(ContextKey.CHANNEL);
		final String agentDetails = (String) request.context.get(ContextKey.AGENT_DETAILS);
		final String reconIndicator = (String) request.context.get(ContextKey.RECON_INDICATOR);

		transaction.setChannel(channel);
		transaction.setAgentDetails(agentDetails);
		transaction.setReconIndicator(reconIndicator);

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
			});
		}
		if(payer.getAmount() != null) {
			transaction.setPayerCurrency(payer.getAmount().getCurr());
			transaction.setPayerAmount(payer.getAmount().getValue());
		}
		return transaction;
	}

	public AcquirerBioAuthPurchase registerTransactionRequest(AcquirerBioAuthPurchase transaction, ReqBioAuth request) {
		populateRequestData(transaction, request);
		return repositiry.save(transaction);
	}

	public AcquirerBioAuthPurchase registerTransactionResponse(AcquirerBioAuthPurchase transaction, RespBioAuth response) {
		if(response != null) {
			final Ack respAck = (Ack) response.context.get(ContextKey.RESPONSE_ACK);
			transaction.setRespTxnTs(response.getTxn().getTs());
			transaction.setRespMsgId(response.getHead().getMsgId());
			transaction.setRespMsgTs(response.getHead().getTs());
			transaction.setRespOrgId(response.getHead().getOrgId());
			transaction.setRespResult(response.getResp().getResult());
			transaction.setRespErrCode(response.getResp().getErrCode());
			transaction.setRespAuthCode(response.getResp().getAuthCode());
			if(response.getUidaiData() != null) transaction.setRespUidaiInfo(response.getUidaiData().getInfo());
			transaction.setRespTime(new Date());
			if(respAck != null) {
				String errorCdDtl = respAck.getErrorMessages().stream().map(e -> e.getErrorCd()+"|"+e.getErrorDtl()).collect(Collectors.joining("||"));
				transaction.setRespAckTs(respAck.getTs());
				transaction.setRespAckErr(respAck.getErr());
				transaction.setRespAckErrCdDtl(errorCdDtl);
			}
		}
		return repositiry.save(transaction);
	}

}
