package com.fss.aeps.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.fss.aeps.jaxb.PayConstant;
import com.fss.aeps.jaxb.TxnSubType;
import com.fss.aeps.jpa.issuer.IssuerAdvice;
import com.fss.aeps.jpa.issuer.IssuerBalanceEnquiry;
import com.fss.aeps.jpa.issuer.IssuerReversal;
import com.fss.aeps.jpa.issuer.IssuerTransaction;

@Repository
public class IssuerRepositories {

	public interface IssuerBalanceRepository extends CrudRepository<IssuerBalanceEnquiry, String> {

		public IssuerBalanceEnquiry findFirstByTxnIdAndTxnType(String txnId, PayConstant txnType);
	}

	public interface IssuerReversalRepository extends CrudRepository<IssuerReversal, String> {

		public IssuerReversal findFirstByTxnIdAndTxnTypeAndTxnSubType(String txnId, PayConstant txnType, TxnSubType txnSubType);

		public IssuerReversal findFirstByCustRefAndTxnTypeAndPurpose(String custRef, PayConstant txnType, String purpose);

		public Optional<IssuerReversal> findFirstByTxnIdAndTxnTypeAndPurpose(String txnId, PayConstant type, String purpose);

	}

	public interface IssuerTransactionRepository extends CrudRepository<IssuerTransaction, String> {

		public IssuerTransaction findFirstByTxnIdAndTxnTypeAndPurpose(String txnId, PayConstant type, String purpose);

	}

	public interface IssuerAdviceRepository extends CrudRepository<IssuerAdvice, String> {

		public IssuerAdvice findFirstByTxnIdAndTxnTypeAndPurpose(String txnId, PayConstant type, String purpose);

		public IssuerAdvice findFirstByTxnIdAndDepositId(String id, String depositId);

		public IssuerAdvice findFirstByTxnIdAndOrgRrn(String id, String orgRrn);

		public IssuerAdvice findFirstByOrgTxnId(String id);

	}
}
