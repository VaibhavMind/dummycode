package com.payasia.dao.bean;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * The persistent class for the App_Code_Master database table.
 * 
 */
@Entity
@Table(name = "Leave_Grant_Batch_Detail")
public class LeaveGrantBatchDetail implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Leave_Grant_Batch_Detail_ID")
	private long leaveGrantBatchDetailId;

	@ManyToOne
	@JoinColumn(name = "Leave_Grant_Batch_ID")
	private LeaveGrantBatch leaveGrantBatch;

	@ManyToOne
	@JoinColumn(name = "Leave_Scheme_Type_ID")
	private LeaveSchemeType leaveSchemeType;

	@Column(name = "From_Period")
	private Timestamp fromPeriod;

	@Column(name = "To_Period")
	private Timestamp toPeriod;

	@Column(name = "Employees_Count")
	private long employeesCount;

	@Column(name = "Deleted_Date")
	private Timestamp deletedDate;

	 
	@OneToMany(mappedBy = "leaveGrantBatchDetail", cascade = { CascadeType.REMOVE })
	private Set<LeaveGrantBatchEmployeeDetail> leaveGrantBatchEmployeeDetails;

	public LeaveGrantBatchDetail() {
	}

	public long getLeaveGrantBatchDetailId() {
		return leaveGrantBatchDetailId;
	}

	public void setLeaveGrantBatchDetailId(long leaveGrantBatchDetailId) {
		this.leaveGrantBatchDetailId = leaveGrantBatchDetailId;
	}

	public LeaveGrantBatch getLeaveGrantBatch() {
		return leaveGrantBatch;
	}

	public void setLeaveGrantBatch(LeaveGrantBatch leaveGrantBatch) {
		this.leaveGrantBatch = leaveGrantBatch;
	}

	public LeaveSchemeType getLeaveSchemeType() {
		return leaveSchemeType;
	}

	public void setLeaveSchemeType(LeaveSchemeType leaveSchemeType) {
		this.leaveSchemeType = leaveSchemeType;
	}

	public Timestamp getFromPeriod() {
		return fromPeriod;
	}

	public void setFromPeriod(Timestamp fromPeriod) {
		this.fromPeriod = fromPeriod;
	}

	public Timestamp getToPeriod() {
		return toPeriod;
	}

	public void setToPeriod(Timestamp toPeriod) {
		this.toPeriod = toPeriod;
	}

	public long getEmployeesCount() {
		return employeesCount;
	}

	public void setEmployeesCount(long employeesCount) {
		this.employeesCount = employeesCount;
	}

	public Set<LeaveGrantBatchEmployeeDetail> getLeaveGrantBatchEmployeeDetails() {
		return leaveGrantBatchEmployeeDetails;
	}

	public void setLeaveGrantBatchEmployeeDetails(
			Set<LeaveGrantBatchEmployeeDetail> leaveGrantBatchEmployeeDetails) {
		this.leaveGrantBatchEmployeeDetails = leaveGrantBatchEmployeeDetails;
	}

	public Timestamp getDeletedDate() {
		return deletedDate;
	}

	public void setDeletedDate(Timestamp deletedDate) {
		this.deletedDate = deletedDate;
	}

}