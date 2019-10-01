package com.payasia.dao.bean;

import java.io.Serializable;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * The persistent class for the Payslip database table.
 * 
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Payslip extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Payslip_ID")
	private long payslipId;

	@Column(name = "Part")
	private int part;

	@Column(name = "Year")
	private int year;

	 
	@ManyToOne
	@JoinColumn(name = "Company_ID")
	private Company company;

	 
	@ManyToOne
	@JoinColumn(name = "Employee_ID")
	private Employee employee;

	 
	@ManyToOne
	@JoinColumn(name = "Month_ID")
	private MonthMaster monthMaster;

	 
	@ManyToOne
	@JoinColumn(name = "Payslip_Frequency_ID")
	private PayslipFrequency payslipFrequency;

	public Payslip() {
	}

	public long getPayslipId() {
		return this.payslipId;
	}

	public void setPayslipId(long payslipId) {
		this.payslipId = payslipId;
	}

	public int getPart() {
		return this.part;
	}

	public void setPart(int part) {
		this.part = part;
	}

	public int getYear() {
		return this.year;
	}

	public void setYear(int year) {
		this.year = year;
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

	public MonthMaster getMonthMaster() {
		return this.monthMaster;
	}

	public void setMonthMaster(MonthMaster monthMaster) {
		this.monthMaster = monthMaster;
	}

	public PayslipFrequency getPayslipFrequency() {
		return this.payslipFrequency;
	}

	public void setPayslipFrequency(PayslipFrequency payslipFrequency) {
		this.payslipFrequency = payslipFrequency;
	}

}