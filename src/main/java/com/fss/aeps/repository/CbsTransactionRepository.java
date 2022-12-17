package com.fss.aeps.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.fss.aeps.jpa.CbsTransaction;

@Repository
public interface CbsTransactionRepository extends CrudRepository<CbsTransaction, Long> {

}