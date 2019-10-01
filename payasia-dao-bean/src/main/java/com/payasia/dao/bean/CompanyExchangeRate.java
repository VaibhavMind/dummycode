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
 * The persistent class for the Company_Exchange_Rate database table.
 * 
 */
@Entity
@Table(name = "Company_Exchange_Rate")
public class CompanyExchangeRate extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Comp_Exchange_Rate_ID")
	private long compExchangeRateId;

	@Column(name = "Exchange_Rate")
	private BigDecimal exchangeRate;

	@Column(name = "Start_Date")
	private Timestamp startDate;

	@Column(name = "End_Date")
	private Timestamp endDate;

	 
	@ManyToOne
	@JoinColumn(name = "Company_ID")
	private Company company;

	 
	@ManyToOne
	@JoinColumn(name = "Currency_ID")
	private CurrencyMaster currencyMaster;

	public CompanyExchangeRate() {
	}

	public long getCompExchangeRateId() {
		return this.compExchangeRateId;
	}

	public void setCompExchangeRateId(long compExchangeRateId) {
		this.compExchangeRateId = compExchangeRateId;
	}

	public BigDecimal getExchangeRate() {
		return this.exchangeRate;
	}

	public void setExchangeRate(BigDecimal exchangeRate) {
		this.exchangeRate = exchangeRate;
	}

	public Company getCompany() {
		return this.company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public CurrencyMaster getCurrencyMaster() {
		return this.currencyMaster;
	}

	public void setCurrencyMaster(CurrencyMaster currencyMaster) {
		this.currencyMaster = currencyMaster;
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

}