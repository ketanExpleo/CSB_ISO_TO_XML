package com.fss.aeps.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.fss.aeps.jaxb.PayConstant;
import com.fss.aeps.jaxb.TxnSubType;
import com.fss.aeps.jpa.acquirer.AcquirerBalanceEnquiry;
import com.fss.aeps.jpa.acquirer.AcquirerReversal;
import com.fss.aeps.jpa.acquirer.AcquirerTransaction;

@Repository
public class AcquirerRepositories {

	public interface AcquirerBalanceRepository extends CrudRepository<AcquirerBalanceEnquiry, String> {

		public AcquirerBalanceEnquiry findFirstByTxnIdAndTxnType(String txnId, PayConstant txnType);

	}


	public interface AcquirerReversalRepository extends CrudRepository<AcquirerReversal, String> {

		public AcquirerReversal findFirstByTxnIdAndTxnTypeAndTxnSubType(String txnId, PayConstant txnType, TxnSubType txnSubType);

		public AcquirerReversal findFirstByCustRefAndTxnTypeAndPurpose(String custRef, PayConstant txnType, String purpose);

		public AcquirerReversal findFirstByCustRefAndTxnType(String custRef, PayConstant txnType);

	}

	@Repository
	public interface AcquirerTransactionRepository extends CrudRepository<AcquirerTransaction, String> {

		public AcquirerTransaction findFirstByTxnIdAndTxnTypeAndTxnSubType(String txnId, PayConstant txnType, TxnSubType txnSubType);

		public AcquirerTransaction findFirstByDepositIdAndCustRefAndTxnTypeAndPurpose(String depositId, String custRef, PayConstant txnType, String purpose);

		public AcquirerTransaction findFirstByCustRefAndTxnTypeAndPurpose(String custRef, PayConstant txnType, String purpose);

		public AcquirerTransaction findFirstByCustRefAndTxnType(String custRef, PayConstant txnType);

	}


}
