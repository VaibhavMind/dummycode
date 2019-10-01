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
 * The persistent class for the Claim_Application database table.
 * 
 */
@Entity
@Table(name = "Claim_Application")
public class ClaimApplication extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Claim_Application_ID")
	private long claimApplicationId;

	@Column(name = "Claim_Number")
	private long claimNumber;

	@Column(name = "Remarks")
	private String remarks;

	@Column(name = "Total_Amount")
	private BigDecimal totalAmount;

	@Column(name = "Total_Items")
	private int totalItems;

	@Column(name = "Email_CC")
	private String emailCC;

	@Column(name = "Visible_To_Employee")
	private Boolean visibleToEmployee;

	 
	@ManyToOne
	@JoinColumn(name = "Claim_Status_ID")
	private ClaimStatusMaster claimStatusMaster;

	 
	@ManyToOne
	@JoinColumn(name = "Employee_Claim_Template_ID")
	private EmployeeClaimTemplate employeeClaimTemplate;

	 
	@ManyToOne
	@JoinColumn(name = "Company_ID")
	private Company company;

	 
	@ManyToOne
	@JoinColumn(name = "Employee_ID")
	private Employee employee;

	 
	@OneToMany(mappedBy = "claimApplication", cascade = { CascadeType.REMOVE })
	private Set<ClaimApplicationReviewer> claimApplicationReviewers;

	 
	@OneToMany(mappedBy = "claimApplication", cascade = { CascadeType.REMOVE })
	private Set<ClaimApplicationWorkflow> claimApplicationWorkflows;

	 
	@OneToMany(mappedBy = "claimApplication", cascade = { CascadeType.REMOVE })
	private Set<ClaimApplicationItem> claimApplicationItems;

	public ClaimApplication() {
	}

	public long getClaimApplicationId() {
		return this.claimApplicationId;
	}

	public void setClaimApplicationId(long claimApplicationId) {
		this.claimApplicationId = claimApplicationId;
	}

	public String getRemarks() {
		return this.remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public BigDecimal getTotalAmount() {
		return this.totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public int getTotalItems() {
		return this.totalItems;
	}

	public void setTotalItems(int totalItems) {
		this.totalItems = totalItems;
	}

	public ClaimStatusMaster getClaimStatusMaster() {
		return this.claimStatusMaster;
	}

	public void setClaimStatusMaster(ClaimStatusMaster claimStatusMaster) {
		this.claimStatusMaster = claimStatusMaster;
	}

	public Company getCompany() {
		return this.company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public Employee getEmployee() {
		return this.employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public Set<ClaimApplicationReviewer> getClaimApplicationReviewers() {
		return this.claimApplicationReviewers;
	}

	public void setClaimApplicationReviewers(
			Set<ClaimApplicationReviewer> claimApplicationReviewers) {
		this.claimApplicationReviewers = claimApplicationReviewers;
	}

	public Set<ClaimApplicationWorkflow> getClaimApplicationWorkflows() {
		return this.claimApplicationWorkflows;
	}

	public void setClaimApplicationWorkflows(
			Set<ClaimApplicationWorkflow> claimApplicationWorkflows) {
		this.claimApplicationWorkflows = claimApplicationWorkflows;
	}

	public long getClaimNumber() {
		return claimNumber;
	}

	public void setClaimNumber(long claimNumber) {
		this.claimNumber = claimNumber;
	}

	public EmployeeClaimTemplate getEmployeeClaimTemplate() {
		return employeeClaimTemplate;
	}

	public void setEmployeeClaimTemplate(
			EmployeeClaimTemplate employeeClaimTemplate) {
		this.employeeClaimTemplate = employeeClaimTemplate;
	}

	public Set<ClaimApplicationItem> getClaimApplicationItems() {
		return claimApplicationItems;
	}

	public void setClaimApplicationItems(
			Set<ClaimApplicationItem> claimApplicationItems) {
		this.claimApplicationItems = claimApplicationItems;
	}

	public String getEmailCC() {
		return emailCC;
	}

	public void setEmailCC(String emailCC) {
		this.emailCC = emailCC;
	}

	public Boolean getVisibleToEmployee() {
		return visibleToEmployee;
	}

	public void setVisibleToEmployee(Boolean visibleToEmployee) {
		this.visibleToEmployee = visibleToEmployee;
	}

}