package com.fss.aeps.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.fss.aeps.jpa.issuer.CbsToNpciResponseCodes;

@Repository
public interface CbsToNpciResponseCodesRepository extends CrudRepository<CbsToNpciResponseCodes, String> {

	@Override
	public List<CbsToNpciResponseCodes> findAll();
}