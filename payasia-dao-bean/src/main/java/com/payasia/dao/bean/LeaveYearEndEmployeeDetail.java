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
 * The persistent class for the App_Code_Master database table.
 * 
 */
@Entity
@Table(name = "Leave_Year_End_Employee_Detail")
public class LeaveYearEndEmployeeDetail implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Leave_Year_End_Employee_Detail_ID")
	private long leaveYearEndEmployeeDetailId;

	@ManyToOne
	@JoinColumn(name = "Leave_Year_End_Batch_ID")
	private LeaveYearEndBatch leaveYearEndBatch;

	@ManyToOne
	@JoinColumn(name = "Employee_ID")
	private Employee employee;

	@Column(name = "Balance")
	private BigDecimal balance;

	@Column(name = "Encashed")
	private BigDecimal encashed;

	@Column(name = "Lapsed")
	private BigDecimal lapsed;

	@Column(name = "Closing_Balance")
	private BigDecimal closingBalance;

	@Column(name = "Deleted_Date")
	private Timestamp deletedDate;

	public LeaveYearEndEmployeeDetail() {
	}

	public long getLeaveYearEndEmployeeDetailId() {
		return leaveYearEndEmployeeDetailId;
	}

	public void setLeaveYearEndEmployeeDetailId(
			long leaveYearEndEmployeeDetailId) {
		this.leaveYearEndEmployeeDetailId = leaveYearEndEmployeeDetailId;
	}

	public LeaveYearEndBatch getLeaveYearEndBatch() {
		return leaveYearEndBatch;
	}

	public void setLeaveYearEndBatch(LeaveYearEndBatch leaveYearEndBatch) {
		this.leaveYearEndBatch = leaveYearEndBatch;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public BigDecimal getEncashed() {
		return encashed;
	}

	public void setEncashed(BigDecimal encashed) {
		this.encashed = encashed;
	}

	public BigDecimal getLapsed() {
		return lapsed;
	}

	public void setLapsed(BigDecimal lapsed) {
		this.lapsed = lapsed;
	}

	public BigDecimal getClosingBalance() {
		return closingBalance;
	}

	public void setClosingBalance(BigDecimal closingBalance) {
		this.closingBalance = closingBalance;
	}

	public Timestamp getDeletedDate() {
		return deletedDate;
	}

	public void setDeletedDate(Timestamp deletedDate) {
		this.deletedDate = deletedDate;
	}

}