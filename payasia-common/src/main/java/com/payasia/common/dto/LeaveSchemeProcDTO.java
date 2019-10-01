package com.payasia.common.dto;

import java.io.Serializable;

public class LeaveSchemeProcDTO implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1190430040145401586L;
	private Boolean status;
	private String errorMsg;
	public Boolean getStatus() {
		return status;
	}
	public void setStatus(Boolean status) {
		this.status = status;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	
	

}
