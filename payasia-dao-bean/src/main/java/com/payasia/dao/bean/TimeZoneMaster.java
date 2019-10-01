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
 * The persistent class for the Time_Zone_Master database table.
 * 
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Table(name = "Time_Zone_Master")
public class TimeZoneMaster implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Time_Zone_ID")
	private long timeZoneId;

	@Column(name = "GMT_Offset")
	private String gmtOffset;

	@Column(name = "Time_Zone_Name")
	private String timeZoneName;

	 
	@OneToMany(mappedBy = "timeZoneMaster")
	private Set<Company> companies;

	public TimeZoneMaster() {
	}

	public long getTimeZoneId() {
		return this.timeZoneId;
	}

	public void setTimeZoneId(long timeZoneId) {
		this.timeZoneId = timeZoneId;
	}

	public String getGmtOffset() {
		return this.gmtOffset;
	}

	public void setGmtOffset(String gmtOffset) {
		this.gmtOffset = gmtOffset;
	}

	public String getTimeZoneName() {
		return this.timeZoneName;
	}

	public void setTimeZoneName(String timeZoneName) {
		this.timeZoneName = timeZoneName;
	}

	public Set<Company> getCompanies() {
		return this.companies;
	}

	public void setCompanies(Set<Company> companies) {
		this.companies = companies;
	}

}