package com.payasia.common.form;

import java.io.Serializable;
import java.math.BigDecimal;

public class ClaimBatchForm implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5245925351642706561L;
	private long claimBatchID;
	private String description;
	private String startDate;
	private String endDate;
	private String claims;
	
	private String editable;
	private String fieldEditable;
	
	private String employeeName;
	private String employeeNumber;
	private String claimTemplate;
	private Long claimTemplateId;
	private Long claimApplicationId;
	private String createdDate;
	private BigDecimal totalAmount;
	private Boolean paid;
	private String paidStatus;
	private boolean showPaidStatusForClaimBatch;
	
	private String paidDate;
	
	
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

	public String getClaimTemplate() {
		return claimTemplate;
	}

	public void setClaimTemplate(String claimTemplate) {
		this.claimTemplate = claimTemplate;
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


	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public long getClaimBatchID() {
		return claimBatchID;
	}

	public void setClaimBatchID(long claimBatchID) {
		this.claimBatchID = claimBatchID;
	}

	public String getClaims() {
		return claims;
	}

	public void setClaims(String claims) {
		this.claims = claims;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public Long getClaimApplicationId() {
		return claimApplicationId;
	}

	public void setClaimApplicationId(Long claimApplicationId) {
		this.claimApplicationId = claimApplicationId;
	}

	public String getEditable() {
		return editable;
	}

	public void setEditable(String editable) {
		this.editable = editable;
	}

	public Boolean getPaid() {
		return paid;
	}

	public void setPaid(Boolean paid) {
		this.paid = paid;
	}

	public String getPaidStatus() {
		return paidStatus;
	}

	public void setPaidStatus(String paidStatus) {
		this.paidStatus = paidStatus;
	}

	public boolean isShowPaidStatusForClaimBatch() {
		return showPaidStatusForClaimBatch;
	}

	public void setShowPaidStatusForClaimBatch(boolean showPaidStatusForClaimBatch) {
		this.showPaidStatusForClaimBatch = showPaidStatusForClaimBatch;
	}

	public String getFieldEditable() {
		return fieldEditable;
	}

	public void setFieldEditable(String fieldEditable) {
		this.fieldEditable = fieldEditable;
	}

	public String getPaidDate() {
		return paidDate;
	}

	public void setPaidDate(String paidDate) {
		this.paidDate = paidDate;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}


	

		
	

}
