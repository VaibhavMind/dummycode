package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

public class EmpOTAddForm implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5055175214442978332L;
	private Long companyId;
	private Long employeeId;
	private Long otApplicationId;
	private Long otTemplateId;
	private String otTemplateName;
	
	private Long otStatusId;
	private String createdDate;
	private String updatedDate;
	private String generalRemarks;
	
	private Long otApplicationItemId;
	private String otDate;
	private Long dayTypeId;
	private String dayType;
	private String remarks;
	
	private Long otApplicationItemDetailId;
	private Long otTemplateItemId;
	private String otTemplateItemValue;
	
	private Long otApplicationItemDetailWorkFlowId;
	private Long otApplicationItemWorkFlowId;
	
	private Long otReviewer1Id;
	private String otReviewer1;
	private String otReviewer2;
	private Long otReviewer2Id;
	private String otReviewer3;
	private Long otReviewer3Id;
	private int totalNoOfReviewers;
	private List<OTItemDefinitionForm> otTypeFormList;
	
	private Long otApplicationReviewerId;
	private Long employeeReviewerId;
	private Long workFlowRuleId;
	
	private Long otApplicationWorkFlowId;
	private String forwardTo;
	private String emailCC;
	private String createdBYId;
	
	
	
	public Long getOtReviewer1Id() {
		return otReviewer1Id;
	}

	public void setOtReviewer1Id(Long otReviewer1Id) {
		this.otReviewer1Id = otReviewer1Id;
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

	public Long getOtReviewer2Id() {
		return otReviewer2Id;
	}

	public void setOtReviewer2Id(Long otReviewer2Id) {
		this.otReviewer2Id = otReviewer2Id;
	}

	public String getOtReviewer3() {
		return otReviewer3;
	}

	public void setOtReviewer3(String otReviewer3) {
		this.otReviewer3 = otReviewer3;
	}

	public Long getOtReviewer3Id() {
		return otReviewer3Id;
	}

	public void setOtReviewer3Id(Long otReviewer3Id) {
		this.otReviewer3Id = otReviewer3Id;
	}

	public int getTotalNoOfReviewers() {
		return totalNoOfReviewers;
	}

	public void setTotalNoOfReviewers(int totalNoOfReviewers) {
		this.totalNoOfReviewers = totalNoOfReviewers;
	}

	
	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public Long getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}

	public Long getOtApplicationId() {
		return otApplicationId;
	}

	public void setOtApplicationId(Long otApplicationId) {
		this.otApplicationId = otApplicationId;
	}

	public Long getOtTemplateId() {
		return otTemplateId;
	}

	public void setOtTemplateId(Long otTemplateId) {
		this.otTemplateId = otTemplateId;
	}

	public Long getOtStatusId() {
		return otStatusId;
	}

	public void setOtStatusId(Long otStatusId) {
		this.otStatusId = otStatusId;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public String getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(String updatedDate) {
		this.updatedDate = updatedDate;
	}

	public String getGeneralRemarks() {
		return generalRemarks;
	}

	public void setGeneralRemarks(String generalRemarks) {
		this.generalRemarks = generalRemarks;
	}

	public Long getOtApplicationItemId() {
		return otApplicationItemId;
	}

	public void setOtApplicationItemId(Long otApplicationItemId) {
		this.otApplicationItemId = otApplicationItemId;
	}

	public String getOtDate() {
		return otDate;
	}

	public void setOtDate(String otDate) {
		this.otDate = otDate;
	}

	public Long getDayTypeId() {
		return dayTypeId;
	}

	public void setDayTypeId(Long dayTypeId) {
		this.dayTypeId = dayTypeId;
	}

	public Long getOtApplicationItemDetailId() {
		return otApplicationItemDetailId;
	}

	public void setOtApplicationItemDetailId(Long otApplicationItemDetailId) {
		this.otApplicationItemDetailId = otApplicationItemDetailId;
	}

	public Long getOtTemplateItemId() {
		return otTemplateItemId;
	}

	public void setOtTemplateItemId(Long otTemplateItemId) {
		this.otTemplateItemId = otTemplateItemId;
	}

	public String getOtTemplateItemValue() {
		return otTemplateItemValue;
	}

	public void setOtTemplateItemValue(String otTemplateItemValue) {
		this.otTemplateItemValue = otTemplateItemValue;
	}

	public Long getOtApplicationItemDetailWorkFlowId() {
		return otApplicationItemDetailWorkFlowId;
	}

	public void setOtApplicationItemDetailWorkFlowId(
			Long otApplicationItemDetailWorkFlowId) {
		this.otApplicationItemDetailWorkFlowId = otApplicationItemDetailWorkFlowId;
	}

	public Long getOtApplicationItemWorkFlowId() {
		return otApplicationItemWorkFlowId;
	}

	public void setOtApplicationItemWorkFlowId(Long otApplicationItemWorkFlowId) {
		this.otApplicationItemWorkFlowId = otApplicationItemWorkFlowId;
	}

	public Long getOtApplicationReviewerId() {
		return otApplicationReviewerId;
	}

	public void setOtApplicationReviewerId(Long otApplicationReviewerId) {
		this.otApplicationReviewerId = otApplicationReviewerId;
	}

	public Long getEmployeeReviewerId() {
		return employeeReviewerId;
	}

	public void setEmployeeReviewerId(Long employeeReviewerId) {
		this.employeeReviewerId = employeeReviewerId;
	}

	public Long getWorkFlowRuleId() {
		return workFlowRuleId;
	}

	public void setWorkFlowRuleId(Long workFlowRuleId) {
		this.workFlowRuleId = workFlowRuleId;
	}

	public Long getOtApplicationWorkFlowId() {
		return otApplicationWorkFlowId;
	}

	public void setOtApplicationWorkFlowId(Long otApplicationWorkFlowId) {
		this.otApplicationWorkFlowId = otApplicationWorkFlowId;
	}

	public String getForwardTo() {
		return forwardTo;
	}

	public void setForwardTo(String forwardTo) {
		this.forwardTo = forwardTo;
	}

	public String getEmailCC() {
		return emailCC;
	}

	public void setEmailCC(String emailCC) {
		this.emailCC = emailCC;
	}

	public String getCreatedBYId() {
		return createdBYId;
	}

	public void setCreatedBYId(String createdBYId) {
		this.createdBYId = createdBYId;
	}

	public String getDayType() {
		return dayType;
	}

	public void setDayType(String dayType) {
		this.dayType = dayType;
	}

	

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getOtTemplateName() {
		return otTemplateName;
	}

	public void setOtTemplateName(String otTemplateName) {
		this.otTemplateName = otTemplateName;
	}

	public List<OTItemDefinitionForm> getOtTypeFormList() {
		return otTypeFormList;
	}

	public void setOtTypeFormList(List<OTItemDefinitionForm> otTypeFormList) {
		this.otTypeFormList = otTypeFormList;
	}


}
