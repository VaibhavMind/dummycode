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
 * The persistent class for the Employee_Login_History database table.
 * 
 */
@Entity
@Table(name = "Employee_Login_History")
public class EmployeeLoginHistory implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Emp_Login_History_ID")
	private long empLoginHistoryId;

	@Column(name = "Logged_In_Date")
	private Timestamp loggedInDate;

	@Column(name = "Status")
	private String status;

	@Column(name = "IP_Address")
	private String ipAddress;

	@Column(name = "Login_Mode")
	private String loginMode;

	@ManyToOne
	@JoinColumn(name = "Employee_ID")
	private Employee employee;

	public EmployeeLoginHistory() {
	}

	public long getEmpLoginHistoryId() {
		return this.empLoginHistoryId;
	}

	public void setEmpLoginHistoryId(long empLoginHistoryId) {
		this.empLoginHistoryId = empLoginHistoryId;
	}

	public Timestamp getLoggedInDate() {
		return this.loggedInDate;
	}

	public void setLoggedInDate(Timestamp loggedInDate) {
		this.loggedInDate = loggedInDate;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Employee getEmployee() {
		return this.employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getLoginMode() {
		return loginMode;
	}

	public void setLoginMode(String loginMode) {
		this.loginMode = loginMode;
	}

}