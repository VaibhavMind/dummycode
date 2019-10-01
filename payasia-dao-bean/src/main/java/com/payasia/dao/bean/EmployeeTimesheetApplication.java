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
 * The persistent class for the Ingersoll_OT_Timesheet database table.
 * 
 */
@Entity
@Table(name = "Employee_Timesheet_Application")
public class EmployeeTimesheetApplication extends BaseEntity implements
		Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Timesheet_ID")
	private long timesheetId;

	@ManyToOne
	@JoinColumn(name = "Company_ID")
	private Company company;

	@ManyToOne
	@JoinColumn(name = "Employee_ID")
	private Employee employee;

	@ManyToOne
	@JoinColumn(name = "Timesheet_Batch_ID")
	private TimesheetBatch timesheetBatch;

	@ManyToOne
	@JoinColumn(name = "Timesheet_Status_ID")
	private TimesheetStatusMaster timesheetStatusMaster;

	@Column(name = "Remarks")
	private String remarks;

	@OneToMany(mappedBy = "employeeTimesheetApplication", cascade = { CascadeType.REMOVE })
	private Set<TimesheetApplicationReviewer> timesheetApplicationReviewers;

	@OneToMany(mappedBy = "employeeTimesheetApplication", cascade = { CascadeType.REMOVE })
	private Set<TimesheetApplicationWorkflow> timesheetApplicationWorkflows;

	@OneToMany(mappedBy = "employeeTimesheetApplication", cascade = { CascadeType.REMOVE })
	private Set<LundinTimesheetDetail> lundinTimesheetDetails;

	@OneToMany(mappedBy = "employeeTimesheetApplication", cascade = { CascadeType.REMOVE })
	private Set<LionEmployeeTimesheet> LionEmployeeTimesheets;

	@OneToMany(mappedBy = "employeeTimesheetApplication", cascade = { CascadeType.REMOVE })
	private Set<LionEmployeeTimesheetApplicationDetail> lionEmployeeTimesheetApplicationDetails;

	public EmployeeTimesheetApplication() {
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public long getTimesheetId() {
		return timesheetId;
	}

	public void setTimesheetId(long timesheetId) {
		this.timesheetId = timesheetId;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Set<LundinTimesheetDetail> getLundinTimesheetDetails() {
		return lundinTimesheetDetails;
	}

	public void setLundinTimesheetDetails(
			Set<LundinTimesheetDetail> lundinTimesheetDetails) {
		this.lundinTimesheetDetails = lundinTimesheetDetails;
	}

	public TimesheetBatch getTimesheetBatch() {
		return timesheetBatch;
	}

	public void setTimesheetBatch(TimesheetBatch timesheetBatch) {
		this.timesheetBatch = timesheetBatch;
	}

	public TimesheetStatusMaster getTimesheetStatusMaster() {
		return timesheetStatusMaster;
	}

	public void setTimesheetStatusMaster(
			TimesheetStatusMaster timesheetStatusMaster) {
		this.timesheetStatusMaster = timesheetStatusMaster;
	}

	public Set<TimesheetApplicationReviewer> getTimesheetApplicationReviewers() {
		return timesheetApplicationReviewers;
	}

	public void setTimesheetApplicationReviewers(
			Set<TimesheetApplicationReviewer> timesheetApplicationReviewers) {
		this.timesheetApplicationReviewers = timesheetApplicationReviewers;
	}

	public Set<TimesheetApplicationWorkflow> getTimesheetApplicationWorkflows() {
		return timesheetApplicationWorkflows;
	}

	public void setTimesheetApplicationWorkflows(
			Set<TimesheetApplicationWorkflow> timesheetApplicationWorkflows) {
		this.timesheetApplicationWorkflows = timesheetApplicationWorkflows;
	}

	public Set<LionEmployeeTimesheet> getLionEmployeeTimesheets() {
		return LionEmployeeTimesheets;
	}

	public void setLionEmployeeTimesheets(
			Set<LionEmployeeTimesheet> lionEmployeeTimesheets) {
		LionEmployeeTimesheets = lionEmployeeTimesheets;
	}

	public Set<LionEmployeeTimesheetApplicationDetail> getLionEmployeeTimesheetApplicationDetails() {
		return lionEmployeeTimesheetApplicationDetails;
	}

	public void setLionEmployeeTimesheetApplicationDetails(
			Set<LionEmployeeTimesheetApplicationDetail> lionEmployeeTimesheetApplicationDetails) {
		this.lionEmployeeTimesheetApplicationDetails = lionEmployeeTimesheetApplicationDetails;
	}

}