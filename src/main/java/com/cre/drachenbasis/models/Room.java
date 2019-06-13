package com.cre.drachenbasis.models;


import java.util.Set;

import javax.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import com.cre.drachenbasis.models.Drache;

@Document(collection="rooms")
@JsonIgnoreProperties(value = {"createdAt"}, allowGetters = true)
public class Room {
    @Id
    private String id;
    
    @NotBlank
    @Size(max=100)
    @Indexed()
    private String name; 
	private String snapshot;
    private Set<String> drachen;
     
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Room() {
        super();
    }

    public String getSnapshot() {
		return snapshot;
	}

	public void setSnapshot(String snapshot) {
		this.snapshot = snapshot;
	}

	public Set<String> getDrachen() {
		return drachen;
	}

	public void setDrachen(Set<String> drachen) {
		this.drachen = drachen;
	}	
	
    @Override
    public String toString() {
        return String.format(
                "Room[id=%s, Name='%s', Name='%s']",
                id, name);
    }

}
