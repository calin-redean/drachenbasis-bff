package com.cre.drachenbasis.repository;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

public class DistinctDrachenActivity {

	   public static List<String> distinct(String collection, String column){
	        
		   	List<String> retList = new ArrayList<String>();
	        //Get a new connection to the db assuming that it is running 
	        MongoClient m1 = new MongoClient();
	        
	        //use test as a database,use your database here
	        DB db = m1.getDB("drachen");
	        
	         //fetch the collection object ,car is used here,use your own 
	        DBCollection coll = db.getCollection(collection);
	        
	        //call distinct method and store the result in list l1
	        List cl1= coll.distinct(column);
	        
	        //iterate through the list and print the elements
	        for(int i=0;i<cl1.size();i++){
	        	retList.add(cl1.get(i).toString());
	            //System.out.println(cl1.get(i));
	        }
	        
	        m1.close();
	        
	        return retList;
	    }
	   	   
}