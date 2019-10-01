package com.payasia.common.dto;

import java.io.Serializable;

public class RollBackProcDTO implements Serializable {
	
	  /**
	 * 
	 */
	private static final long serialVersionUID = 2008789972699273180L;
	private Boolean Status;
	  private String errorMsg;
	public Boolean getStatus() {
		return Status;
	}
	public void setStatus(Boolean status) {
		Status = status;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	  
	  

}
