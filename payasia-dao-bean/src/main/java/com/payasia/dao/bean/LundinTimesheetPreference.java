package com.payasia.dao.bean;

import java.io.Serializable;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * The persistent class for the Lundin_OT_Preference database table.
 * 
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "Lundin_Timesheet_Preference")
public class LundinTimesheetPreference extends BaseEntity implements
		Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name = "Timesheet_Preference_ID")
	private long timesheetPreferenceId;

	@ManyToOne
	@JoinColumn(name = "Company_ID")
	private Company company;

	@JoinColumn(name = "Cutoff_Day")
	private int cutoff_Day;
	@ManyToOne
	@JoinColumn(name = "Department_Mapped_By")
	private DataDictionary dataDictionary;

	@ManyToOne
	@JoinColumn(name = "Cost_Category")
	private DataDictionary costCategory;

	@ManyToOne
	@JoinColumn(name = "Daily_Rate")
	private DataDictionary dailyRate;

	@ManyToOne
	@JoinColumn(name = "Auto_Timewrite")
	private DataDictionary autoTimewrite;

	public DataDictionary getDataDictionary() {
		return dataDictionary;
	}

	public void setDataDictionary(DataDictionary dataDictionary) {
		this.dataDictionary = dataDictionary;
	}

	@Column(name = "Use_System_Mail_As_From_Address")
	private boolean useSystemMailAsFromAddress;

	public boolean isUseSystemMailAsFromAddress() {
		return useSystemMailAsFromAddress;
	}

	public void setUseSystemMailAsFromAddress(boolean useSystemMailAsFromAddress) {
		this.useSystemMailAsFromAddress = useSystemMailAsFromAddress;
	}

	public LundinTimesheetPreference() {
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public int getCutoff_Day() {
		return cutoff_Day;
	}

	public void setCutoff_Day(int cutoff_Day) {
		this.cutoff_Day = cutoff_Day;
	}

	public long getTimesheetPreferenceId() {
		return timesheetPreferenceId;
	}

	public void setTimesheetPreferenceId(long timesheetPreferenceId) {
		this.timesheetPreferenceId = timesheetPreferenceId;
	}

	public DataDictionary getCostCategory() {
		return costCategory;
	}

	public void setCostCategory(DataDictionary costCategory) {
		this.costCategory = costCategory;
	}

	public DataDictionary getDailyRate() {
		return dailyRate;
	}

	public void setDailyRate(DataDictionary dailyRate) {
		this.dailyRate = dailyRate;
	}

	public DataDictionary getAutoTimewrite() {
		return autoTimewrite;
	}

	public void setAutoTimewrite(DataDictionary autoTimewrite) {
		this.autoTimewrite = autoTimewrite;
	}

}