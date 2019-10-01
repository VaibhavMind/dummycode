package com.payasia.dao.bean;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * The persistent class for the Company_Group database table.
 * 
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Table(name = "Scheduler_Master")
public class SchedulerMaster implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "Scheduler_ID")
	private long schedulerId;

	@Column(name = "Scheduler_Name")
	private String schedulerName;

	@Column(name = "Scheduler_Desc")
	private String schedulerDesc;

	@Column(name = "Scheduler_Time")
	private Timestamp SchedulerTime;

	 
	@ManyToOne
	@JoinColumn(name = "Module_ID")
	private ModuleMaster moduleMaster;

	 
	@OneToMany(mappedBy = "schedulerMaster")
	private Set<SchedulerStatus> schedulerStatuses;

	public SchedulerMaster() {
	}

	public long getSchedulerId() {
		return schedulerId;
	}

	public void setSchedulerId(long schedulerId) {
		this.schedulerId = schedulerId;
	}

	public String getSchedulerName() {
		return schedulerName;
	}

	public void setSchedulerName(String schedulerName) {
		this.schedulerName = schedulerName;
	}

	public String getSchedulerDesc() {
		return schedulerDesc;
	}

	public void setSchedulerDesc(String schedulerDesc) {
		this.schedulerDesc = schedulerDesc;
	}

	public Timestamp getSchedulerTime() {
		return SchedulerTime;
	}

	public void setSchedulerTime(Timestamp schedulerTime) {
		SchedulerTime = schedulerTime;
	}

	public ModuleMaster getModuleMaster() {
		return moduleMaster;
	}

	public void setModuleMaster(ModuleMaster moduleMaster) {
		this.moduleMaster = moduleMaster;
	}

	public Set<SchedulerStatus> getSchedulerStatuses() {
		return schedulerStatuses;
	}

	public void setSchedulerStatuses(Set<SchedulerStatus> schedulerStatuses) {
		this.schedulerStatuses = schedulerStatuses;
	}

}