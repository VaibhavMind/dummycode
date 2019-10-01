package com.payasia.common.form;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_DEFAULT)
public class ClaimApplicationForm implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6055882811610217238L;
	private Long claimApplicationId;
	private Long companyId;
	private Long employeeId;
	private Long claimTemplateId;
	private String claimTemplateName;
	private String createdDate;
	private String updatedDate;
	private Integer totalItems;
	private BigDecimal totalAmount;
	private Long claimStatusId;
	private String remarks;
	private String claimFormName;
	private Long claimNumber;
	
	private boolean canOverride;
	private boolean canReject;
	private boolean canApprove;
	private boolean canForward;
	
	
	
	
	public boolean isCanOverride() {
		return canOverride;
	}
	public void setCanOverride(boolean canOverride) {
		this.canOverride = canOverride;
	}
	public boolean isCanReject() {
		return canReject;
	}
	public void setCanReject(boolean canReject) {
		this.canReject = canReject;
	}
	public boolean isCanApprove() {
		return canApprove;
	}
	public void setCanApprove(boolean canApprove) {
		this.canApprove = canApprove;
	}
	public boolean isCanForward() {
		return canForward;
	}
	public void setCanForward(boolean canForward) {
		this.canForward = canForward;
	}
	private List<ClaimApplicationItemForm> claimapplicationItems;
	private List<ClaimApplicationWorkflowForm> claimWorkflows;
	
	
	
	
	public List<ClaimApplicationWorkflowForm> getClaimWorkflows() {
		return claimWorkflows;
	}
	public void setClaimWorkflows(List<ClaimApplicationWorkflowForm> claimWorkflows) {
		this.claimWorkflows = claimWorkflows;
	}
	public List<ClaimApplicationItemForm> getClaimapplicationItems() {
		return claimapplicationItems;
	}
	public void setClaimapplicationItems(
			List<ClaimApplicationItemForm> claimapplicationItems) {
		this.claimapplicationItems = claimapplicationItems;
	}
	public Long getClaimNumber() {
		return claimNumber;
	}
	public void setClaimNumber(Long claimNumber) {
		this.claimNumber = claimNumber;
	}
	public String getClaimFormName() {
		return claimFormName;
	}
	public void setClaimFormName(String claimFormName) {
		this.claimFormName = claimFormName;
	}
	public String getClaimTemplateName() {
		return claimTemplateName;
	}
	public void setClaimTemplateName(String claimTemplateName) {
		this.claimTemplateName = claimTemplateName;
	}
	public Long getClaimApplicationId() {
		return claimApplicationId;
	}
	public void setClaimApplicationId(Long claimApplicationId) {
		this.claimApplicationId = claimApplicationId;
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
	public Long getClaimTemplateId() {
		return claimTemplateId;
	}
	public void setClaimTemplateId(Long claimTemplateId) {
		this.claimTemplateId = claimTemplateId;
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
	public Integer getTotalItems() {
		return totalItems;
	}
	public void setTotalItems(Integer totalItems) {
		this.totalItems = totalItems;
	}
	public BigDecimal getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}
	public Long getClaimStatusId() {
		return claimStatusId;
	}
	public void setClaimStatusId(Long claimStatusId) {
		this.claimStatusId = claimStatusId;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	
	

}
