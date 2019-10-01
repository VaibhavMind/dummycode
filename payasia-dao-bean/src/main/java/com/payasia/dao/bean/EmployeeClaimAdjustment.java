package com.payasia.dao.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "Employee_Claim_Adjustment")
public class EmployeeClaimAdjustment extends BaseEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6986969524262776078L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Employee_Claim_Adjustment_ID")
	private long employeeClaimAdjustmentID;

	@Column(name = "Effective_Date")
	private Timestamp effectiveDate;

	@Column(name = "Remarks")
	private String remarks;

	@Column(name = "Amount")
	private BigDecimal amount;

	@Column(name = "Resignation_Rollback")
	private boolean resignationRollback = false;

	@ManyToOne
	@JoinColumn(name = "Company_ID")
	private Company company;

	 
	@ManyToOne
	@JoinColumn(name = "Employee_Claim_Template_ID")
	private EmployeeClaimTemplate employeeClaimTemplate;

	public long getEmployeeClaimAdjustmentID() {
		return employeeClaimAdjustmentID;
	}

	public void setEmployeeClaimAdjustmentID(long employeeClaimAdjustmentID) {
		this.employeeClaimAdjustmentID = employeeClaimAdjustmentID;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public EmployeeClaimTemplate getEmployeeClaimTemplate() {
		return employeeClaimTemplate;
	}

	public void setEmployeeClaimTemplate(
			EmployeeClaimTemplate employeeClaimTemplate) {
		this.employeeClaimTemplate = employeeClaimTemplate;
	}

	public boolean isResignationRollback() {
		return resignationRollback;
	}

	public void setResignationRollback(boolean resignationRollback) {
		this.resignationRollback = resignationRollback;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public Timestamp getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(Timestamp effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

}
