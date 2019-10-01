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
 * The persistent class for the Company_Group database table.
 * 
 */
@Entity
@Table(name = "Scheduler_Status")
public class SchedulerStatus implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name = "Scheduler_Status_ID")
	private long schedulerStatusId;

	@Column(name = "Last_Run_Date")
	private Timestamp lastRunDate;

	@Column(name = "Last_Run_Status")
	private boolean lastRunStatus;

	@Column(name = "Failure_Message")
	private String failureMessage;

	 
	@ManyToOne
	@JoinColumn(name = "Company_ID")
	private Company company;

	 
	@ManyToOne
	@JoinColumn(name = "Scheduler_ID")
	private SchedulerMaster schedulerMaster;

	public SchedulerStatus() {
	}

	public long getSchedulerStatusId() {
		return schedulerStatusId;
	}

	public void setSchedulerStatusId(long schedulerStatusId) {
		this.schedulerStatusId = schedulerStatusId;
	}

	public Timestamp getLastRunDate() {
		return lastRunDate;
	}

	public void setLastRunDate(Timestamp lastRunDate) {
		this.lastRunDate = lastRunDate;
	}

	public boolean isLastRunStatus() {
		return lastRunStatus;
	}

	public void setLastRunStatus(boolean lastRunStatus) {
		this.lastRunStatus = lastRunStatus;
	}

	public String getFailureMessage() {
		return failureMessage;
	}

	public void setFailureMessage(String failureMessage) {
		this.failureMessage = failureMessage;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public SchedulerMaster getSchedulerMaster() {
		return schedulerMaster;
	}

	public void setSchedulerMaster(SchedulerMaster schedulerMaster) {
		this.schedulerMaster = schedulerMaster;
	}

}