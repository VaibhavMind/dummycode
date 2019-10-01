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
 * The persistent class for the Employee_Password_Change_History database table.
 * 
 */
@Entity
@Table(name = "Employee_Password_Change_History")
public class EmployeePasswordChangeHistory implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Emp_Pwd_Change_History_ID")
	private long empPwdChangeHistoryId;

	@Column(name = "Change_Date")
	private Timestamp changeDate;

	@Column(name = "Changed_Password")
	private String changedPassword;

	@Column(name = "Salt")
	private String salt;

	 
	@ManyToOne
	@JoinColumn(name = "Employee_ID")
	private Employee employee;

	public EmployeePasswordChangeHistory() {
	}

	public long getEmpPwdChangeHistoryId() {
		return this.empPwdChangeHistoryId;
	}

	public void setEmpPwdChangeHistoryId(long empPwdChangeHistoryId) {
		this.empPwdChangeHistoryId = empPwdChangeHistoryId;
	}

	public Timestamp getChangeDate() {
		return this.changeDate;
	}

	public void setChangeDate(Timestamp changeDate) {
		this.changeDate = changeDate;
	}

	public String getChangedPassword() {
		return this.changedPassword;
	}

	public void setChangedPassword(String changedPassword) {
		this.changedPassword = changedPassword;
	}

	public Employee getEmployee() {
		return this.employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

}