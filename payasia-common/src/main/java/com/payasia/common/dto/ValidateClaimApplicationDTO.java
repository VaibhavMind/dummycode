package com.payasia.common.dto;

import java.io.Serializable;

public class ValidateClaimApplicationDTO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3471834165691894291L;
	private Integer errorCode;
	private String errorKey;
	private String errorValue;
	public Integer getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(Integer errorCode) {
		this.errorCode = errorCode;
	}
	public String getErrorKey() {
		return errorKey;
	}
	public void setErrorKey(String errorKey) {
		this.errorKey = errorKey;
	}
	public String getErrorValue() {
		return errorValue;
	}
	public void setErrorValue(String errorValue) {
		this.errorValue = errorValue;
	}
	
	
	
	

}
