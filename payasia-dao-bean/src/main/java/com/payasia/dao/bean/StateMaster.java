package com.payasia.dao.bean;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * The persistent class for the State_Master database table.
 * 
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "State_Master")
public class StateMaster implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name = "State_ID")
	private long stateId;

	@Column(name = "State_Name")
	private String stateName;

	 
	@OneToMany(mappedBy = "stateMaster")
	private Set<HolidayConfigMaster> holidayConfigMasters;

	 
	@ManyToOne
	@JoinColumn(name = "Country_ID")
	private CountryMaster countryMaster;

	 
	@OneToMany(mappedBy = "stateMaster")
	private Set<CompanyHolidayCalendarDetail> companyHolidayCalendarDetails;

	public StateMaster() {
	}

	public long getStateId() {
		return this.stateId;
	}

	public void setStateId(long stateId) {
		this.stateId = stateId;
	}

	public String getStateName() {
		return this.stateName;
	}

	public void setStateName(String stateName) {
		this.stateName = stateName;
	}

	public Set<HolidayConfigMaster> getHolidayConfigMasters() {
		return this.holidayConfigMasters;
	}

	public void setHolidayConfigMasters(
			Set<HolidayConfigMaster> holidayConfigMasters) {
		this.holidayConfigMasters = holidayConfigMasters;
	}

	public CountryMaster getCountryMaster() {
		return this.countryMaster;
	}

	public void setCountryMaster(CountryMaster countryMaster) {
		this.countryMaster = countryMaster;
	}

	public Set<CompanyHolidayCalendarDetail> getCompanyHolidayCalendarDetails() {
		return companyHolidayCalendarDetails;
	}

	public void setCompanyHolidayCalendarDetails(
			Set<CompanyHolidayCalendarDetail> companyHolidayCalendarDetails) {
		this.companyHolidayCalendarDetails = companyHolidayCalendarDetails;
	}

}