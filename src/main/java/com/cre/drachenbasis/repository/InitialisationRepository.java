package com.cre.drachenbasis.repository;

import com.cre.drachenbasis.models.Initialisation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface InitialisationRepository extends MongoRepository<Initialisation, String> {
	@Query("{ 'table' : ?0 }")
	Initialisation findByTable(String table);
	@Query("{'snapshot': ?0 }")
	Initialisation findTableBySnapshot(String snapshot);
}
