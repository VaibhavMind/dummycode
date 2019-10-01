package com.payasia.dao.bean;

import java.io.Serializable;
import java.math.BigDecimal;
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
 * The persistent class for the Employee_Leave_Scheme_Type database table.
 * 
 */
@Entity
@Table(name = "Employee_Leave_Scheme_Type")
public class EmployeeLeaveSchemeType extends CompanyBaseEntity implements
		Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Employee_Leave_Scheme_Type_ID")
	private long employeeLeaveSchemeTypeId;

	@Column(name = "Balance")
	private BigDecimal balance;

	@Column(name = "Carried_Forward")
	private BigDecimal carriedForward;

	@Column(name = "Credited")
	private BigDecimal credited;

	@Column(name = "Encashed")
	private BigDecimal encashed;

	@Column(name = "Forfeited")
	private BigDecimal forfeited;

	@Column(name = "Pending")
	private BigDecimal pending;

	@Column(name = "Taken")
	private BigDecimal taken;

	@Column(name = "Active")
	private Boolean active = true;

	 
	@ManyToOne
	@JoinColumn(name = "Employee_Leave_Scheme_ID")
	private EmployeeLeaveScheme employeeLeaveScheme;

	 
	@ManyToOne
	@JoinColumn(name = "Leave_Scheme_Type_ID")
	private LeaveSchemeType leaveSchemeType;

	 
	@OneToMany(mappedBy = "employeeLeaveSchemeType", cascade = { CascadeType.REMOVE })
	private Set<EmployeeLeaveSchemeTypeHistory> employeeLeaveSchemeTypeHistories;

	 
	@OneToMany(mappedBy = "employeeLeaveSchemeType")
	private Set<EmployeeLeaveReviewer> employeeLeaveReviewers;

	 
	@OneToMany(mappedBy = "employeeLeaveSchemeType")
	private Set<LeaveApplication> leaveApplications;

	 
	@OneToMany(mappedBy = "employeeLeaveSchemeType")
	private Set<EmployeeLeaveDistribution> employeeLeaveDistributions;

	public EmployeeLeaveSchemeType() {
	}

	public long getEmployeeLeaveSchemeTypeId() {
		return this.employeeLeaveSchemeTypeId;
	}

	public void setEmployeeLeaveSchemeTypeId(long employeeLeaveSchemeTypeId) {
		this.employeeLeaveSchemeTypeId = employeeLeaveSchemeTypeId;
	}

	public BigDecimal getBalance() {
		return this.balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public BigDecimal getCarriedForward() {
		return this.carriedForward;
	}

	public void setCarriedForward(BigDecimal carriedForward) {
		this.carriedForward = carriedForward;
	}

	public BigDecimal getCredited() {
		return this.credited;
	}

	public void setCredited(BigDecimal credited) {
		this.credited = credited;
	}

	public BigDecimal getEncashed() {
		return this.encashed;
	}

	public void setEncashed(BigDecimal encashed) {
		this.encashed = encashed;
	}

	public BigDecimal getForfeited() {
		return this.forfeited;
	}

	public void setForfeited(BigDecimal forfeited) {
		this.forfeited = forfeited;
	}

	public BigDecimal getPending() {
		return this.pending;
	}

	public void setPending(BigDecimal pending) {
		this.pending = pending;
	}

	public BigDecimal getTaken() {
		return this.taken;
	}

	public void setTaken(BigDecimal taken) {
		this.taken = taken;
	}

	public EmployeeLeaveScheme getEmployeeLeaveScheme() {
		return this.employeeLeaveScheme;
	}

	public void setEmployeeLeaveScheme(EmployeeLeaveScheme employeeLeaveScheme) {
		this.employeeLeaveScheme = employeeLeaveScheme;
	}

	public LeaveSchemeType getLeaveSchemeType() {
		return leaveSchemeType;
	}

	public void setLeaveSchemeType(LeaveSchemeType leaveSchemeType) {
		this.leaveSchemeType = leaveSchemeType;
	}

	public Set<EmployeeLeaveSchemeTypeHistory> getEmployeeLeaveSchemeTypeHistories() {
		return this.employeeLeaveSchemeTypeHistories;
	}

	public void setEmployeeLeaveSchemeTypeHistories(
			Set<EmployeeLeaveSchemeTypeHistory> employeeLeaveSchemeTypeHistories) {
		this.employeeLeaveSchemeTypeHistories = employeeLeaveSchemeTypeHistories;
	}

	public Set<EmployeeLeaveReviewer> getEmployeeLeaveReviewers() {
		return employeeLeaveReviewers;
	}

	public void setEmployeeLeaveReviewers(
			Set<EmployeeLeaveReviewer> employeeLeaveReviewers) {
		this.employeeLeaveReviewers = employeeLeaveReviewers;
	}

	public Set<LeaveApplication> getLeaveApplications() {
		return leaveApplications;
	}

	public void setLeaveApplications(Set<LeaveApplication> leaveApplications) {
		this.leaveApplications = leaveApplications;
	}

	public Set<EmployeeLeaveDistribution> getEmployeeLeaveDistributions() {
		return employeeLeaveDistributions;
	}

	public void setEmployeeLeaveDistributions(
			Set<EmployeeLeaveDistribution> employeeLeaveDistributions) {
		this.employeeLeaveDistributions = employeeLeaveDistributions;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

}