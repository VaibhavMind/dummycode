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

@Entity
@Table(name = "Lion_Timesheet_Preference")
public class LionTimesheetPreference extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name = "Timesheet_Preference_ID")
	private long timesheetPreferenceID;

	@ManyToOne
	@JoinColumn(name = "Company_ID")
	private Company company;

	@Column(name = "Period_Start")
	private Timestamp periodStart;

	@ManyToOne
	@JoinColumn(name = "Cutoff_Cycle")
	private AppCodeMaster cutoffCycle;

	@ManyToOne
	@JoinColumn(name = "Location")
	private DataDictionary location;

	@Column(name = "Use_System_Mail_As_From_Address")
	private boolean useSystemMailAsFromAddress;

	public long getTimesheetPreferenceID() {
		return timesheetPreferenceID;
	}

	public void setTimesheetPreferenceID(long timesheetPreferenceID) {
		this.timesheetPreferenceID = timesheetPreferenceID;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public Timestamp getPeriodStart() {
		return periodStart;
	}

	public void setPeriodStart(Timestamp periodStart) {
		this.periodStart = periodStart;
	}

	public AppCodeMaster getCutoffCycle() {
		return cutoffCycle;
	}

	public void setCutoffCycle(AppCodeMaster cutoffCycle) {
		this.cutoffCycle = cutoffCycle;
	}

	public DataDictionary getLocation() {
		return location;
	}

	public void setLocation(DataDictionary location) {
		this.location = location;
	}

	public boolean isUseSystemMailAsFromAddress() {
		return useSystemMailAsFromAddress;
	}

	public void setUseSystemMailAsFromAddress(boolean useSystemMailAsFromAddress) {
		this.useSystemMailAsFromAddress = useSystemMailAsFromAddress;
	}
}
