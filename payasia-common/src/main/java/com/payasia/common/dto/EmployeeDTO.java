package com.payasia.common.dto;

import java.io.Serializable;
import java.util.Date;

public class EmployeeDTO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3433638821449252949L;
	private Long employeeId;
	private Long companyId;
	private Long token;
	private Date lastAccessedTime;
	private String uuid;
	private String companyCode;
	private String workingCompanyTimezoneOffset;
	private String companyDateFormat;
	
	public Long getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}
	public Long getCompanyId() {
		return companyId;
	}
	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}
	public Long getToken() {
		return token;
	}
	public void setToken(Long token) {
		this.token = token;
	}
	public Date getLastAccessedTime() {
		return lastAccessedTime;
	}
	public void setLastAccessedTime(Date lastAccessedTime) {
		this.lastAccessedTime = lastAccessedTime;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getCompanyCode() {
		return companyCode;
	}
	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}
	public String getWorkingCompanyTimezoneOffset() {
		return workingCompanyTimezoneOffset;
	}
	public void setWorkingCompanyTimezoneOffset(String workingCompanyTimezoneOffset) {
		this.workingCompanyTimezoneOffset = workingCompanyTimezoneOffset;
	}
	public String getCompanyDateFormat() {
		return companyDateFormat;
	}
	public void setCompanyDateFormat(String companyDateFormat) {
		this.companyDateFormat = companyDateFormat;
	}
	
	
	
	

}
