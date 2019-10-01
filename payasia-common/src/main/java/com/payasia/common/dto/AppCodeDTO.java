package com.payasia.common.dto;

import java.io.Serializable;


public class AppCodeDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7527698699374809550L;

	private long appCodeID;

	private String category;

	private String codeDesc;

	private String codeValue;

	public long getAppCodeID() {
		return appCodeID;
	}

	public void setAppCodeID(long appCodeID) {
		this.appCodeID = appCodeID;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getCodeDesc() {
		return codeDesc;
	}

	public void setCodeDesc(String codeDesc) {
		this.codeDesc = codeDesc;
	}

	public String getCodeValue() {
		return codeValue;
	}

	public void setCodeValue(String codeValue) {
		this.codeValue = codeValue;
	}
	
	
}
