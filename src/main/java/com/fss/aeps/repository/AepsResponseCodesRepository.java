package com.fss.aeps.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.fss.aeps.jpa.AepsResponseCodes;

@Repository
public interface AepsResponseCodesRepository extends CrudRepository<AepsResponseCodes, String> {

	@Override
	public List<AepsResponseCodes> findAll();
}