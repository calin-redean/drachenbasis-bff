package com.cre.drachenbasis.models;

import javax.validation.constraints.Size;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Document(collection="drachenactivity")
@JsonIgnoreProperties(value = {"createdAt"}, allowGetters = true)
public class DracheActivity {

	public enum Presence{
		PRESENT, IN, OUT, ABSENT, EXCUSED
	}
	
    @Id
    private String id;
	
	@NotBlank
    @Size(max=100)
    @Indexed()
    private String first_name;
	
	private Presence status;
	private boolean essenStatus;
	private Boolean hausaufgaben;
	private String room;
	private String lastroom;
	private boolean hausaufgabenStatus;
	
	private String arrival;
	private String leaving;
	private String schoolclass;
	private String comment;
	
	private Map<String,Presence> week = new HashMap<String,Presence>();	
	private Map<String,String> weekComment = new HashMap<String,String>();
	private Map<String, String> weekArrival = new HashMap<String, String>();
	private Map<String, String> weekLeaving = new HashMap<String, String>();
	
	
	public Map<String, String> getWeekArrival() {
		return weekArrival;
	}

	public void setWeekArrival(Map<String, String> weekArrival) {
		this.weekArrival = weekArrival;
	}

	public Map<String, String> getWeekLeaving() {
		return weekLeaving;
	}

	public void setWeekLeaving(Map<String, String> weekLeaving) {
		this.weekLeaving = weekLeaving;
	}

	public String getArrival() {
		return arrival;
	}

	public void setArrival(String arrival) {
		this.arrival = arrival;
	}

	public String getLeaving() {
		return leaving;
	}

	public void setLeaving(String leaving) {
		this.leaving = leaving;
	}

	public void setWeekComment(Map<String, String> weekComment) {
		this.weekComment = weekComment;
	}

	public String getSchoolclass() {
		return schoolclass;
	}

	public void setSchoolclass(String schoolclass) {
		this.schoolclass = schoolclass;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public boolean getHausaufgabenStatus() {
		return hausaufgabenStatus;
	}

	public void setHausaufgabenStatus(boolean hausaufgabenStatus) {
		this.hausaufgabenStatus = hausaufgabenStatus;
	}

	public boolean getEssenStatus() {
		return essenStatus;
	}

	public void setEssenStatus(boolean essen) {
		this.essenStatus = essen;
	}

	public Boolean getHausaufgaben() {
		return hausaufgaben;
	}

	public void setHausaufgaben(Boolean hausaufgaben) {
		this.hausaufgaben = hausaufgaben;
	}

	public Map<String,Presence> getWeek() {
		return week;
	}

	public void setWeek(Map<String,Presence> week) {
		this.week = week;
	}
	public Presence getPresenceForDay(String day) {
		return week.get(day);
	}
	
	public void setPresenceForDay(String day, Presence presence) {
		week.put(day, presence);
	}
	
	public void setCommentForDay(String day, String comment) {
		weekComment.put(day, comment);
	}
	public String getCommentForDay(String day) {
		return weekComment.get(day);
	}

	public void setArrivalForDay(String day, String arrival) {
		weekArrival.put(day, arrival);
	}
	public String getArrivalForDay(String day) {
		return weekArrival.get(day);
	}

	public void setLeavingForDay(String day, String leaving) {
		weekLeaving.put(day, leaving);
	}
	public String getLeavingForDay(String day) {
		return weekLeaving.get(day);
	}

	
	public Map<String,String> getWeekComment() {
		return weekComment;
	}

	public void setWeekComent(Map<String,String> weekComment) {
		this.weekComment = weekComment;
	}	
	public Presence getStatus() {
		return status;
	}

	public void setStatus(Presence status) {
		this.status = status;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFirst_name() {
		return first_name;
	}

	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}

	public String getRoom() {
		return room;
	}

	public void setRoom(String room) {
		this.room = room;
	}
	public String getLastRoom() {
		return lastroom;
	}

	public void setLastRoom(String room) {
		this.lastroom = room;
	}
}
