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
@Table(name = "Employee_Claim_Template")
public class EmployeeClaimTemplate extends CompanyBaseEntity implements
		Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Employee_Claim_Template_ID")
	private long employeeClaimTemplateId;

	@Column(name = "Start_Date")
	private Timestamp startDate;

	@Column(name = "End_Date")
	private Timestamp endDate;

	 
	@ManyToOne
	@JoinColumn(name = "Claim_Template_ID")
	private ClaimTemplate claimTemplate;

	 
	@ManyToOne
	@JoinColumn(name = "Employee_ID")
	private Employee employee;

	 
	@OneToMany(mappedBy = "employeeClaimTemplate", cascade = { CascadeType.REMOVE })
	private Set<EmployeeClaimReviewer> employeeClaimReviewers;

	 
	@OneToMany(mappedBy = "employeeClaimTemplate", cascade = { CascadeType.REMOVE })
	private Set<EmployeeClaimTemplateItem> employeeClaimTemplateItems;

	 
	@OneToMany(mappedBy = "employeeClaimTemplate")
	private Set<ClaimApplication> claimApplications;

	 
	@OneToMany(mappedBy = "employeeClaimTemplate", cascade = { CascadeType.REMOVE })
	private Set<EmployeeClaimAdjustment> employeeClaimAdjustments;

	public EmployeeClaimTemplate() {
	}

	public long getEmployeeClaimTemplateId() {
		return employeeClaimTemplateId;
	}

	public void setEmployeeClaimTemplateId(long employeeClaimTemplateId) {
		this.employeeClaimTemplateId = employeeClaimTemplateId;
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

	public ClaimTemplate getClaimTemplate() {
		return claimTemplate;
	}

	public void setClaimTemplate(ClaimTemplate claimTemplate) {
		this.claimTemplate = claimTemplate;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public Set<EmployeeClaimReviewer> getEmployeeClaimReviewers() {
		return employeeClaimReviewers;
	}

	public void setEmployeeClaimReviewers(
			Set<EmployeeClaimReviewer> employeeClaimReviewers) {
		this.employeeClaimReviewers = employeeClaimReviewers;
	}

	public Set<EmployeeClaimTemplateItem> getEmployeeClaimTemplateItems() {
		return employeeClaimTemplateItems;
	}

	public void setEmployeeClaimTemplateItems(
			Set<EmployeeClaimTemplateItem> employeeClaimTemplateItems) {
		this.employeeClaimTemplateItems = employeeClaimTemplateItems;
	}

	public Set<ClaimApplication> getClaimApplications() {
		return claimApplications;
	}

	public void setClaimApplications(Set<ClaimApplication> claimApplications) {
		this.claimApplications = claimApplications;
	}

	public Set<EmployeeClaimAdjustment> getEmployeeClaimAdjustments() {
		return employeeClaimAdjustments;
	}

	public void setEmployeeClaimAdjustments(
			Set<EmployeeClaimAdjustment> employeeClaimAdjustments) {
		this.employeeClaimAdjustments = employeeClaimAdjustments;
	}

}