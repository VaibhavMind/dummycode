package com.payasia.common.form;

import java.io.Serializable;
import java.math.BigDecimal;

public class ClaimApplicationItemWorkflowForm implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4778451626680286207L;
	
	private Long claimApplicationItemWorkflowID;
	private Long claimApplicationItemID	;
	private Long action;
	private BigDecimal overriddenAmount;	
	private BigDecimal overriddenTaxAmount;
	private BigDecimal amountBeforeTax;
	private BigDecimal status;
	private String statusName;
	private String statusMode;
	private String remarks;
	private  String createdDate;
	private Long created_By;
	private String createdByName;

	private byte[] image;
	
	public String getStatusMode() {
		return statusMode;
	}
	public void setStatusMode(String statusMode) {
		this.statusMode = statusMode;
	}
	public String getStatusName() {
		return statusName;
	}
	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}
	public String getCreatedByName() {
		return createdByName;
	}
	public void setCreatedByName(String createdByName) {
		this.createdByName = createdByName;
	}
	public BigDecimal getAmountBeforeTax() {
		return amountBeforeTax;
	}
	public void setAmountBeforeTax(BigDecimal amountBeforeTax) {
		this.amountBeforeTax = amountBeforeTax;
	}
	public Long getClaimApplicationItemWorkflowID() {
		return claimApplicationItemWorkflowID;
	}
	public void setClaimApplicationItemWorkflowID(
			Long claimApplicationItemWorkflowID) {
		this.claimApplicationItemWorkflowID = claimApplicationItemWorkflowID;
	}
	public Long getClaimApplicationItemID() {
		return claimApplicationItemID;
	}
	public void setClaimApplicationItemID(Long claimApplicationItemID) {
		this.claimApplicationItemID = claimApplicationItemID;
	}
	public Long getAction() {
		return action;
	}
	public void setAction(Long action) {
		this.action = action;
	}
	public BigDecimal getOverriddenAmount() {
		return overriddenAmount;
	}
	public void setOverriddenAmount(BigDecimal overriddenAmount) {
		this.overriddenAmount = overriddenAmount;
	}
	public BigDecimal getOverriddenTaxAmount() {
		return overriddenTaxAmount;
	}
	public void setOverriddenTaxAmount(BigDecimal overriddenTaxAmount) {
		this.overriddenTaxAmount = overriddenTaxAmount;
	}
	public BigDecimal getStatus() {
		return status;
	}
	public void setStatus(BigDecimal status) {
		this.status = status;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	public Long getCreated_By() {
		return created_By;
	}
	public void setCreated_By(Long created_By) {
		this.created_By = created_By;
	}
	public byte[] getImage() {
		return image;
	}
	public void setImage(byte[] image) {
		this.image = image;
	}
	
}
