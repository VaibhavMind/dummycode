package com.payasia.dao.bean;

import java.io.Serializable;
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
 * The persistent class for the App_Code_Master database table.
 * 
 */
@Entity
@Table(name = "Leave_Grant_Batch_Employee_Detail")
public class LeaveGrantBatchEmployeeDetail extends CompanyBaseEntity implements
		Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Leave_Grant_Batch_Employee_Detail_ID")
	private long leaveGrantBatchEmployeeDetailId;

	@ManyToOne
	@JoinColumn(name = "Leave_Grant_Batch_Detail_ID")
	private LeaveGrantBatchDetail leaveGrantBatchDetail;

	@ManyToOne
	@JoinColumn(name = "Employee_ID")
	private Employee employee;

	@Column(name = "Granted_Days")
	private float grantedDays;

	@Column(name = "Deleted_Date")
	private Timestamp deletedDate;

	public LeaveGrantBatchEmployeeDetail() {
	}

	public long getLeaveGrantBatchEmployeeDetailId() {
		return leaveGrantBatchEmployeeDetailId;
	}

	public void setLeaveGrantBatchEmployeeDetailId(
			long leaveGrantBatchEmployeeDetailId) {
		this.leaveGrantBatchEmployeeDetailId = leaveGrantBatchEmployeeDetailId;
	}

	public LeaveGrantBatchDetail getLeaveGrantBatchDetail() {
		return leaveGrantBatchDetail;
	}

	public void setLeaveGrantBatchDetail(
			LeaveGrantBatchDetail leaveGrantBatchDetail) {
		this.leaveGrantBatchDetail = leaveGrantBatchDetail;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public float getGrantedDays() {
		return grantedDays;
	}

	public void setGrantedDays(float grantedDays) {
		this.grantedDays = grantedDays;
	}

	public Timestamp getDeletedDate() {
		return deletedDate;
	}

	public void setDeletedDate(Timestamp deletedDate) {
		this.deletedDate = deletedDate;
	}

}