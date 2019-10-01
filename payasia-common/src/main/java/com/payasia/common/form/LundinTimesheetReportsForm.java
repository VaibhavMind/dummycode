package com.payasia.common.form;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class LundinTimesheetReportsForm implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2115000903395091547L;
	private Long employeeId;
	private String employeeName;
	private String employeeNumber;
	private String reportName;
	private String batchDesc;
	private Long otBatchId;
	private int defaultBatch;
	private String empTimsheetRepYear;
	private String selectAllEmp;
	private String reportOptions;
	private Long fromBatchId;
	private Long toBatchId;
	private String employeeIds;
	private String metaData;
	private int year;
	private Boolean dateRange;
	private Integer reportYear;
	private boolean includeApprovalCancel;
	private boolean includeResignedEmployees;
	
	private Long departmentId;
	private String departmentName;
	private Long blockId;
	private String blockName;
	private String blockCode;
	private Long afeId;
	private String afeName;
	private String afeCode;
	private Long defaultBlockCodeId;
	private Long defaultAfeId;
	private boolean isShortList ;
	private String timesheetStatus;
	private String sortBy;
	public String getSortBy() {
		return sortBy;
	}
	public void setSortBy(String sortBy) {
		this.sortBy = sortBy;
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
	public String getBatchDesc() {
		return batchDesc;
	}
	public void setBatchDesc(String batchDesc) {
		this.batchDesc = batchDesc;
	}
	public Long getOtBatchId() {
		return otBatchId;
	}
	public void setOtBatchId(Long otBatchId) {
		this.otBatchId = otBatchId;
	}
	public String getEmpTimsheetRepYear() {
		return empTimsheetRepYear;
	}
	public void setEmpTimsheetRepYear(String empTimsheetRepYear) {
		this.empTimsheetRepYear = empTimsheetRepYear;
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
	public String getEmployeeIds() {
		return employeeIds;
	}
	public void setEmployeeIds(String employeeIds) {
		this.employeeIds = employeeIds;
	}
	public String getMetaData() {
		return metaData;
	}
	public void setMetaData(String metaData) {
		this.metaData = metaData;
	}
	public Boolean getDateRange() {
		return dateRange;
	}
	public void setDateRange(Boolean dateRange) {
		this.dateRange = dateRange;
	}
	public Integer getReportYear() {
		return reportYear;
	}
	public void setReportYear(Integer reportYear) {
		this.reportYear = reportYear;
	}
	public boolean isIncludeApprovalCancel() {
		return includeApprovalCancel;
	}
	public void setIncludeApprovalCancel(boolean includeApprovalCancel) {
		this.includeApprovalCancel = includeApprovalCancel;
	}
	public boolean isIncludeResignedEmployees() {
		return includeResignedEmployees;
	}
	public void setIncludeResignedEmployees(boolean includeResignedEmployees) {
		this.includeResignedEmployees = includeResignedEmployees;
	}
	public Long getDepartmentId() {
		return departmentId;
	}
	public void setDepartmentId(Long departmentId) {
		this.departmentId = departmentId;
	}
	public String getDepartmentName() {
		return departmentName;
	}
	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}
	public Long getBlockId() {
		return blockId;
	}
	public void setBlockId(Long blockId) {
		this.blockId = blockId;
	}
	public String getBlockName() {
		return blockName;
	}
	public void setBlockName(String blockName) {
		this.blockName = blockName;
	}
	public Long getAfeId() {
		return afeId;
	}
	public void setAfeId(Long afeId) {
		this.afeId = afeId;
	}
	public String getAfeName() {
		return afeName;
	}
	public void setAfeName(String afeName) {
		this.afeName = afeName;
	}
	public Long getFromBatchId() {
		return fromBatchId;
	}
	public void setFromBatchId(Long fromBatchId) {
		this.fromBatchId = fromBatchId;
	}
	public Long getToBatchId() {
		return toBatchId;
	}
	public void setToBatchId(Long toBatchId) {
		this.toBatchId = toBatchId;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public Long getDefaultBlockCodeId() {
		return defaultBlockCodeId;
	}
	public void setDefaultBlockCodeId(Long defaultBlockCodeId) {
		this.defaultBlockCodeId = defaultBlockCodeId;
	}
	public Long getDefaultAfeId() {
		return defaultAfeId;
	}
	public void setDefaultAfeId(Long defaultAfeId) {
		this.defaultAfeId = defaultAfeId;
	}
	
	public Boolean getIsShortList() {
		return isShortList;
	}
	public void setIsShortList(Boolean isShortList) {
		this.isShortList = isShortList;
	}
	public int getDefaultBatch() {
		return defaultBatch;
	}
	public void setDefaultBatch(int defaultBatch) {
		this.defaultBatch = defaultBatch;
	}
	public String getBlockCode() {
		return blockCode;
	}
	public void setBlockCode(String blockCode) {
		this.blockCode = blockCode;
	}
	public String getAfeCode() {
		return afeCode;
	}
	public void setAfeCode(String afeCode) {
		this.afeCode = afeCode;
	}
	public String getTimesheetStatus() {
		return timesheetStatus;
	}
	public void setTimesheetStatus(String timesheetStatus) {
		this.timesheetStatus = timesheetStatus;
	}
	
	
}
