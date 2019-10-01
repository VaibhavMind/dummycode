package com.payasia.dao.bean;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The persistent class for the Claim_Batch_Master database table.
 * 
 */
@Entity
@Table(name = "Claim_Batch_Master")
public class ClaimBatchMaster extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name = "Claim_Batch_ID")
	private long claimBatchID;

	@Column(name = "Claim_Batch_Desc")
	private String claimBatchDesc;

	@Column(name = "End_Date")
	private Timestamp endDate;

	@Column(name = "Start_Date")
	private Timestamp startDate;

	@Column(name = "Paid")
	private Boolean paid;

	@Column(name = "Paid_Date")
	@Temporal(TemporalType.DATE)
	private Date paidDate;

	@ManyToOne
	@JoinColumn(name = "Company_ID")
	private Company company;

	public ClaimBatchMaster() {
	}

	public long getClaimBatchID() {
		return this.claimBatchID;
	}

	public void setClaimBatchID(long claimBatchID) {
		this.claimBatchID = claimBatchID;
	}

	public String getClaimBatchDesc() {
		return this.claimBatchDesc;
	}

	public void setClaimBatchDesc(String claimBatchDesc) {
		this.claimBatchDesc = claimBatchDesc;
	}

	public Timestamp getEndDate() {
		return this.endDate;
	}

	public void setEndDate(Timestamp endDate) {
		this.endDate = endDate;
	}

	public Timestamp getStartDate() {
		return this.startDate;
	}

	public void setStartDate(Timestamp startDate) {
		this.startDate = startDate;
	}

	public Company getCompany() {
		return this.company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public Boolean getPaid() {
		return paid;
	}

	public void setPaid(Boolean paid) {
		this.paid = paid;
	}

	public Date getPaidDate() {
		return paidDate;
	}

	public void setPaidDate(Date paidDate) {
		this.paidDate = paidDate;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}