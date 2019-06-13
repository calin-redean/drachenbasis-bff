package com.cre.drachenbasis.repository;

import com.cre.drachenbasis.models.Room;

import java.util.List;

import org.springframework.data.mongodb.repository.Query;
//import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.CrudRepository;


import org.springframework.stereotype.Repository;

@Repository
public interface RoomsRepository extends CrudRepository<Room, String>, RoomsRepositoryCustom {
	@Query("{ 'name' : ?0 }")
	Room findRoomByName(String name);

	List<Room> deleteByName(String name);
	Long deleteRoomByName(String name);
}
