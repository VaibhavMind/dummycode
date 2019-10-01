package com.payasia.dao.bean;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * The persistent class for the Holiday_Config_Master database table.
 * 
 */
@Entity
@Table(name = "Holiday_Config_Master")
public class HolidayConfigMaster extends CompanyBaseEntity implements
		Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name = "Holiday_Config_Master_ID")
	private long holidayConfigMasterId;

	@Column(name = "Holiday_Date")
	private Timestamp holidayDate;

	@Column(name = "Holiday_Desc")
	private String holidayDesc;

	 
	@ManyToOne
	@JoinColumn(name = "Country_ID")
	private CountryMaster countryMaster;

	 
	@ManyToOne
	@JoinColumn(name = "State_ID")
	private StateMaster stateMaster;

	public HolidayConfigMaster() {
	}

	public long getHolidayConfigMasterId() {
		return this.holidayConfigMasterId;
	}

	public void setHolidayConfigMasterId(long holidayConfigMasterId) {
		this.holidayConfigMasterId = holidayConfigMasterId;
	}

	public Timestamp getHolidayDate() {
		return this.holidayDate;
	}

	public void setHolidayDate(Timestamp holidayDate) {
		this.holidayDate = holidayDate;
	}

	public String getHolidayDesc() {
		return this.holidayDesc;
	}

	public void setHolidayDesc(String holidayDesc) {
		this.holidayDesc = holidayDesc;
	}

	public CountryMaster getCountryMaster() {
		return this.countryMaster;
	}

	public void setCountryMaster(CountryMaster countryMaster) {
		this.countryMaster = countryMaster;
	}

	public StateMaster getStateMaster() {
		return this.stateMaster;
	}

	public void setStateMaster(StateMaster stateMaster) {
		this.stateMaster = stateMaster;
	}

}