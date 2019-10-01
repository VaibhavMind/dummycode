package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

public class OTReviewerForm extends PageResponse implements Serializable{
	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2689261601872447312L;
	private String status;
	private String employeeStatus;
	private String employeeName;
	private String otReviewer1;
	private String otReviewer2;
	private String otReviewer3;
	
	public String getEmployeeStatus() {
		return employeeStatus;
	}
	public void setEmployeeStatus(String employeeStatus) {
		this.employeeStatus = employeeStatus;
	}
	private Long otSchemeId;
	private String otSchemeName;
	

	
	private String otReviewerName;
	private Long otReviewerId;
	private Long employeeOTReviewerId;
	
	private Long employeeOTReviewerId1;
	private Long employeeOTReviewerId2;
	private Long employeeOTReviewerId3;
	
	
	
	private String ruleName;
	private String ruleValue;
	
	private Long otReveiwerId1;
	private Long otReveiwerId2;
	private Long otReveiwerId3;
	
	
	private Long otReviewerRuleId;
	
	private Long otReviewerRuleId1;
	private Long otReviewerRuleId2;
	private Long otReviewerRuleId3;
	
	
	
	
	private Long employeeId;
	
	
	
	public Long getEmployeeOTReviewerId() {
		return employeeOTReviewerId;
	} 
	public void setEmployeeOTReviewerId(Long employeeOTReviewerId) {
		this.employeeOTReviewerId = employeeOTReviewerId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	public Long getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}
	public List<OTReviewerForm> getRows() {
		return rows;
	}
	public void setRows(List<OTReviewerForm> rows) {
		this.rows = rows;
	}
	private List<OTReviewerForm> rows;
	
	
	
	public String getEmployeeName() {
		return employeeName;
	}
	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}
	public String getOtReviewer1() {
		return otReviewer1;
	}
	public void setOtReviewer1(String otReviewer1) {
		this.otReviewer1 = otReviewer1;
	}
	public String getOtReviewer2() {
		return otReviewer2;
	}
	public void setOtReviewer2(String otReviewer2) {
		this.otReviewer2 = otReviewer2;
	}
	public String getOtReviewer3() {
		return otReviewer3;
	}
	public void setOtReviewer3(String otReviewer3) {
		this.otReviewer3 = otReviewer3;
	}
	public Long getOtSchemeId() {
		return otSchemeId;
	}
	public void setOtSchemeId(Long otSchemeId) {
		this.otSchemeId = otSchemeId;
	}
	public String getOtSchemeName() {
		return otSchemeName;
	}
	public void setOtSchemeName(String otSchemeName) {
		this.otSchemeName = otSchemeName;
	}
	public Long getOtReviewerId() {
		return otReviewerId;
	}
	public void setOtReviewerId(Long otReviewerId) {
		this.otReviewerId = otReviewerId;
	}
	public String getOtReviewerName() {
		return otReviewerName;
	}
	public void setOtReviewerName(String otReviewerName) {
		this.otReviewerName = otReviewerName;
	}
	
	public Long getEmployeeOTReviewerId1() {
		return employeeOTReviewerId1;
	}
	public void setEmployeeOTReviewerId1(Long employeeOTReviewerId1) {
		this.employeeOTReviewerId1 = employeeOTReviewerId1;
	}
	public Long getEmployeeOTReviewerId2() {
		return employeeOTReviewerId2;
	}
	public void setEmployeeOTReviewerId2(Long employeeOTReviewerId2) {
		this.employeeOTReviewerId2 = employeeOTReviewerId2;
	}
	public Long getEmployeeOTReviewerId3() {
		return employeeOTReviewerId3;
	}
	public void setEmployeeOTReviewerId3(Long employeeOTReviewerId3) {
		this.employeeOTReviewerId3 = employeeOTReviewerId3;
	}
	public String getRuleName() {
		return ruleName;
	}
	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}
	public String getRuleValue() {
		return ruleValue;
	}
	public void setRuleValue(String ruleValue) {
		this.ruleValue = ruleValue;
	}
	
	public Long getOtReviewerRuleId() {
		return otReviewerRuleId;
	}
	public void setOtReviewerRuleId(Long otReviewerRuleId) {
		this.otReviewerRuleId = otReviewerRuleId;
	}
	public Long getOtReviewerRuleId1() {
		return otReviewerRuleId1;
	}
	public void setOtReviewerRuleId1(Long otReviewerRuleId1) {
		this.otReviewerRuleId1 = otReviewerRuleId1;
	}
	public Long getOtReviewerRuleId2() {
		return otReviewerRuleId2;
	}
	public void setOtReviewerRuleId2(Long otReviewerRuleId2) {
		this.otReviewerRuleId2 = otReviewerRuleId2;
	}
	public Long getOtReviewerRuleId3() {
		return otReviewerRuleId3;
	}
	public void setOtReviewerRuleId3(Long otReviewerRuleId3) {
		this.otReviewerRuleId3 = otReviewerRuleId3;
	}
	public Long getOtReveiwerId1() {
		return otReveiwerId1;
	}
	public void setOtReveiwerId1(Long otReveiwerId1) {
		this.otReveiwerId1 = otReveiwerId1;
	}
	public Long getOtReveiwerId2() {
		return otReveiwerId2;
	}
	public void setOtReveiwerId2(Long otReveiwerId2) {
		this.otReveiwerId2 = otReveiwerId2;
	}
	public Long getOtReveiwerId3() {
		return otReveiwerId3;
	}
	public void setOtReveiwerId3(Long otReveiwerId3) {
		this.otReveiwerId3 = otReveiwerId3;
	}
	
	
	
	

}
