package com.payasia.common.form;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

import com.payasia.dao.bean.LeaveYearEndBatch;

public class LeaveYearEndEmployeeDetailForm implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long leaveYearEndEmployeeDetailId;
	private LeaveYearEndBatch leaveYearEndBatch;
	private String employee;
	private BigDecimal balance;
	private BigDecimal encashed;
	private BigDecimal lapsed;
	private BigDecimal closingBalance;
	private Timestamp deletedDate;
	public long getLeaveYearEndEmployeeDetailId() {
		return leaveYearEndEmployeeDetailId;
	}
	public void setLeaveYearEndEmployeeDetailId(long leaveYearEndEmployeeDetailId) {
		this.leaveYearEndEmployeeDetailId = leaveYearEndEmployeeDetailId;
	}
	public LeaveYearEndBatch getLeaveYearEndBatch() {
		return leaveYearEndBatch;
	}
	public void setLeaveYearEndBatch(LeaveYearEndBatch leaveYearEndBatch) {
		this.leaveYearEndBatch = leaveYearEndBatch;
	}
	public String getEmployee() {
		return employee;
	}
	public void setEmployee(String employee) {
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
