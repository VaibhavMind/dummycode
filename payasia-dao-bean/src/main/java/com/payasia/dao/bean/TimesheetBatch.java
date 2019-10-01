package com.payasia.dao.bean;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * The persistent class for the Lundin_Timesheet_Batch database table.
 * 
 */
@Entity
@Table(name = "Timesheet_Batch")
public class TimesheetBatch extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name = "Timesheet_Batch_ID")
	private long timesheetBatchId;

	@Column(name = "Timesheet_Batch_Desc")
	private String timesheetBatchDesc;

	@Column(name = "End_Date")
	private Timestamp endDate;

	@Column(name = "Start_Date")
	private Timestamp startDate;

	@ManyToOne
	@JoinColumn(name = "Company_ID")
	private Company company;

	@OneToMany(mappedBy = "timesheetBatch")
	private Set<EmployeeTimesheetApplication> employeeTimesheetApplications;

	@OneToMany(mappedBy = "timesheetBatch")
	private Set<CoherentOvertimeApplication> coherentOvertimeApplications;

	public TimesheetBatch() {
	}

	public long getTimesheetBatchId() {
		return timesheetBatchId;
	}

	public void setTimesheetBatchId(long timesheetBatchId) {
		this.timesheetBatchId = timesheetBatchId;
	}

	public String getTimesheetBatchDesc() {
		return timesheetBatchDesc;
	}

	public void setTimesheetBatchDesc(String timesheetBatchDesc) {
		this.timesheetBatchDesc = timesheetBatchDesc;
	}

	public Timestamp getEndDate() {
		return endDate;
	}

	public void setEndDate(Timestamp endDate) {
		this.endDate = endDate;
	}

	public Timestamp getStartDate() {
		return startDate;
	}

	public void setStartDate(Timestamp startDate) {
		this.startDate = startDate;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public Set<EmployeeTimesheetApplication> getEmployeeTimesheetApplications() {
		return employeeTimesheetApplications;
	}

	public void setEmployeeTimesheetApplications(
			Set<EmployeeTimesheetApplication> employeeTimesheetApplications) {
		this.employeeTimesheetApplications = employeeTimesheetApplications;
	}

	public Set<CoherentOvertimeApplication> getCoherentOvertimeApplications() {
		return coherentOvertimeApplications;
	}

	public void setCoherentOvertimeApplications(
			Set<CoherentOvertimeApplication> coherentOvertimeApplications) {
		this.coherentOvertimeApplications = coherentOvertimeApplications;
	}

}