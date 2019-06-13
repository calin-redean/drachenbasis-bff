package com.cre.drachenbasis.repository;

import com.cre.drachenbasis.models.Drache;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DracheRepository extends MongoRepository<Drache, String> {
	@Query("{ 'first_name' : ?0 }")
	Drache findByThePersonsFirstname(String firstname);
}
