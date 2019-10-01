package com.payasia.dao.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * The persistent class for the Employee_Photo database table.
 * 
 */
@Entity
@Table(name = "Employee_Login_Detail")
public class EmployeeLoginDetail extends CompanyBaseEntity implements
		Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Employee_Login_Detail_ID")
	private long employeeLoginDetailId;

	@Column(name = "Login_Name")
	private String loginName;
	@Column(name = "Password")
	private String password;

	@Column(name = "Password_Sent")
	private boolean passwordSent = false;

	@Column(name = "Invalid_Login_Attempt")
	private Integer invalidLoginAttempt;

	@Column(name = "Password_Reset")
	private Boolean passwordReset = true;

	@Column(name = "Salt")
	private String salt;

	 
	@ManyToOne
	@JoinColumn(name = "Employee_ID")
	private Employee employee;

	public EmployeeLoginDetail() {
	}

	public long getEmployeeLoginDetailId() {
		return employeeLoginDetailId;
	}

	public void setEmployeeLoginDetailId(long employeeLoginDetailId) {
		this.employeeLoginDetailId = employeeLoginDetailId;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isPasswordSent() {
		return passwordSent;
	}

	public void setPasswordSent(boolean passwordSent) {
		this.passwordSent = passwordSent;
	}

	public Integer getInvalidLoginAttempt() {
		return invalidLoginAttempt;
	}

	public void setInvalidLoginAttempt(Integer invalidLoginAttempt) {
		this.invalidLoginAttempt = invalidLoginAttempt;
	}

	public Boolean getPasswordReset() {
		return passwordReset;
	}

	public void setPasswordReset(Boolean passwordReset) {
		this.passwordReset = passwordReset;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

}