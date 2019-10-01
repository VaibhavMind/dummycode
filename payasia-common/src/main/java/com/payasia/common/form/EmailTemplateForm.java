package com.payasia.common.form;

import java.io.Serializable;

public class EmailTemplateForm implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4681129304666671084L;
	private String catagory;
	private String name;
	private String subject;

	public String getCatagory() {
		return catagory;
	}

	public void setCatagory(String catagory) {
		this.catagory = catagory;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}
}
