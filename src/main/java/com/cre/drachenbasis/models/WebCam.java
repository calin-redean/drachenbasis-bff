package com.cre.drachenbasis.models;

import org.springframework.data.annotation.Id;

public class WebCam {
    @Id
    private String id;
	private String room;
	private String ip;
	private String username;
	private String pass;
	
	//number of times when there was no connection to the webcam
	//will be reseted with the fist successful connection
	private int errorcount;
	
	//specifies if in the room there is an inactive webcam
	private boolean off;
	
	public boolean isOff() {
		return off;
	}
	public void setOff(boolean off) {
		this.off = off;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getRoom() {
		return room;
	}
	public void setRoom(String room) {
		this.room = room;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPass() {
		return pass;
	}
	public void setPass(String pass) {
		this.pass = pass;
	}
	
	public int getErrorcount() {
		return errorcount;
	}
	public void setErrorcount(int errorcount) {
		this.errorcount = errorcount;
	}
}