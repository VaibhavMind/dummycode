package com.payasia.common.dto;

import java.io.Serializable;

public class PayAsiaCompanyStatisticReportDTO implements Serializable {

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2102355296739599889L;
	private Long employeeId;
	private String companyGroup;
	private String companyName;
	private String companyCode;
	private int empHeadCount;
	private int activeHeadCount;
	private int noOfUsersAllowToAccess;
	private String hasPayslip;
	private int noOfMonthsPayslipUploaded;
	private String hasHrisModule;
	private String hasLeaveModule;
	private String hasClaimModule;
	private String hasMobileModule;
	private int noOfUsersWithLeaveScheme;
	private int noOfUsersAsLeaveReviewerNotLeaveSchemeAssigned;
	private int noOfUsersWithClaimTemplate;
	private int noOfUsersAsClaimReviewerNotClaimTemplateAssigned;
	public Long getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}
	public String getCompanyGroup() {
		return companyGroup;
	}
	public void setCompanyGroup(String companyGroup) {
		this.companyGroup = companyGroup;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getCompanyCode() {
		return companyCode;
	}
	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}
	public int getEmpHeadCount() {
		return empHeadCount;
	}
	public void setEmpHeadCount(int empHeadCount) {
		this.empHeadCount = empHeadCount;
	}
	public int getActiveHeadCount() {
		return activeHeadCount;
	}
	public void setActiveHeadCount(int activeHeadCount) {
		this.activeHeadCount = activeHeadCount;
	}
	public int getNoOfUsersAllowToAccess() {
		return noOfUsersAllowToAccess;
	}
	public void setNoOfUsersAllowToAccess(int noOfUsersAllowToAccess) {
		this.noOfUsersAllowToAccess = noOfUsersAllowToAccess;
	}
	
	public int getNoOfMonthsPayslipUploaded() {
		return noOfMonthsPayslipUploaded;
	}
	public void setNoOfMonthsPayslipUploaded(int noOfMonthsPayslipUploaded) {
		this.noOfMonthsPayslipUploaded = noOfMonthsPayslipUploaded;
	}
	
	
	
	public int getNoOfUsersWithLeaveScheme() {
		return noOfUsersWithLeaveScheme;
	}
	public void setNoOfUsersWithLeaveScheme(int noOfUsersWithLeaveScheme) {
		this.noOfUsersWithLeaveScheme = noOfUsersWithLeaveScheme;
	}
	public int getNoOfUsersAsLeaveReviewerNotLeaveSchemeAssigned() {
		return noOfUsersAsLeaveReviewerNotLeaveSchemeAssigned;
	}
	public void setNoOfUsersAsLeaveReviewerNotLeaveSchemeAssigned(
			int noOfUsersAsLeaveReviewerNotLeaveSchemeAssigned) {
		this.noOfUsersAsLeaveReviewerNotLeaveSchemeAssigned = noOfUsersAsLeaveReviewerNotLeaveSchemeAssigned;
	}
	public int getNoOfUsersWithClaimTemplate() {
		return noOfUsersWithClaimTemplate;
	}
	public void setNoOfUsersWithClaimTemplate(int noOfUsersWithClaimTemplate) {
		this.noOfUsersWithClaimTemplate = noOfUsersWithClaimTemplate;
	}
	public int getNoOfUsersAsClaimReviewerNotClaimTemplateAssigned() {
		return noOfUsersAsClaimReviewerNotClaimTemplateAssigned;
	}
	public void setNoOfUsersAsClaimReviewerNotClaimTemplateAssigned(
			int noOfUsersAsClaimReviewerNotClaimTemplateAssigned) {
		this.noOfUsersAsClaimReviewerNotClaimTemplateAssigned = noOfUsersAsClaimReviewerNotClaimTemplateAssigned;
	}
	public String getHasPayslip() {
		return hasPayslip;
	}
	public void setHasPayslip(String hasPayslip) {
		this.hasPayslip = hasPayslip;
	}
	public String getHasHrisModule() {
		return hasHrisModule;
	}
	public void setHasHrisModule(String hasHrisModule) {
		this.hasHrisModule = hasHrisModule;
	}
	public String getHasLeaveModule() {
		return hasLeaveModule;
	}
	public void setHasLeaveModule(String hasLeaveModule) {
		this.hasLeaveModule = hasLeaveModule;
	}
	public String getHasClaimModule() {
		return hasClaimModule;
	}
	public void setHasClaimModule(String hasClaimModule) {
		this.hasClaimModule = hasClaimModule;
	}
	public String getHasMobileModule() {
		return hasMobileModule;
	}
	public void setHasMobileModule(String hasMobileModule) {
		this.hasMobileModule = hasMobileModule;
	}
	
	
	
	

}
