package com.payasia.dao.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * The persistent class for the Claim_Application_Item_Workflow database table.
 * 
 */
@Entity
@Table(name = "Claim_Application_Item_Workflow")
public class ClaimApplicationItemWorkflow extends CompanyUpdatedBaseEntity
		implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // Changes by Gaurav
	@Column(name = "Claim_Application_Item_Workflow_ID")
	private long claimApplicationItemWorkflowId;

	@Column(name = "Overridden_Amount")
	private BigDecimal overriddenAmount;

	@Column(name = "Overridden_Tax_Amount")
	private BigDecimal overriddenTaxAmount;

	@Column(name = "Created_Date")
	private Timestamp createdDate;

	@Column(name = "Remarks")
	private String remarks;

	 
	@ManyToOne
	@JoinColumn(name = "Claim_Application_Item_ID")
	private ClaimApplicationItem claimApplicationItem;

	 
	@ManyToOne
	@JoinColumn(name = "Created_By")
	private Employee createdBy;

	public Employee getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Employee createdBy) {
		this.createdBy = createdBy;
	}

	@ManyToOne
	@JoinColumn(name = "Action")
	private AppCodeMaster claimItemWorkflowAction;

	@ManyToOne
	@JoinColumn(name = "Status")
	private AppCodeMaster claimItemWorkflowStatus;

	public ClaimApplicationItemWorkflow() {
	}

	public long getClaimApplicationItemWorkflowId() {
		return this.claimApplicationItemWorkflowId;
	}

	public void setClaimApplicationItemWorkflowId(
			long claimApplicationItemWorkflowId) {
		this.claimApplicationItemWorkflowId = claimApplicationItemWorkflowId;
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

	public ClaimApplicationItem getClaimApplicationItem() {
		return claimApplicationItem;
	}

	public void setClaimApplicationItem(
			ClaimApplicationItem claimApplicationItem) {
		this.claimApplicationItem = claimApplicationItem;
	}

	public AppCodeMaster getClaimItemWorkflowAction() {
		return claimItemWorkflowAction;
	}

	public void setClaimItemWorkflowAction(AppCodeMaster claimItemWorkflowAction) {
		this.claimItemWorkflowAction = claimItemWorkflowAction;
	}

	public AppCodeMaster getClaimItemWorkflowStatus() {
		return claimItemWorkflowStatus;
	}

	public void setClaimItemWorkflowStatus(AppCodeMaster claimItemWorkflowStatus) {
		this.claimItemWorkflowStatus = claimItemWorkflowStatus;
	}

	public Timestamp getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Timestamp createdDate) {
		this.createdDate = createdDate;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

}