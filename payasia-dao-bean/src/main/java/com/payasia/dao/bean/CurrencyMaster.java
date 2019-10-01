package com.payasia.dao.bean;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * The persistent class for the Currency_Master database table.
 * 
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Table(name = "Currency_Master")
public class CurrencyMaster implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Currency_ID")
	private long currencyId;

	@Column(name = "Currency_Desc")
	private String currencyDesc;

	@Column(name = "Currency_Code")
	private String currencyCode;

	 
	@OneToMany(mappedBy = "currencyMaster")
	private Set<CompanyExchangeRate> companyExchangeRates;

	 
	@OneToMany(mappedBy = "defaultCurrency")
	private Set<ClaimTemplate> claimTemplates;

	 
	@OneToMany(mappedBy = "currencyMaster")
	private Set<ClaimApplicationItem> claimApplicationItem;

	@Column(name = "Currency_Name")
	private String currencyName;

	public CurrencyMaster() {
	}

	public long getCurrencyId() {
		return this.currencyId;
	}

	public void setCurrencyId(long currencyId) {
		this.currencyId = currencyId;
	}

	public String getCurrencyDesc() {
		return this.currencyDesc;
	}

	public void setCurrencyDesc(String currencyDesc) {
		this.currencyDesc = currencyDesc;
	}

	public String getCurrencyName() {
		return this.currencyName;
	}

	public void setCurrencyName(String currencyName) {
		this.currencyName = currencyName;
	}

	public Set<CompanyExchangeRate> getCompanyExchangeRates() {
		return this.companyExchangeRates;
	}

	public void setCompanyExchangeRates(
			Set<CompanyExchangeRate> companyExchangeRates) {
		this.companyExchangeRates = companyExchangeRates;
	}

	public Set<ClaimTemplate> getClaimTemplates() {
		return claimTemplates;
	}

	public void setClaimTemplates(Set<ClaimTemplate> claimTemplates) {
		this.claimTemplates = claimTemplates;
	}

	public Set<ClaimApplicationItem> getClaimApplicationItem() {
		return claimApplicationItem;
	}

	public void setClaimApplicationItem(
			Set<ClaimApplicationItem> claimApplicationItem) {
		this.claimApplicationItem = claimApplicationItem;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

}