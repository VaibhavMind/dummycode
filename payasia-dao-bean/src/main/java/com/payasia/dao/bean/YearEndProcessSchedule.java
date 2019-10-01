package com.payasia.dao.bean;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * The persistent class for the Year_End_Process_Schedule database table.
 * 
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "Year_End_Process_Schedule")
public class YearEndProcessSchedule extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Year_End_Process_Schedule_ID")
	private long yearEndProcessScheduleId;

	 
	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "Company_ID")
	private Company company;

	@Column(name = "Leave_Roll_Over")
	private Timestamp leaveRollOver;

	@Column(name = "Leave_Activate")
	private Timestamp leave_Activate;

	public long getYearEndProcessScheduleId() {
		return yearEndProcessScheduleId;
	}

	public void setYearEndProcessScheduleId(long yearEndProcessScheduleId) {
		this.yearEndProcessScheduleId = yearEndProcessScheduleId;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public Timestamp getLeaveRollOver() {
		return leaveRollOver;
	}

	public void setLeaveRollOver(Timestamp leaveRollOver) {
		this.leaveRollOver = leaveRollOver;
	}

	public Timestamp getLeave_Activate() {
		return leave_Activate;
	}

	public void setLeave_Activate(Timestamp leave_Activate) {
		this.leave_Activate = leave_Activate;
	}

}