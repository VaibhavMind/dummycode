package com.payasia.api.utils;

import java.io.Serializable;

public class ApiErrorMsg implements Serializable {

	private static final long serialVersionUID = 1L;

	private String errorCode;
	private String errorMsg;
	private boolean isError;

	public ApiErrorMsg() {
	}

	public ApiErrorMsg(String errorCode, String errorMsg, boolean isError) {
		super();
		this.errorCode = errorCode;
		this.errorMsg = errorMsg;
		this.isError = isError;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public boolean isError() {
		return isError;
	}

	public void setError(boolean isError) {
		this.isError = isError;
	}

}
