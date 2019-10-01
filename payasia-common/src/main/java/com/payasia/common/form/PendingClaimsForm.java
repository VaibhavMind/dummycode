package com.payasia.common.form;

import java.io.Serializable;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.payasia.common.dto.ValidationClaimItemDTO;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PendingClaimsForm implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7704109396182098943L;
	
	private ClaimApplicationForm claimApplicationForm;
	
	private Long claimApplicationReviewerId;
	private String createdBy;
	private String createdDate;
	private String totalItems;
	private BigDecimal totalAmount;
	
	private String remarks;
	private Long claimTemplateId;
	private Long claimNumber;
	private String claimTemplateName;
	private Long createdById;
	private Long claimApplicationId;
	private String emailCC;
	private String status;
	private boolean isLundinTimesheetModule;
	private ValidationClaimItemDTO validationClaimItemDTO;
	
	private String messageCode;
	
	public Long getCreatedById() {
		return createdById;
	}


	public void setCreatedById(Long createdById) {
		this.createdById = createdById;
	}


	public Long getClaimNumber() {
		return claimNumber;
	}


	public void setClaimNumber(Long claimNumber) {
		this.claimNumber = claimNumber;
	}


	public String getClaimTemplateName() {
		return claimTemplateName;
	}


	public void setClaimTemplateName(String claimTemplateName) {
		this.claimTemplateName = claimTemplateName;
	}


	public Long getClaimTemplateId() {
		return claimTemplateId;
	}


	public void setClaimTemplateId(Long claimTemplateId) {
		this.claimTemplateId = claimTemplateId;
	}


	public String getRemarks() {
		return remarks;
	}


	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}


	public Long getClaimApplicationReviewerId() {
		
		
		return claimApplicationReviewerId;
	}
	
	
	public void setClaimApplicationReviewerId(Long claimApplicationReviewerId) {
		this.claimApplicationReviewerId = claimApplicationReviewerId;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	public String getTotalItems() {
		return totalItems;
	}


	public void setTotalItems(String totalItems) {
		this.totalItems = totalItems;
	}


	public BigDecimal getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}


	public ClaimApplicationForm getClaimApplicationForm() {
		return claimApplicationForm;
	}


	public void setClaimApplicationForm(ClaimApplicationForm claimApplicationForm) {
		this.claimApplicationForm = claimApplicationForm;
	}


	public Long getClaimApplicationId() {
		return claimApplicationId;
	}


	public void setClaimApplicationId(Long claimApplicationId) {
		this.claimApplicationId = claimApplicationId;
	}
	public String getEmailCC() {
		return emailCC;
	}


	public void setEmailCC(String emailCC) {
		this.emailCC = emailCC;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public ValidationClaimItemDTO getValidationClaimItemDTO() {
		return validationClaimItemDTO;
	}


	public void setValidationClaimItemDTO(
			ValidationClaimItemDTO validationClaimItemDTO) {
		this.validationClaimItemDTO = validationClaimItemDTO;
	}


	public boolean isLundinTimesheetModule() {
		return isLundinTimesheetModule;
	}


	public void setLundinTimesheetModule(boolean isLundinTimesheetModule) {
		this.isLundinTimesheetModule = isLundinTimesheetModule;
	}

	public String getMessageCode() {
		return messageCode;
	}


	public void setMessageCode(String messageCode) {
		this.messageCode = messageCode;
	}
	
}
