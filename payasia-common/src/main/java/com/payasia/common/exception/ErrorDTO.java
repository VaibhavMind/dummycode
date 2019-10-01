package com.payasia.common.exception;

import java.io.Serializable;

public class ErrorDTO implements Serializable{

	private static final long serialVersionUID = 1L;

	private String errorType;
	private String errorMsg;
	private Long errorReference;
	
	public String getErrorType() {
		return errorType;
	}
	
	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}
	
	public String getErrorMsg() {
		return errorMsg;
	}
	
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	
	public Long getErrorReference() {
		return errorReference;
	}
	
	public void setErrorReference(Long errorReference) {
		this.errorReference = errorReference;
	}
}
