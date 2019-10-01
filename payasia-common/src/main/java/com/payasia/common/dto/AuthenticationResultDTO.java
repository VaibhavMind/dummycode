package com.payasia.common.dto;

import java.io.Serializable;

public class AuthenticationResultDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8923930017714831240L;
	
	private Boolean isAuthenticated;
	private Boolean isInvalidLoginAttemptsExceeded;
	public Boolean getIsAuthenticated() {
		return isAuthenticated;
	}
	public void setIsAuthenticated(Boolean isAuthenticated) {
		this.isAuthenticated = isAuthenticated;
	}
	public Boolean getIsInvalidLoginAttemptsExceeded() {
		return isInvalidLoginAttemptsExceeded;
	}
	public void setIsInvalidLoginAttemptsExceeded(
			Boolean isInvalidLoginAttemptsExceeded) {
		this.isInvalidLoginAttemptsExceeded = isInvalidLoginAttemptsExceeded;
	}
	
	
	
	
	

}
