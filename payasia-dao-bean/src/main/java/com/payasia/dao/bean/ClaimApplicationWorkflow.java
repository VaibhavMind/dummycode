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
 * The persistent class for the Claim_Application_Workflow database table.
 * 
 */
@Entity
@Table(name = "Claim_Application_Workflow")
public class ClaimApplicationWorkflow implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "Claim_Application_Workflow_ID")
	private long claimApplicationWorkflowId;

	@Column(name = "Created_Date")
	private Timestamp createdDate;

	@Column(name = "Email_CC")
	private String emailCC;

	@Column(name = "Forward_To")
	private String forwardTo;

	@Column(name = "Remarks")
	private String remarks;

	@Column(name = "Total_Amount")
	private BigDecimal totalAmount;

	 
	@ManyToOne
	@JoinColumn(name = "Claim_Application_ID")
	private ClaimApplication claimApplication;

	 
	@ManyToOne
	@JoinColumn(name = "Claim_Status_ID")
	private ClaimStatusMaster claimStatusMaster;

	 
	@ManyToOne
	@JoinColumn(name = "Created_By")
	private Employee employee;

	public ClaimApplicationWorkflow() {
	}

	public long getClaimApplicationWorkflowId() {
		return this.claimApplicationWorkflowId;
	}

	public void setClaimApplicationWorkflowId(long claimApplicationWorkflowId) {
		this.claimApplicationWorkflowId = claimApplicationWorkflowId;
	}

	public Timestamp getCreatedDate() {
		return this.createdDate;
	}

	public void setCreatedDate(Timestamp createdDate) {
		this.createdDate = createdDate;
	}

	public String getEmailCC() {
		return this.emailCC;
	}

	public void setEmailCC(String emailCC) {
		this.emailCC = emailCC;
	}

	public String getForwardTo() {
		return this.forwardTo;
	}

	public void setForwardTo(String forwardTo) {
		this.forwardTo = forwardTo;
	}

	public String getRemarks() {
		return this.remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public BigDecimal getTotalAmount() {
		return this.totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public ClaimApplication getClaimApplication() {
		return this.claimApplication;
	}

	public void setClaimApplication(ClaimApplication claimApplication) {
		this.claimApplication = claimApplication;
	}

	public ClaimStatusMaster getClaimStatusMaster() {
		return this.claimStatusMaster;
	}

	public void setClaimStatusMaster(ClaimStatusMaster claimStatusMaster) {
		this.claimStatusMaster = claimStatusMaster;
	}

	public Employee getEmployee() {
		return this.employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

}