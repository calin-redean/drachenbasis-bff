package com.cre.drachenbasis.models;

import javax.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="drachen")
@JsonIgnoreProperties(value = {"createdAt"}, allowGetters = true)
public class Drache {
    @Id
    private String id;
    
    @NotBlank
    @Size(max=100)
    @Indexed()
    private String first_name;
    private boolean hausaufgaben;
	private String mo_von;
    private String mo_bis;
    private String mo_comment;
    
    private String di_von;
    private String di_bis;
    private String di_comment;
    
    private String mi_von;
    private String mi_bis;
    private String mi_comment;
    
    private String do_von;
    private String do_bis;
    private String do_comment;
    
    private String fr_von;
    private String fr_bis;
    private String fr_comment;
    
    private String schoolclass;
    
	public String getMo_comment() {
		return mo_comment;
	}

	public void setMo_comment(String mo_comment) {
		this.mo_comment = mo_comment;
	}

	public String getDi_comment() {
		return di_comment;
	}

	public void setDi_comment(String di_comment) {
		this.di_comment = di_comment;
	}

	public String getMi_comment() {
		return mi_comment;
	}

	public void setMi_comment(String mi_comment) {
		this.mi_comment = mi_comment;
	}

	public String getDo_comment() {
		return do_comment;
	}

	public void setDo_comment(String do_comment) {
		this.do_comment = do_comment;
	}

	public String getFr_comment() {
		return fr_comment;
	}

	public void setFr_comment(String fr_comment) {
		this.fr_comment = fr_comment;
	}

	public String getSchoolclass() {
		return schoolclass;
	}

	public void setSchoolclass(String schoolclass) {
		this.schoolclass = schoolclass;
	}

	public boolean getHausaufgaben() {
		return hausaufgaben;
	}

	public void setHausaufgaben(boolean hausaufgaben) {
		this.hausaufgaben = hausaufgaben;
	}

	public String getMo_von() {
		return mo_von;
	}

	public void setMo_von(String mo_von) {
		this.mo_von = mo_von;
	}

	public String getMo_bis() {
		return mo_bis;
	}

	public void setMo_bis(String mo_bis) {
		this.mo_bis = mo_bis;
	}

	public String getDi_von() {
		return di_von;
	}

	public void setDi_von(String di_von) {
		this.di_von = di_von;
	}

	public String getDi_bis() {
		return di_bis;
	}

	public void setDi_bis(String di_bis) {
		this.di_bis = di_bis;
	}

	public String getMi_von() {
		return mi_von;
	}

	public void setMi_von(String mi_von) {
		this.mi_von = mi_von;
	}

	public String getMi_bis() {
		return mi_bis;
	}

	public void setMi_bis(String mi_bis) {
		this.mi_bis = mi_bis;
	}

	public String getDo_von() {
		return do_von;
	}

	public void setDo_von(String do_von) {
		this.do_von = do_von;
	}

	public String getDo_bis() {
		return do_bis;
	}

	public void setDo_bis(String do_bis) {
		this.do_bis = do_bis;
	}

	public String getFr_von() {
		return fr_von;
	}

	public void setFr_von(String fr_von) {
		this.fr_von = fr_von;
	}

	public String getFr_bis() {
		return fr_bis;
	}

	public void setFr_bis(String fr_bis) {
		this.fr_bis = fr_bis;
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

	public Drache() {
        super();
    }
    
    @Override
    public String toString() {
        return String.format(
                "Drache[id=%s, Vorname='%s', Mo_von='%s' Mo_bis='%s' di_von='%s' di_bis='%s' mi_von='%s' mi_bis='%s' do_von='%s' do_bis='%s' fr_von='%s' fr_bis='%s']",
                id, first_name, 
                mo_von,
                mo_bis,
                di_von,
                di_bis,
                mi_von,
                mi_bis,
                do_von,
                do_bis,
                fr_von,
                fr_bis
                );
    }
}
