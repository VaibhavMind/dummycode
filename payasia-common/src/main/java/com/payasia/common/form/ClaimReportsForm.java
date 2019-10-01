package com.payasia.common.form;

import java.util.Arrays;

public class ClaimReportsForm {

	private Long employeeId;
	private String employeeName;
	private String employeeNumber;
	private String reportName;
	private String date;
	private String selectAllEmp;
	private String reportOptions;
	private String startDate;
	private String endDate;
	private Boolean isAllEmployees;
	private String employeeIds;
	private int year;
	private Integer reportYear;
	private String fileType;
	private String claimCategoryName;
	private Long claimCategoryId;
	private Long[] multipleClaimCategoryId;
	private String claimTemplateName;
	private Long claimTemplateId;
	private Long[] multipleClaimTemplateId;
	private String claimItemName;
	private Long claimItemId;
	private Long[] multipleClaimItemId;
	private String claimBatchName;
	private Long claimBatchId;
	private Long statusId;
	private String statusName;
	private String claimDetailsStatusId;
	private String groupById;
	private String groupByName;
	private String metaData;
	private boolean isShortList;
	private String sortBy;
	private String companyIds;
	private String groupId;
	private String paidStatus;
	private boolean includeResignedEmployees;
	private boolean includeSubordinateEmployees;
	
	public String getMetaData() {
		return metaData;
	}

	public void setMetaData(String metaData) {
		this.metaData = metaData;
	}

	public String getClaimDetailsStatusId() {
		return claimDetailsStatusId;
	}

	public void setClaimDetailsStatusId(String claimDetailsStatusId) {
		this.claimDetailsStatusId = claimDetailsStatusId;
	}

	public String getGroupById() {
		return groupById;
	}

	public void setGroupById(String groupById) {
		this.groupById = groupById;
	}

	public String getClaimBatchName() {
		return claimBatchName;
	}

	public void setClaimBatchName(String claimBatchName) {
		this.claimBatchName = claimBatchName;
	}

	public Long getClaimBatchId() {
		return claimBatchId;
	}

	public void setClaimBatchId(Long claimBatchId) {
		this.claimBatchId = claimBatchId;
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

	public Boolean getIsAllEmployees() {
		return isAllEmployees;
	}

	public void setIsAllEmployees(Boolean isAllEmployees) {
		this.isAllEmployees = isAllEmployees;
	}

	public String getEmployeeIds() {
		return employeeIds;
	}

	public void setEmployeeIds(String employeeIds) {
		this.employeeIds = employeeIds;
	}

	public Integer getReportYear() {
		return reportYear;
	}

	public void setReportYear(Integer reportYear) {
		this.reportYear = reportYear;
	}

	public String getClaimCategoryName() {
		return claimCategoryName;
	}

	public void setClaimCategoryName(String claimCategoryName) {
		this.claimCategoryName = claimCategoryName;
	}

	public Long getClaimCategoryId() {
		return claimCategoryId;
	}

	public void setClaimCategoryId(Long claimCategoryId) {
		this.claimCategoryId = claimCategoryId;
	}

	public String getClaimTemplateName() {
		return claimTemplateName;
	}

	public void setClaimTemplateName(String claimTemplateName) {
		this.claimTemplateName = claimTemplateName;
	}

	public Long getClaimTemplateId() {
		return claimTemplateId;
	}

	public void setClaimTemplateId(Long claimTemplateId) {
		this.claimTemplateId = claimTemplateId;
	}

	public String getClaimItemName() {
		return claimItemName;
	}

	public void setClaimItemName(String claimItemName) {
		this.claimItemName = claimItemName;
	}

	public Long getClaimItemId() {
		return claimItemId;
	}

	public void setClaimItemId(Long claimItemId) {
		this.claimItemId = claimItemId;
	}

	public Long getStatusId() {
		return statusId;
	}

	public void setStatusId(Long statusId) {
		this.statusId = statusId;
	}

	public String getStatusName() {
		return statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}

	public String getGroupByName() {
		return groupByName;
	}

	public void setGroupByName(String groupByName) {
		this.groupByName = groupByName;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public String getSortBy() {
		return sortBy;
	}

	public void setSortBy(String sortBy) {
		this.sortBy = sortBy;
	}

	public Boolean getIsShortList() {
		return isShortList;
	}

	public void setIsShortList(Boolean isShortList) {
		this.isShortList = isShortList;
	}

	public Long[] getMultipleClaimTemplateId() {
		return multipleClaimTemplateId;
	}

	public void setMultipleClaimTemplateId(Long[] multipleClaimTemplateId) {
		if(multipleClaimTemplateId !=null){
			   this.multipleClaimTemplateId = Arrays.copyOf(multipleClaimTemplateId, multipleClaimTemplateId.length); 
		}
	}

	public Long[] getMultipleClaimItemId() {
		return multipleClaimItemId;
	}

	public void setMultipleClaimItemId(Long[] multipleClaimItemId) {
		if(multipleClaimItemId !=null){
			   this.multipleClaimItemId = Arrays.copyOf(multipleClaimItemId, multipleClaimItemId.length); 
		}
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

	public Long[] getMultipleClaimCategoryId() {
		return multipleClaimCategoryId;
	}

	public void setMultipleClaimCategoryId(Long[] multipleClaimCategoryId) {
		this.multipleClaimCategoryId = multipleClaimCategoryId;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getPaidStatus() {
		return paidStatus;
	}

	public void setPaidStatus(String paidStatus) {
		this.paidStatus = paidStatus;
	}

	public boolean isIncludeResignedEmployees() {
		return includeResignedEmployees;
	}

	public void setIncludeResignedEmployees(boolean includeResignedEmployees) {
		this.includeResignedEmployees = includeResignedEmployees;
	}

	public boolean isIncludeSubordinateEmployees() {
		return includeSubordinateEmployees;
	}

	public void setIncludeSubordinateEmployees(boolean includeSubordinateEmployees) {
		this.includeSubordinateEmployees = includeSubordinateEmployees;
	}

}
