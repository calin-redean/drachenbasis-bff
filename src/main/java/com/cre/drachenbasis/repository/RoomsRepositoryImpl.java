package com.cre.drachenbasis.repository;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;

import com.cre.drachenbasis.models.Room;

@RequiredArgsConstructor
public class RoomsRepositoryImpl implements RoomsRepositoryCustom{
	
	private final MongoOperations operations;
	
	@Override
	public List<Room> getAllRoomsLatestActivity(){
		AggregationResults<Room> results = operations.aggregate(newAggregation(Room.class, //
				sort(Sort.Direction.DESC,"snapshot"),
				group("name")
					.first("id").as("id")
					.first("name").as("name")
					.first("snapshot").as("snapshot")
					.first("drachen").as("drachen")),
				Room.class);

		return results.getMappedResults();
	}

}
