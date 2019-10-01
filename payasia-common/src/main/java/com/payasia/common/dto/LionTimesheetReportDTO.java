package com.payasia.common.dto;

import java.io.Serializable;
import java.util.List;

public class LionTimesheetReportDTO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<String> employeeNumbers;
	private List<String> dataDictNameList ;
	
	private List<LeaveReportHeaderDTO> timesheetHeaderDTOs;
	private List<LionTimesheetSummaryReportDTO> lionTimesheetStatusReportDTOs;
	private List<LeaveReportCustomDataDTO> leaveCustomDataDTOs;
	private int totalEmployeesCount;
	public List<String> getEmployeeNumbers() {
		return employeeNumbers;
	}
	public void setEmployeeNumbers(List<String> employeeNumbers) {
		this.employeeNumbers = employeeNumbers;
	}
	public List<String> getDataDictNameList() {
		return dataDictNameList;
	}
	public void setDataDictNameList(List<String> dataDictNameList) {
		this.dataDictNameList = dataDictNameList;
	}
	public List<LeaveReportHeaderDTO> getTimesheetHeaderDTOs() {
		return timesheetHeaderDTOs;
	}
	public void setTimesheetHeaderDTOs(
			List<LeaveReportHeaderDTO> timesheetHeaderDTOs) {
		this.timesheetHeaderDTOs = timesheetHeaderDTOs;
	}
	public List<LionTimesheetSummaryReportDTO> getLionTimesheetStatusReportDTOs() {
		return lionTimesheetStatusReportDTOs;
	}
	public void setLionTimesheetStatusReportDTOs(
			List<LionTimesheetSummaryReportDTO> lionTimesheetStatusReportDTOs) {
		this.lionTimesheetStatusReportDTOs = lionTimesheetStatusReportDTOs;
	}
	public List<LeaveReportCustomDataDTO> getLeaveCustomDataDTOs() {
		return leaveCustomDataDTOs;
	}
	public void setLeaveCustomDataDTOs(
			List<LeaveReportCustomDataDTO> leaveCustomDataDTOs) {
		this.leaveCustomDataDTOs = leaveCustomDataDTOs;
	}
	public int getTotalEmployeesCount() {
		return totalEmployeesCount;
	}
	public void setTotalEmployeesCount(int totalEmployeesCount) {
		this.totalEmployeesCount = totalEmployeesCount;
	}
	
	
	

}
