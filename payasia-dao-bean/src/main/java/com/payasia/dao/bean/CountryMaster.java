package com.payasia.dao.bean;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * The persistent class for the Country_Master database table.
 * 
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "Country_Master")
public class CountryMaster implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name = "Country_ID")
	private long countryId;

	@Column(name = "Country_Name")
	private String countryName;

	@Column(name = "Nationality")
	private String nationality;

	@OneToMany(mappedBy = "countryMaster")
	private Set<Company> companies;

	@OneToMany(mappedBy = "countryMaster")
	private Set<HolidayConfigMaster> holidayConfigMasters;

	@OneToMany(mappedBy = "countryMaster")
	private Set<StateMaster> stateMasters;

	@OneToMany(mappedBy = "countryMaster")
	private Set<CompanyHolidayCalendarDetail> companyHolidayCalendarDetails;

	public CountryMaster() {
	}

	public long getCountryId() {
		return this.countryId;
	}

	public void setCountryId(long countryId) {
		this.countryId = countryId;
	}

	public String getCountryName() {
		return this.countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public Set<Company> getCompanies() {
		return this.companies;
	}

	public void setCompanies(Set<Company> companies) {
		this.companies = companies;
	}

	public Set<HolidayConfigMaster> getHolidayConfigMasters() {
		return this.holidayConfigMasters;
	}

	public void setHolidayConfigMasters(
			Set<HolidayConfigMaster> holidayConfigMasters) {
		this.holidayConfigMasters = holidayConfigMasters;
	}

	public Set<StateMaster> getStateMasters() {
		return this.stateMasters;
	}

	public void setStateMasters(Set<StateMaster> stateMasters) {
		this.stateMasters = stateMasters;
	}

	public Set<CompanyHolidayCalendarDetail> getCompanyHolidayCalendarDetails() {
		return companyHolidayCalendarDetails;
	}

	public void setCompanyHolidayCalendarDetails(
			Set<CompanyHolidayCalendarDetail> companyHolidayCalendarDetails) {
		this.companyHolidayCalendarDetails = companyHolidayCalendarDetails;
	}

	public String getNationality() {
		return nationality;
	}

	public void setNationality(String nationality) {
		this.nationality = nationality;
	}

}