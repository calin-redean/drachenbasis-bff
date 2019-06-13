package com.cre.drachenbasis.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.cre.drachenbasis.models.AG;

@Repository
public interface AGRepository extends MongoRepository<AG, String>{
	
}
