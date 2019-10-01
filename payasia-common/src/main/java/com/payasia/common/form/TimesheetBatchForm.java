package com.payasia.common.form;

import java.sql.Timestamp;
import java.util.Set;

import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.EmployeeTimesheetApplication;
import com.payasia.dao.bean.TimesheetBatch;

public class TimesheetBatchForm {

	private long timesheetBatchId;
	private String timesheetBatchDesc;
	private Timestamp endDate;
	private Timestamp startDate;
	private Company company;
	private Set<EmployeeTimesheetApplication> employeeTimesheetApplications;
	
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
	
	public TimesheetBatchForm(TimesheetBatch timesheetBatch){
	 this.timesheetBatchId=timesheetBatch.getTimesheetBatchId();
	 this.timesheetBatchDesc=timesheetBatch.getTimesheetBatchDesc();
	 this.endDate=timesheetBatch.getEndDate();
	 this.startDate=timesheetBatch.getStartDate();
	 this.company=timesheetBatch.getCompany();
	}
	
}
