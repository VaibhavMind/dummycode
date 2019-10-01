package com.payasia.dao.bean;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * The persistent class for the Calendar_Pattern_Master database table.
 * 
 */
@Entity
@Table(name = "Calendar_Pattern_Master")
public class CalendarPatternMaster extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Calendar_Pattern_ID")
	private long calendarPatternId;

	@Column(name = "Pattern_Name")
	private String patternName;

	@Column(name = "Pattern_Desc")
	private String patternDesc;

	 
	@ManyToOne
	@JoinColumn(name = "Company_ID")
	private Company company;

	 
	@OneToMany(mappedBy = "calendarPatternMaster", cascade = { CascadeType.REMOVE })
	private Set<CalendarPatternDetail> calendarPatternDetails;

	public CalendarPatternMaster() {
	}

	public long getCalendarPatternId() {
		return calendarPatternId;
	}

	public void setCalendarPatternId(long calendarPatternId) {
		this.calendarPatternId = calendarPatternId;
	}

	public String getPatternName() {
		return patternName;
	}

	public void setPatternName(String patternName) {
		this.patternName = patternName;
	}

	public String getPatternDesc() {
		return patternDesc;
	}

	public void setPatternDesc(String patternDesc) {
		this.patternDesc = patternDesc;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public Set<CalendarPatternDetail> getCalendarPatternDetails() {
		return calendarPatternDetails;
	}

	public void setCalendarPatternDetails(
			Set<CalendarPatternDetail> calendarPatternDetails) {
		this.calendarPatternDetails = calendarPatternDetails;
	}

}