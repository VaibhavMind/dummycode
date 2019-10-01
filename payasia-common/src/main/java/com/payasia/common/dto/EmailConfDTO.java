package com.payasia.common.dto;

import java.io.Serializable;

import com.payasia.dao.bean.EmailTemplate;

public class EmailConfDTO implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6178219859162476255L;
	private String errorKey;
	private EmailTemplate emailTemplate;
	public String getErrorKey() {
		return errorKey;
	}
	public void setErrorKey(String errorKey) {
		this.errorKey = errorKey;
	}
	public EmailTemplate getEmailTemplate() {
		return emailTemplate;
	}
	public void setEmailTemplate(EmailTemplate emailTemplate) {
		this.emailTemplate = emailTemplate;
	}
	
	
	
	

}
