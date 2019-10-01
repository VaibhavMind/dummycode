package com.payasia.common.dto;

import java.io.Serializable;


public class ClaimReviewersDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7019002345490284784L;
	private String employeeName;
	private String employeeNumber;
	private Long employeeId ;
	
	private String claimTemplate;
	private String reviewer1EmployeeNumber;
	private String reviewer1CompanyCode;
	private String reviewer2EmployeeNumber;
	private String reviewer2CompanyCode;
	private String reviewer3EmployeeNumber;
	private String reviewer3CompanyCode;
	
	private String reviewer1;
	private String reviewer2;
	private String reviewer3;
	
	private Long reviewer1Id;
	private Long reviewer2Id;
	private Long reviewer3Id;
	private Long employeeClaimTemplateId ;
	private Long claimTemplateId ;
	
	private String ruleValue;
	
	
	public String getEmployeeName() {
		return employeeName;
	}
	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}
	public String getEmployeeNumber() {
		return employeeNumber;
	}
	public void setEmployeeNumber(String employeeNumber) {
		this.employeeNumber = employeeNumber;
	}
	public String getReviewer1() {
		return reviewer1;
	}
	public void setReviewer1(String reviewer1) {
		this.reviewer1 = reviewer1;
	}
	public String getReviewer2() {
		return reviewer2;
	}
	public void setReviewer2(String reviewer2) {
		this.reviewer2 = reviewer2;
	}
	public String getReviewer3() {
		return reviewer3;
	}
	public void setReviewer3(String reviewer3) {
		this.reviewer3 = reviewer3;
	}
	public String getReviewer1EmployeeNumber() {
		return reviewer1EmployeeNumber;
	}
	public void setReviewer1EmployeeNumber(String reviewer1EmployeeNumber) {
		this.reviewer1EmployeeNumber = reviewer1EmployeeNumber;
	}
	public String getReviewer1CompanyCode() {
		return reviewer1CompanyCode;
	}
	public void setReviewer1CompanyCode(String reviewer1CompanyCode) {
		this.reviewer1CompanyCode = reviewer1CompanyCode;
	}
	public String getReviewer2EmployeeNumber() {
		return reviewer2EmployeeNumber;
	}
	public void setReviewer2EmployeeNumber(String reviewer2EmployeeNumber) {
		this.reviewer2EmployeeNumber = reviewer2EmployeeNumber;
	}
	public String getReviewer2CompanyCode() {
		return reviewer2CompanyCode;
	}
	public void setReviewer2CompanyCode(String reviewer2CompanyCode) {
		this.reviewer2CompanyCode = reviewer2CompanyCode;
	}
	public String getReviewer3EmployeeNumber() {
		return reviewer3EmployeeNumber;
	}
	public void setReviewer3EmployeeNumber(String reviewer3EmployeeNumber) {
		this.reviewer3EmployeeNumber = reviewer3EmployeeNumber;
	}
	public String getReviewer3CompanyCode() {
		return reviewer3CompanyCode;
	}
	public void setReviewer3CompanyCode(String reviewer3CompanyCode) {
		this.reviewer3CompanyCode = reviewer3CompanyCode;
	}
	public Long getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}
	
	public String getRuleValue() {
		return ruleValue;
	}
	public void setRuleValue(String ruleValue) {
		this.ruleValue = ruleValue;
	}
	public Long getReviewer1Id() {
		return reviewer1Id;
	}
	public void setReviewer1Id(Long reviewer1Id) {
		this.reviewer1Id = reviewer1Id;
	}
	public Long getReviewer2Id() {
		return reviewer2Id;
	}
	public void setReviewer2Id(Long reviewer2Id) {
		this.reviewer2Id = reviewer2Id;
	}
	public Long getReviewer3Id() {
		return reviewer3Id;
	}
	public void setReviewer3Id(Long reviewer3Id) {
		this.reviewer3Id = reviewer3Id;
	}
	public String getClaimTemplate() {
		return claimTemplate;
	}
	public void setClaimTemplate(String claimTemplate) {
		this.claimTemplate = claimTemplate;
	}
	public Long getEmployeeClaimTemplateId() {
		return employeeClaimTemplateId;
	}
	public void setEmployeeClaimTemplateId(Long employeeClaimTemplateId) {
		this.employeeClaimTemplateId = employeeClaimTemplateId;
	}
	public Long getClaimTemplateId() {
		return claimTemplateId;
	}
	public void setClaimTemplateId(Long claimTemplateId) {
		this.claimTemplateId = claimTemplateId;
	}
	
	
}
