package com.payasia.api.utils;

public class ApiSuccessMsg {
	
	private String successCode;
	private String successMsg;
	private boolean isSuccess;
	
	public ApiSuccessMsg() {}

	public ApiSuccessMsg(String successCode, String successMsg, boolean isSuccess) {
		super();
		this.successCode = successCode;
		this.successMsg = successMsg;
		this.isSuccess = isSuccess;
	}

	public String getSuccessCode() {
		return successCode;
	}

	public void setSuccessCode(String successCode) {
		this.successCode = successCode;
	}

	public String getSuccessMsg() {
		return successMsg;
	}

	public void setSuccessMsg(String successMsg) {
		this.successMsg = successMsg;
	}

	public boolean isSuccess() {
		return isSuccess;
	}

	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}
	
	
	
	
}
