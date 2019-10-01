package com.payasia.common.form;

import java.util.List;

import com.payasia.dao.bean.LionEmployeeTimesheetApplicationDetail;


public class LionTimesheetDTO {

	private String startDate;
	private String endDate;
	private long timesheetBatchId;
	private List<LionEmployeeTimesheetApplicationDetail> lionEmployeeTimesheetApplicationDetail;
	private long employeeId;
	private String employeeName;
	private String location;
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public long getTimesheetBatchId() {
		return timesheetBatchId;
	}
	public void setTimesheetBatchId(long timesheetBatchId) {
		this.timesheetBatchId = timesheetBatchId;
	}
	public List<LionEmployeeTimesheetApplicationDetail> getLionEmployeeTimesheetApplicationDetail() {
		return lionEmployeeTimesheetApplicationDetail;
	}
	public void setLionEmployeeTimesheetApplicationDetail(
			List<LionEmployeeTimesheetApplicationDetail> lionEmployeeTimesheetApplicationDetail) {
		this.lionEmployeeTimesheetApplicationDetail = lionEmployeeTimesheetApplicationDetail;
	}
	public long getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(long employeeId) {
		this.employeeId = employeeId;
	}
	public String getEmployeeName() {
		return employeeName;
	}
	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
		
}
