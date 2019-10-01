package com.payasia.dao.bean;

import java.io.Serializable;
import java.math.BigDecimal;
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
 * The persistent class for the Pay_Data_Collection database table.
 * 
 */
@Entity
@Table(name = "Pay_Data_Collection")
public class PayDataCollection extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Pay_Data_Collection_ID")
	private long payDataCollectionId;

	@Column(name = "Amount")
	private BigDecimal amount;

	@Column(name = "End_Date")
	private Timestamp endDate;

	@Column(name = "Start_Date")
	private Timestamp startDate;

	 
	@ManyToOne
	@JoinColumn(name = "Company_ID")
	private Company company;

	 
	@ManyToOne
	@JoinColumn(name = "Employee_ID")
	private Employee employee;

	 
	@ManyToOne
	@JoinColumn(name = "Paycode_ID")
	private Paycode paycode;

	public PayDataCollection() {
	}

	public long getPayDataCollectionId() {
		return this.payDataCollectionId;
	}

	public void setPayDataCollectionId(long payDataCollectionId) {
		this.payDataCollectionId = payDataCollectionId;
	}

	public BigDecimal getAmount() {
		return this.amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Timestamp getEndDate() {
		return this.endDate;
	}

	public void setEndDate(Timestamp endDate) {
		this.endDate = endDate;
	}

	public Timestamp getStartDate() {
		return this.startDate;
	}

	public void setStartDate(Timestamp startDate) {
		this.startDate = startDate;
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

	public Paycode getPaycode() {
		return this.paycode;
	}

	public void setPaycode(Paycode paycode) {
		this.paycode = paycode;
	}

}