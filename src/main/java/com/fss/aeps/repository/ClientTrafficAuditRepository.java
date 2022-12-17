package com.fss.aeps.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.fss.aeps.jpa.ClientTrafficAudit;

@Repository
public interface ClientTrafficAuditRepository extends CrudRepository<ClientTrafficAudit, Integer> {


}