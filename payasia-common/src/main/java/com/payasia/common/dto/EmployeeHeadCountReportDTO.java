package com.payasia.common.dto;

import java.io.Serializable;
import java.util.HashMap;

public class EmployeeHeadCountReportDTO implements Serializable {

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2102355296739599889L;
	private Long employeeId;
	private String employeeNumber;
	private String firstName;
	private String lastName;
	private String leaveSchemeName;
	private String claimTemplateName;
	private String originalHireDate;
	private String resignationDate;
	private String isReviewer;
	
	private Long companyId;
	private String companyCode;
	private String companyName;
	private String countryName;
	
	private String fromPeriod;
	private String toPeriod;
	private String generatedBy;
	private int totalEmployeesCount;
	private int employeeWithoutLeaveScheme;
	private int employeesWithoutClaimTemplate;
	
	private HashMap<String,String> transactions;
	
	
	public Long getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}
	public String getEmployeeNumber() {
		return employeeNumber;
	}
	public void setEmployeeNumber(String employeeNumber) {
		this.employeeNumber = employeeNumber;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getLeaveSchemeName() {
		return leaveSchemeName;
	}
	public void setLeaveSchemeName(String leaveSchemeName) {
		this.leaveSchemeName = leaveSchemeName;
	}
	public String getOriginalHireDate() {
		return originalHireDate;
	}
	public void setOriginalHireDate(String originalHireDate) {
		this.originalHireDate = originalHireDate;
	}
	public String getResignationDate() {
		return resignationDate;
	}
	public void setResignationDate(String resignationDate) {
		this.resignationDate = resignationDate;
	}
	public String getIsReviewer() {
		return isReviewer;
	}
	public void setIsReviewer(String isReviewer) {
		this.isReviewer = isReviewer;
	}
	public Long getCompanyId() {
		return companyId;
	}
	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}
	public String getCompanyCode() {
		return companyCode;
	}
	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getCountryName() {
		return countryName;
	}
	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}
	public HashMap<String, String> getTransactions() {
		return transactions;
	}
	public void setTransactions(HashMap<String, String> transactions) {
		this.transactions = transactions;
	}
	public String getFromPeriod() {
		return fromPeriod;
	}
	public void setFromPeriod(String fromPeriod) {
		this.fromPeriod = fromPeriod;
	}
	public String getToPeriod() {
		return toPeriod;
	}
	public void setToPeriod(String toPeriod) {
		this.toPeriod = toPeriod;
	}
	public String getGeneratedBy() {
		return generatedBy;
	}
	public void setGeneratedBy(String generatedBy) {
		this.generatedBy = generatedBy;
	}
	public int getTotalEmployeesCount() {
		return totalEmployeesCount;
	}
	public void setTotalEmployeesCount(int totalEmployeesCount) {
		this.totalEmployeesCount = totalEmployeesCount;
	}
	public String getClaimTemplateName() {
		return claimTemplateName;
	}
	public void setClaimTemplateName(String claimTemplateName) {
		this.claimTemplateName = claimTemplateName;
	}
	public int getEmployeeWithoutLeaveScheme() {
		return employeeWithoutLeaveScheme;
	}
	public void setEmployeeWithoutLeaveScheme(int employeeWithoutLeaveScheme) {
		this.employeeWithoutLeaveScheme = employeeWithoutLeaveScheme;
	}
	public int getEmployeesWithoutClaimTemplate() {
		return employeesWithoutClaimTemplate;
	}
	public void setEmployeesWithoutClaimTemplate(int employeesWithoutClaimTemplate) {
		this.employeesWithoutClaimTemplate = employeesWithoutClaimTemplate;
	}

}
