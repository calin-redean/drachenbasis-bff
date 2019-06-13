package com.cre.drachenbasis.repository;

import com.cre.drachenbasis.models.RFIDReader;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RFIDRepository extends MongoRepository<RFIDReader, String> {
	@Query("{ 'hostname' : '?0' }")
	RFIDReader findByHostname(String hostname);
	//@Query("{off:false}")
	//List<RFIDReader> findAllActive();
}
