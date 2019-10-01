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
 * The persistent class for the Emp_Number_History database table.
 * 
 */
@Entity
@Table(name = "Emp_Number_History")
public class EmpNumberHistory extends CompanyBaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Emp_No_History_ID")
	private long empNoHistoryId;

	@Column(name = "Changed_Date")
	private Timestamp changedDate;

	@Column(name = "Changed_Emp_No")
	private String changedEmpNo;

	@Column(name = "Prev_Emp_No")
	private String prevEmpNo;

	@Column(name = "Reason")
	private String reason;

	 
	@ManyToOne
	@JoinColumn(name = "Employee_ID")
	private Employee employee1;

	 
	@ManyToOne
	@JoinColumn(name = "Changed_By")
	private Employee employee2;

	public EmpNumberHistory() {
	}

	public long getEmpNoHistoryId() {
		return this.empNoHistoryId;
	}

	public void setEmpNoHistoryId(long empNoHistoryId) {
		this.empNoHistoryId = empNoHistoryId;
	}

	public Timestamp getChangedDate() {
		return this.changedDate;
	}

	public void setChangedDate(Timestamp changedDate) {
		this.changedDate = changedDate;
	}

	public String getChangedEmpNo() {
		return this.changedEmpNo;
	}

	public void setChangedEmpNo(String changedEmpNo) {
		this.changedEmpNo = changedEmpNo;
	}

	public String getPrevEmpNo() {
		return this.prevEmpNo;
	}

	public void setPrevEmpNo(String prevEmpNo) {
		this.prevEmpNo = prevEmpNo;
	}

	public String getReason() {
		return this.reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public Employee getEmployee1() {
		return this.employee1;
	}

	public void setEmployee1(Employee employee1) {
		this.employee1 = employee1;
	}

	public Employee getEmployee2() {
		return this.employee2;
	}

	public void setEmployee2(Employee employee2) {
		this.employee2 = employee2;
	}

}