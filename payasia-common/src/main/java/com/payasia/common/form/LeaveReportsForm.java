package com.payasia.common.form;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_DEFAULT)
public class LeaveReportsForm {

	private Long employeeId;
	private String employeeName;
	private String employeeNumber;
	private String reportName;
	private String leaveType;
	private Long leaveTypeId;
	private Long[] multipleLeaveTypeId;
	private String date;
	private String selectAllEmp;
	private String reportOptions;
	private String startDate;
	private String endDate;
	private Long leaveTransactionId;
	private String leaveTransactionName;
	private String[] multipleLeaveTransactionName;
	private Boolean isAllEmployees;
	private String leaveBalAsOnDate;
	private String employeeIds;
	private boolean isShortList ;
	private String metaData;
	private Boolean year;
	private Boolean dateRange;
	private Integer reportYear;
	private String yearWiseFromDate;
	private String yearWiseToDate;
	private String multiSheet;
	private boolean multipleRecord;
	private boolean includeApprovalCancel;
	private boolean includeResignedEmployees;
	private String leaveBalAsOnDateCustomRep;
	private String fileType;
	private Long leaveReviewerId;
	private String companyIds;
	private String groupId;
	
	private String[] dataDictionaryIds;
	
	public boolean isMultipleRecord() {
		return multipleRecord;
	}
	public void setMultipleRecord(boolean multipleRecord) {
		this.multipleRecord = multipleRecord;
	}
	public boolean isIncludeApprovalCancel() {
		return includeApprovalCancel;
	}
	public void setIncludeApprovalCancel(boolean includeApprovalCancel) {
		this.includeApprovalCancel = includeApprovalCancel;
	}
	public void setShortList(boolean isShortList) {
		this.isShortList = isShortList;
	}
	public Integer getReportYear() {
		return reportYear;
	}
	public void setReportYear(Integer reportYear) {
		this.reportYear = reportYear;
	}
	public String getYearWiseFromDate() {
		return yearWiseFromDate;
	}
	public void setYearWiseFromDate(String yearWiseFromDate) {
		this.yearWiseFromDate = yearWiseFromDate;
	}
	public String getYearWiseToDate() {
		return yearWiseToDate;
	}
	public void setYearWiseToDate(String yearWiseToDate) {
		this.yearWiseToDate = yearWiseToDate;
	}
	
	public Boolean getYear() {
		return year;
	}
	public void setYear(Boolean year) {
		this.year = year;
	}
	public Boolean getDateRange() {
		return dateRange;
	}
	public void setDateRange(Boolean dateRange) {
		this.dateRange = dateRange;
	}
	public String getMetaData() {
		return metaData;
	}
	public void setMetaData(String metaData) {
		this.metaData = metaData;
	}
	public Boolean getIsShortList() {
		return isShortList;
	}
	public void setIsShortList(Boolean isShortList) {
		this.isShortList = isShortList;
	}
	public String getEmployeeIds() {
		return employeeIds;
	}
	public void setEmployeeIds(String employeeIds) {
		this.employeeIds = employeeIds;
	}
	public String getLeaveBalAsOnDate() {
		return leaveBalAsOnDate;
	}
	public void setLeaveBalAsOnDate(String leaveBalAsOnDate) {
		this.leaveBalAsOnDate = leaveBalAsOnDate;
	}
	public Boolean getIsAllEmployees() {
		return isAllEmployees;
	}
	public void setIsAllEmployees(Boolean isAllEmployees) {
		this.isAllEmployees = isAllEmployees;
	}
	public Long getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}
	public String getEmployeeName() {
		return employeeName;
	}
	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}
	public String getEmployeeNumber() {
		return employeeNumber;
	}
	public void setEmployeeNumber(String employeeNumber) {
		this.employeeNumber = employeeNumber;
	}
	public String getReportName() {
		return reportName;
	}
	public void setReportName(String reportName) {
		this.reportName = reportName;
	}
	public String getLeaveType() {
		return leaveType;
	}
	public void setLeaveType(String leaveType) {
		this.leaveType = leaveType;
	}
	public Long getLeaveTypeId() {
		return leaveTypeId;
	}
	public void setLeaveTypeId(Long leaveTypeId) {
		this.leaveTypeId = leaveTypeId;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getSelectAllEmp() {
		return selectAllEmp;
	}
	public void setSelectAllEmp(String selectAllEmp) {
		this.selectAllEmp = selectAllEmp;
	}
	public String getReportOptions() {
		return reportOptions;
	}
	public void setReportOptions(String reportOptions) {
		this.reportOptions = reportOptions;
	}
	public String getLeaveTransactionName() {
		return leaveTransactionName;
	}
	public void setLeaveTransactionName(String leaveTransactionName) {
		this.leaveTransactionName = leaveTransactionName;
	}
	public Long getLeaveTransactionId() {
		return leaveTransactionId;
	}
	public void setLeaveTransactionId(Long leaveTransactionId) {
		this.leaveTransactionId = leaveTransactionId;
	}
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
	public String getMultiSheet() {
		return multiSheet;
	}
	public void setMultiSheet(String multiSheet) {
		this.multiSheet = multiSheet;
	}
	public boolean isIncludeResignedEmployees() {
		return includeResignedEmployees;
	}
	public void setIncludeResignedEmployees(boolean includeResignedEmployees) {
		this.includeResignedEmployees = includeResignedEmployees;
	}
	public String getLeaveBalAsOnDateCustomRep() {
		return leaveBalAsOnDateCustomRep;
	}
	public void setLeaveBalAsOnDateCustomRep(String leaveBalAsOnDateCustomRep) {
		this.leaveBalAsOnDateCustomRep = leaveBalAsOnDateCustomRep;
	}
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public Long[] getMultipleLeaveTypeId() {
		return multipleLeaveTypeId;
	}
	public void setMultipleLeaveTypeId(Long[] multipleLeaveTypeId) {
		if(multipleLeaveTypeId !=null){
			   this.multipleLeaveTypeId = Arrays.copyOf(multipleLeaveTypeId, multipleLeaveTypeId.length); 
		} 
	}
	public Long getLeaveReviewerId() {
		return leaveReviewerId;
	}
	public void setLeaveReviewerId(Long leaveReviewerId) {
		this.leaveReviewerId = leaveReviewerId;
	}
	public String[] getMultipleLeaveTransactionName() {
		return multipleLeaveTransactionName;
	}
	public void setMultipleLeaveTransactionName(
			String[] multipleLeaveTransactionName) {
		this.multipleLeaveTransactionName = multipleLeaveTransactionName;
	}
	public String getCompanyIds() {
		return companyIds;
	}
	public void setCompanyIds(String companyIds) {
		this.companyIds = companyIds;
	}
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	
	public String[] getDataDictionaryIds() {
		return dataDictionaryIds;
	}
	public void setDataDictionaryIds(String[] dataDictionaryIds) {
		this.dataDictionaryIds = dataDictionaryIds;
	}
	
}
