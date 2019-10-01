package com.payasia.common.form;

import java.io.Serializable;

public class ClaimCustomFieldForm implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4201717136582189290L;
	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getMandatory() {
		return mandatory;
	}

	public void setMandatory(String mandatory) {
		this.mandatory = mandatory;
	}

	private String description;
	private String mandatory;

}
