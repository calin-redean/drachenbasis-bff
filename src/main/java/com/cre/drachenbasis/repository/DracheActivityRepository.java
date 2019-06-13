package com.cre.drachenbasis.repository;

import com.cre.drachenbasis.models.DracheActivity;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DracheActivityRepository extends MongoRepository<DracheActivity, String> {
	@Query("{ 'first_name' : ?0 }")
	DracheActivity findByThePersonsFirstname(String firstname);
	@Query("{ hausaufgaben:true, status : 'IN'}")
	List<DracheActivity> findAllHomeworkDrachen(Sort sortByFirstName);
}
