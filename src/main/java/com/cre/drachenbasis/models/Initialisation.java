package com.cre.drachenbasis.models;

import javax.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="initialisation")
@JsonIgnoreProperties(value = {"createdAt"}, allowGetters = true)
public class Initialisation {
    @Id
    private String id;
    
    @NotBlank
    @Size(max=100)
    @Indexed()
    private String table;
    
    public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public String getSnapshot() {
		return snapshot;
	}

	public void setSnapshot(String snapshot) {
		this.snapshot = snapshot;
	}

	private String snapshot;
  
}
