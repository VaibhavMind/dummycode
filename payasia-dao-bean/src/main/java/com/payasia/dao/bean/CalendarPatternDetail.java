package com.payasia.dao.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * The persistent class for the Calendar_Pattern_Master database table.
 * 
 */
@Entity
@Table(name = "Calendar_Pattern_Detail")
public class CalendarPatternDetail extends CompanyBaseEntity implements
		Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Calendar_Pattern_Detail_ID")
	private long calendarPatternDetailId;

	@Column(name = "Pattern_Index")
	private int patternIndex;

	 
	@ManyToOne
	@JoinColumn(name = "Pattern_Code")
	private CalendarCodeMaster calendarCodeMaster;

	 
	@ManyToOne
	@JoinColumn(name = "Calendar_Pattern_ID")
	private CalendarPatternMaster calendarPatternMaster;

	public CalendarPatternDetail() {
	}

	public long getCalendarPatternDetailId() {
		return calendarPatternDetailId;
	}

	public void setCalendarPatternDetailId(long calendarPatternDetailId) {
		this.calendarPatternDetailId = calendarPatternDetailId;
	}

	public int getPatternIndex() {
		return patternIndex;
	}

	public void setPatternIndex(int patternIndex) {
		this.patternIndex = patternIndex;
	}

	public CalendarCodeMaster getCalendarCodeMaster() {
		return calendarCodeMaster;
	}

	public void setCalendarCodeMaster(CalendarCodeMaster calendarCodeMaster) {
		this.calendarCodeMaster = calendarCodeMaster;
	}

	public CalendarPatternMaster getCalendarPatternMaster() {
		return calendarPatternMaster;
	}

	public void setCalendarPatternMaster(
			CalendarPatternMaster calendarPatternMaster) {
		this.calendarPatternMaster = calendarPatternMaster;
	}

}