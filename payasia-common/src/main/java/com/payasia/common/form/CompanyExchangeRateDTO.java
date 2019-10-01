package com.payasia.common.form;

import java.io.Serializable;


public class CompanyExchangeRateDTO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2313733155428768805L;

	private String currency;
	private Long currencyId;
	
	private String startDate;
	private String endDate;
	private String exchangeRate;
	private Long companyExchangeRateId;
	private String foreignCurrencyCode;
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public Long getCurrencyId() {
		return currencyId;
	}
	public void setCurrencyId(Long currencyId) {
		this.currencyId = currencyId;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	
	public String getExchangeRate() {
		return exchangeRate;
	}
	public void setExchangeRate(String exchangeRate) {
		this.exchangeRate = exchangeRate;
	}
	public Long getCompanyExchangeRateId() {
		return companyExchangeRateId;
	}
	public void setCompanyExchangeRateId(Long companyExchangeRateId) {
		this.companyExchangeRateId = companyExchangeRateId;
	}
	public String getForeignCurrencyCode() {
		return foreignCurrencyCode;
	}
	public void setForeignCurrencyCode(String foreignCurrencyCode) {
		this.foreignCurrencyCode = foreignCurrencyCode;
	}
	
	

}
