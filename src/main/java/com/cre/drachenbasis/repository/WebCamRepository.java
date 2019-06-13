package com.cre.drachenbasis.repository;

import com.cre.drachenbasis.models.WebCam;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface WebCamRepository extends MongoRepository<WebCam, String> {
	/*@Query("{ 'ip' : '?0' }")
	WebCam findByIP(String ip);*/
	@Query("{off:false}")
	List<WebCam> findAllActive();
}
