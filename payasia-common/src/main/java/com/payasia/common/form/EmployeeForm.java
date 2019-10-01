package com.payasia.common.form;

import java.io.Serializable;

public class EmployeeForm implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long employeeId;
	private Long companyId;
	private Boolean claimModule = false;
	private Boolean leaveModule = false;
	private Boolean payslipModule = false;
	private Boolean mobileModule = false;
	private String name;
	private String companyDateFormat;
	private String uuid;
	private String workingCompanyTimeZoneOffset;

	public Boolean getClaimModule() {
		return claimModule;
	}
	public void setClaimModule(Boolean claimModule) {
		this.claimModule = claimModule;
	}
	public Boolean getLeaveModule() {
		return leaveModule;
	}
	public void setLeaveModule(Boolean leaveModule) {
		this.leaveModule = leaveModule;
	}
	public Boolean getPayslipModule() {
		return payslipModule;
	}
	public void setPayslipModule(Boolean payslipModule) {
		this.payslipModule = payslipModule;
	}
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCompanyDateFormat() {
		return companyDateFormat;
	}
	public void setCompanyDateFormat(String companyDateFormat) {
		this.companyDateFormat = companyDateFormat;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getWorkingCompanyTimeZoneOffset() {
		return workingCompanyTimeZoneOffset;
	}
	public void setWorkingCompanyTimeZoneOffset(String workingCompanyTimeZoneOffset) {
		this.workingCompanyTimeZoneOffset = workingCompanyTimeZoneOffset;
	}
	public Boolean getMobileModule() {
		return mobileModule;
	}
	public void setMobileModule(Boolean mobileModule) {
		this.mobileModule = mobileModule;
	}
	
	

}
