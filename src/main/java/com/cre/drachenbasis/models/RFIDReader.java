package com.cre.drachenbasis.models;

import org.springframework.data.annotation.Id;

public class RFIDReader {
    @Id
    private String id;
	private String room;
	private String hostname;
	private String checklist;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getChecklist() {
		return checklist;
	}
	public void setChecklist(String checklist) {
		this.checklist = checklist;
	}
	public String getRoom() {
		return room;
	}
	public void setRoom(String room) {
		this.room = room;
	}
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

}