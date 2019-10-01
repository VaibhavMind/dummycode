package com.payasia.dao.bean;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * The persistent class for the Leave_Batch_Master database table.
 * 
 */
@Entity
@Table(name = "Leave_Grant_Batch")
public class LeaveGrantBatch extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name = "Leave_Grant_Batch_ID")
	private long leaveGrantBatchId;

	@Column(name = "Batch_Number")
	private long batchNumber;

	@Column(name = "Batch_Desc")
	private String batchDesc;

	@Column(name = "Batch_Date")
	private Timestamp batchDate;

	@Column(name = "Deleted_Date")
	private Timestamp deletedDate;

	 
	@ManyToOne
	@JoinColumn(name = "Company_ID")
	private Company company;

	 
	@OneToMany(mappedBy = "leaveGrantBatch")
	private Set<LeaveGrantBatchDetail> leaveGrantBatchDetails;

	public LeaveGrantBatch() {
	}

	public long getLeaveGrantBatchId() {
		return leaveGrantBatchId;
	}

	public void setLeaveGrantBatchId(long leaveGrantBatchId) {
		this.leaveGrantBatchId = leaveGrantBatchId;
	}

	public long getBatchNumber() {
		return batchNumber;
	}

	public void setBatchNumber(long batchNumber) {
		this.batchNumber = batchNumber;
	}

	public String getBatchDesc() {
		return batchDesc;
	}

	public void setBatchDesc(String batchDesc) {
		this.batchDesc = batchDesc;
	}

	public Timestamp getBatchDate() {
		return batchDate;
	}

	public void setBatchDate(Timestamp batchDate) {
		this.batchDate = batchDate;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public Set<LeaveGrantBatchDetail> getLeaveGrantBatchDetails() {
		return leaveGrantBatchDetails;
	}

	public void setLeaveGrantBatchDetails(
			Set<LeaveGrantBatchDetail> leaveGrantBatchDetails) {
		this.leaveGrantBatchDetails = leaveGrantBatchDetails;
	}

	public Timestamp getDeletedDate() {
		return deletedDate;
	}

	public void setDeletedDate(Timestamp deletedDate) {
		this.deletedDate = deletedDate;
	}
}