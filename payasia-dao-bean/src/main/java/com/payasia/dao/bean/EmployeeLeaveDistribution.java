package com.payasia.dao.bean;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * The persistent class for the Employee_Leave_Distribution database table.
 * 
 */
@Entity
@Table(name = "Employee_Leave_Distribution")
public class EmployeeLeaveDistribution extends CompanyBaseEntity implements
		Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Employee_Leave_Distribution_ID")
	private long employeeLeaveDistributionId;

	@Column(name = "Apr")
	private BigDecimal apr;

	@Column(name = "Aug")
	private BigDecimal aug;

	@Column(name = "Dec")
	private BigDecimal dec;

	@Column(name = "Feb")
	private BigDecimal feb;

	@Column(name = "Jan")
	private BigDecimal jan;

	@Column(name = "Jul")
	private BigDecimal jul;

	@Column(name = "Jun")
	private BigDecimal jun;

	@Column(name = "Mar")
	private BigDecimal mar;

	@Column(name = "May")
	private BigDecimal may;

	@Column(name = "Nov")
	private BigDecimal nov;

	@Column(name = "Oct")
	private BigDecimal oct;

	@Column(name = "Sep")
	private BigDecimal sep;

	@Column(name = "Year")
	private int year;

	 
	@ManyToOne
	@JoinColumn(name = "Employee_Leave_Scheme_Type_ID")
	private EmployeeLeaveSchemeType employeeLeaveSchemeType;

	public EmployeeLeaveDistribution() {
	}

	public long getEmployeeLeaveDistributionId() {
		return this.employeeLeaveDistributionId;
	}

	public void setEmployeeLeaveDistributionId(long employeeLeaveDistributionId) {
		this.employeeLeaveDistributionId = employeeLeaveDistributionId;
	}

	public BigDecimal getApr() {
		return this.apr;
	}

	public void setApr(BigDecimal apr) {
		this.apr = apr;
	}

	public BigDecimal getAug() {
		return this.aug;
	}

	public void setAug(BigDecimal aug) {
		this.aug = aug;
	}

	public BigDecimal getDec() {
		return this.dec;
	}

	public void setDec(BigDecimal dec) {
		this.dec = dec;
	}

	public BigDecimal getFeb() {
		return this.feb;
	}

	public void setFeb(BigDecimal feb) {
		this.feb = feb;
	}

	public BigDecimal getJan() {
		return this.jan;
	}

	public void setJan(BigDecimal jan) {
		this.jan = jan;
	}

	public BigDecimal getJul() {
		return this.jul;
	}

	public void setJul(BigDecimal jul) {
		this.jul = jul;
	}

	public BigDecimal getJun() {
		return this.jun;
	}

	public void setJun(BigDecimal jun) {
		this.jun = jun;
	}

	public BigDecimal getMar() {
		return this.mar;
	}

	public void setMar(BigDecimal mar) {
		this.mar = mar;
	}

	public BigDecimal getMay() {
		return this.may;
	}

	public void setMay(BigDecimal may) {
		this.may = may;
	}

	public BigDecimal getNov() {
		return this.nov;
	}

	public void setNov(BigDecimal nov) {
		this.nov = nov;
	}

	public BigDecimal getOct() {
		return this.oct;
	}

	public void setOct(BigDecimal oct) {
		this.oct = oct;
	}

	public BigDecimal getSep() {
		return this.sep;
	}

	public void setSep(BigDecimal sep) {
		this.sep = sep;
	}

	public int getYear() {
		return this.year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public EmployeeLeaveSchemeType getEmployeeLeaveSchemeType() {
		return employeeLeaveSchemeType;
	}

	public void setEmployeeLeaveSchemeType(
			EmployeeLeaveSchemeType employeeLeaveSchemeType) {
		this.employeeLeaveSchemeType = employeeLeaveSchemeType;
	}

}