package com.payasia.dao.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "Coherent_Timesheet_Preference")
public class CoherentTimesheetPreference extends BaseEntity implements
		Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name = "Timesheet_Preference_ID")
	private long timesheetPreferenceID;

	@ManyToOne
	@JoinColumn(name = "Company_ID")
	private Company company;

	@Column(name = "Cutoff_Day")
	private Integer cutoffDay;

	@ManyToOne
	@JoinColumn(name = "Department")
	private DataDictionary department;

	@ManyToOne
	@JoinColumn(name = "Cost_Center")
	private DataDictionary costCenter;

	@ManyToOne
	@JoinColumn(name = "Location")
	private DataDictionary location;

	@Column(name = "Use_System_Mail_As_From_Address")
	private boolean useSystemMailAsFromAddress;

	@Column(name = "Working_Hours_In_A_Day")
	private Double workingHoursInADay;

	@Column(name = "Is_Validation_72_Hours")
	private boolean is_validation_72_Hours;

	public boolean getIs_validation_72_Hours() {
		return is_validation_72_Hours;
	}

	public void setIs_validation_72_Hours(boolean is_validation_72_Hours) {
		this.is_validation_72_Hours = is_validation_72_Hours;
	}

	public Double getWorkingHoursInADay() {
		return workingHoursInADay;
	}

	public void setWorkingHoursInADay(Double workingHoursInADay) {
		this.workingHoursInADay = workingHoursInADay;
	}

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

	public Integer getCutoffDay() {
		return cutoffDay;
	}

	public void setCutoffDay(Integer cutoffDay) {
		this.cutoffDay = cutoffDay;
	}

	public DataDictionary getDepartment() {
		return department;
	}

	public void setDepartment(DataDictionary department) {
		this.department = department;
	}

	public DataDictionary getCostCenter() {
		return costCenter;
	}

	public void setCostCenter(DataDictionary costCenter) {
		this.costCenter = costCenter;
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
