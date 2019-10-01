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
@Table(name = "Leave_Year_End_Batch")
public class LeaveYearEndBatch implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name = "Leave_Year_End_Batch_ID")
	private long leaveYearEndBatchId;

	@Column(name = "Processed_Date")
	private Timestamp processedDate;

	@Column(name = "Deleted_Date")
	private Timestamp deletedDate;

	@Column(name = "Employee_Count")
	private int employeeCount;

	 
	@ManyToOne
	@JoinColumn(name = "Leave_Scheme_Type_ID")
	private LeaveSchemeType leaveSchemeType;

	 
	@ManyToOne
	@JoinColumn(name = "Company_ID")
	private Company company;

	 
	@OneToMany(mappedBy = "leaveYearEndBatch")
	private Set<LeaveYearEndEmployeeDetail> leaveYearEndEmployeeDetails;

	public LeaveYearEndBatch() {
	}

	public long getLeaveYearEndBatchId() {
		return leaveYearEndBatchId;
	}

	public void setLeaveYearEndBatchId(long leaveYearEndBatchId) {
		this.leaveYearEndBatchId = leaveYearEndBatchId;
	}

	public Timestamp getProcessedDate() {
		return processedDate;
	}

	public void setProcessedDate(Timestamp processedDate) {
		this.processedDate = processedDate;
	}

	public Timestamp getDeletedDate() {
		return deletedDate;
	}

	public void setDeletedDate(Timestamp deletedDate) {
		this.deletedDate = deletedDate;
	}

	public int getEmployeeCount() {
		return employeeCount;
	}

	public void setEmployeeCount(int employeeCount) {
		this.employeeCount = employeeCount;
	}

	public LeaveSchemeType getLeaveSchemeType() {
		return leaveSchemeType;
	}

	public void setLeaveSchemeType(LeaveSchemeType leaveSchemeType) {
		this.leaveSchemeType = leaveSchemeType;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public Set<LeaveYearEndEmployeeDetail> getLeaveYearEndEmployeeDetails() {
		return leaveYearEndEmployeeDetails;
	}

	public void setLeaveYearEndEmployeeDetails(
			Set<LeaveYearEndEmployeeDetail> leaveYearEndEmployeeDetails) {
		this.leaveYearEndEmployeeDetails = leaveYearEndEmployeeDetails;
	}

}