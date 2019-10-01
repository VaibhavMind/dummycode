package com.payasia.common.dto;

import java.io.Serializable;

public class ActivationDTO  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -704830952464311605L;
	
	private String companyCode;
	private Long employeeActivationCodeId;
	private Long companyId;
	private boolean ssoEnable=false;
	
	
	public boolean isSsoEnable() {
		return ssoEnable;
	}
	public void setSsoEnable(boolean ssoEnable) {
		this.ssoEnable = ssoEnable;
	}
	public String getCompanyCode() {
		return companyCode;
	}
	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}
	public Long getEmployeeActivationCodeId() {
		return employeeActivationCodeId;
	}
	public void setEmployeeActivationCodeId(Long employeeActivationCodeId) {
		this.employeeActivationCodeId = employeeActivationCodeId;
	}
	public Long getCompanyId() {
		return companyId;
	}
	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}
	
	

}
