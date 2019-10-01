package com.payasia.dao.bean;

import java.io.Serializable;
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
 * The persistent class for the Coherent_Shift_Application database table.
 * 
 */
@Entity
@Table(name = "Coherent_Shift_Application")
public class CoherentShiftApplication extends BaseEntity implements
		Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name = "Shift_Application_ID")
	private long shiftApplicationID;

	@ManyToOne
	@JoinColumn(name = "Company_ID")
	private Company company;

	@ManyToOne
	@JoinColumn(name = "Employee_ID")
	private Employee employee;

	@ManyToOne
	@JoinColumn(name = "Timesheet_Batch_ID")
	private TimesheetBatch timesheetBatch;

	// @ManyToOne
	// @JoinColumn(name = "Shift_Type")
	// private AppCodeMaster shiftType;

	@Column(name = "Total_Shifts")
	private Integer totalShifts;

	@ManyToOne
	@JoinColumn(name = "Timesheet_Status_ID")
	private TimesheetStatusMaster timesheetStatusMaster;

	@OneToMany(mappedBy = "coherentShiftApplication")
	private Set<CoherentShiftApplicationReviewer> coherentShiftApplicationReviewer;

	@OneToMany(mappedBy = "coherentShiftApplication")
	private Set<CoherentShiftApplicationWorkflow> coherentShiftApplicationWorkflows;

	public long getShiftApplicationID() {
		return shiftApplicationID;
	}

	public Set<CoherentShiftApplicationReviewer> getCoherentShiftApplicationReviewer() {
		return coherentShiftApplicationReviewer;
	}

	public void setCoherentShiftApplicationReviewer(
			Set<CoherentShiftApplicationReviewer> coherentShiftApplicationReviewer) {
		this.coherentShiftApplicationReviewer = coherentShiftApplicationReviewer;
	}

	public Set<CoherentShiftApplicationWorkflow> getCoherentShiftApplicationWorkflows() {
		return coherentShiftApplicationWorkflows;
	}

	public void setCoherentShiftApplicationWorkflows(
			Set<CoherentShiftApplicationWorkflow> coherentShiftApplicationWorkflows) {
		this.coherentShiftApplicationWorkflows = coherentShiftApplicationWorkflows;
	}

	public void setShiftApplicationID(long shiftApplicationID) {
		this.shiftApplicationID = shiftApplicationID;
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

	// public AppCodeMaster getShiftType() {
	// return shiftType;
	// }
	//
	// public void setShiftType(AppCodeMaster shiftType) {
	// this.shiftType = shiftType;
	// }

	public Integer getTotalShifts() {
		return totalShifts;
	}

	public void setTotalShifts(Integer totalShifts) {
		this.totalShifts = totalShifts;
	}

	public TimesheetStatusMaster getTimesheetStatusMaster() {
		return timesheetStatusMaster;
	}

	public void setTimesheetStatusMaster(
			TimesheetStatusMaster timesheetStatusMaster) {
		this.timesheetStatusMaster = timesheetStatusMaster;
	}

}
