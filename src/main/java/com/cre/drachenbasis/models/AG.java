package com.cre.drachenbasis.models;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Document(collection="ag")
@JsonIgnoreProperties(value = {"createdAt"}, allowGetters = true)
public class AG {
    @Id
    private String id;
    
    @NotBlank
    @Size(max=100)
    @Indexed()
    private String first_name;
    private String schoolclass;
    private String hort;
    @NotBlank
    @Size(max=100)
    @Indexed()    
    private String ag_name;
	private String day;
	private String von;
	private String bis;
	
	public String getVon() {
		return von;
	}
	public void setVon(String von) {
		this.von = von;
	}
	public String getBis() {
		return bis;
	}
	public void setBis(String bis) {
		this.bis = bis;
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
	public String getSchoolclass() {
		return schoolclass;
	}
	public void setSchoolclass(String schoolclass) {
		this.schoolclass = schoolclass;
	}
	public String getHort() {
		return hort;
	}
	public void setHort(String hort) {
		this.hort = hort;
	}
	public String getAg_name() {
		return ag_name;
	}
	public void setAg_name(String ag_name) {
		this.ag_name = ag_name;
	}
	public String getDay() {
		return day;
	}
	public void setDay(String day) {
		this.day = day;
	}
	
}
