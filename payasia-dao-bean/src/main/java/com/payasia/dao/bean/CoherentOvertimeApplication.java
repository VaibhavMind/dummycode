package com.payasia.dao.bean;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "Coherent_Overtime_Application")
public class CoherentOvertimeApplication extends BaseEntity implements
		Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name = "Overtime_Application_ID")
	private long overtimeApplicationID;

	@ManyToOne
	@JoinColumn(name = "Company_ID")
	private Company company;

	@ManyToOne
	@JoinColumn(name = "Employee_ID")
	private Employee employee;

	@ManyToOne
	@JoinColumn(name = "Timesheet_Batch_ID")
	private TimesheetBatch timesheetBatch;

	@Column(name = "Total_OT_1_0_Day")
	private Double totalOT10Day;

	@Column(name = "Total_OT_2_0_Day")
	private Double totalOT20Day;

	@Column(name = "Total_OT_Hours")
	private Double totalOTHours;

	@Column(name = "Total_OT_1_5_Hours")
	private Double totalOT15Hours;

	@Column(name = "Remarks")
	private String remarks;

	@OneToMany(mappedBy = "coherentOvertimeApplication", cascade = CascadeType.REMOVE)
	private Set<CoherentOvertimeApplicationDetail> coherentOvertimeApplicationDetails;

	@OneToMany(mappedBy = "coherentOvertimeApplication", cascade = CascadeType.REMOVE)
	private Set<CoherentOvertimeApplicationReviewer> coherentOvertimeApplicationReviewers;

	@OneToMany(mappedBy = "coherentOvertimeApplication", cascade = CascadeType.REMOVE)
	private Set<CoherentOvertimeApplicationWorkflow> coherentOvertimeApplicationWorkflows;

	@ManyToOne
	@JoinColumn(name = "Timesheet_Status_ID")
	private TimesheetStatusMaster timesheetStatusMaster;

	public long getOvertimeApplicationID() {
		return overtimeApplicationID;
	}

	public void setOvertimeApplicationID(long overtimeApplicationID) {
		this.overtimeApplicationID = overtimeApplicationID;
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

	public TimesheetBatch getTimesheetBatch() {
		return timesheetBatch;
	}

	public void setTimesheetBatch(TimesheetBatch timesheetBatch) {
		this.timesheetBatch = timesheetBatch;
	}

	public Double getTotalOT10Day() {
		return totalOT10Day;
	}

	public void setTotalOT10Day(Double totalOT10Day) {
		this.totalOT10Day = totalOT10Day;
	}

	public Double getTotalOT20Day() {
		return totalOT20Day;
	}

	public void setTotalOT20Day(Double totalOT20Day) {
		this.totalOT20Day = totalOT20Day;
	}

	public Double getTotalOTHours() {
		return totalOTHours;
	}

	public void setTotalOTHours(Double totalOTHours) {
		this.totalOTHours = totalOTHours;
	}

	public Double getTotalOT15Hours() {
		return totalOT15Hours;
	}

	public void setTotalOT15Hours(Double totalOT15Hours) {
		this.totalOT15Hours = totalOT15Hours;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public TimesheetStatusMaster getTimesheetStatusMaster() {
		return timesheetStatusMaster;
	}

	public void setTimesheetStatusMaster(
			TimesheetStatusMaster timesheetStatusMaster) {
		this.timesheetStatusMaster = timesheetStatusMaster;
	}

	public Set<CoherentOvertimeApplicationDetail> getCoherentOvertimeApplicationDetails() {
		return coherentOvertimeApplicationDetails;
	}

	public void setCoherentOvertimeApplicationDetails(
			Set<CoherentOvertimeApplicationDetail> coherentOvertimeApplicationDetails) {
		this.coherentOvertimeApplicationDetails = coherentOvertimeApplicationDetails;
	}

	public Set<CoherentOvertimeApplicationReviewer> getCoherentOvertimeApplicationReviewers() {
		return coherentOvertimeApplicationReviewers;
	}

	public void setCoherentOvertimeApplicationReviewers(
			Set<CoherentOvertimeApplicationReviewer> coherentOvertimeApplicationReviewers) {
		this.coherentOvertimeApplicationReviewers = coherentOvertimeApplicationReviewers;
	}

	public Set<CoherentOvertimeApplicationWorkflow> getCoherentOvertimeApplicationWorkflows() {
		return coherentOvertimeApplicationWorkflows;
	}

	public void setCoherentOvertimeApplicationWorkflows(
			Set<CoherentOvertimeApplicationWorkflow> coherentOvertimeApplicationWorkflows) {
		this.coherentOvertimeApplicationWorkflows = coherentOvertimeApplicationWorkflows;
	}

}
