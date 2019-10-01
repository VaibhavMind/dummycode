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
 * The persistent class for the Employee_Leave_Scheme database table.
 * 
 */
@Entity
@Table(name = "Employee_Leave_Scheme")
public class EmployeeLeaveScheme extends CompanyBaseEntity implements
		Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Employee_Leave_Scheme_ID")
	private long employeeLeaveSchemeId;

	@Column(name = "Start_Date")
	private Timestamp startDate;

	@Column(name = "End_Date")
	private Timestamp endDate;

	 
	@OneToMany(mappedBy = "employeeLeaveScheme", cascade = { CascadeType.REMOVE })
	private Set<EmployeeLeaveReviewer> employeeLeaveReviewers;

	 
	@ManyToOne
	@JoinColumn(name = "Employee_ID")
	private Employee employee;

	 
	@ManyToOne
	@JoinColumn(name = "Leave_Scheme_ID")
	private LeaveScheme leaveScheme;

	 
	@OneToMany(mappedBy = "employeeLeaveScheme", cascade = { CascadeType.REMOVE })
	private Set<EmployeeLeaveSchemeType> employeeLeaveSchemeTypes;

	public EmployeeLeaveScheme() {
	}

	public long getEmployeeLeaveSchemeId() {
		return this.employeeLeaveSchemeId;
	}

	public void setEmployeeLeaveSchemeId(long employeeLeaveSchemeId) {
		this.employeeLeaveSchemeId = employeeLeaveSchemeId;
	}

	public Employee getEmployee() {
		return this.employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public LeaveScheme getLeaveScheme() {
		return this.leaveScheme;
	}

	public void setLeaveScheme(LeaveScheme leaveScheme) {
		this.leaveScheme = leaveScheme;
	}

	public Set<EmployeeLeaveSchemeType> getEmployeeLeaveSchemeTypes() {
		return this.employeeLeaveSchemeTypes;
	}

	public void setEmployeeLeaveSchemeTypes(
			Set<EmployeeLeaveSchemeType> employeeLeaveSchemeTypes) {
		this.employeeLeaveSchemeTypes = employeeLeaveSchemeTypes;
	}

	public Timestamp getStartDate() {
		return startDate;
	}

	public void setStartDate(Timestamp startDate) {
		this.startDate = startDate;
	}

	public Timestamp getEndDate() {
		return endDate;
	}

	public void setEndDate(Timestamp endDate) {
		this.endDate = endDate;
	}

	public Set<EmployeeLeaveReviewer> getEmployeeLeaveReviewers() {
		return employeeLeaveReviewers;
	}

	public void setEmployeeLeaveReviewers(
			Set<EmployeeLeaveReviewer> employeeLeaveReviewers) {
		this.employeeLeaveReviewers = employeeLeaveReviewers;
	}

}