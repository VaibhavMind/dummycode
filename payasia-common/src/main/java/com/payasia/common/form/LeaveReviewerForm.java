package com.payasia.common.form;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.payasia.common.dto.DataImportLogDTO;

public class LeaveReviewerForm implements Serializable{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7740078674078871024L;
	private String status;
	private String employeeStatus;
	private String filterIds;
	
	private String employeeName;
	private String leaveReviewer1;
	private String leaveReviewer2;
	private String leaveReviewer3;
	
	private Long leaveSchemeId;
	private String leaveSchemeName;
	
	private Long leaveReviewerId;
	private String leaveReviewerName;
	private Long employeeLeaveReviewerId;
	
	private Long employeeLeaveReviewerId1;
	private Long employeeLeaveReviewerId2;
	private Long employeeLeaveReviewerId3;
	
	private String ruleName;
	private String ruleValue;
	
	private Long leaveReviewerId1;
	private Long leaveReviewerId2;
	private Long leaveReviewerId3;
	
	
	private Long leaveReviewerRuleId;
	
	private Long leaveReviewerRuleId1;
	private Long leaveReviewerRuleId2;
	private Long leaveReviewerRuleId3;
	
	
	private Long employeeId;
	private Long leaveSchemeTypeId;
	private Long employeeLeaveSchemeTypeId;
	private Long employeeLeaveSchemeId;
	private Long leaveTypeId;
	private String leaveType;
	
	private boolean dataValid;
	private List<DataImportLogDTO> dataImportLogDTOs;
	private CommonsMultipartFile fileUpload;
	private boolean sameExcelField = false;
	private List<String> colName;
	private List<String> duplicateColNames;
	private List<HashMap<String, String>> importedData;
	
	
	public Long getLeaveSchemeTypeId() {
		return leaveSchemeTypeId;
	}

	public void setLeaveSchemeTypeId(Long leaveSchemeTypeId) {
		this.leaveSchemeTypeId = leaveSchemeTypeId;
	}

	public Long getEmployeeLeaveSchemeId() {
		return employeeLeaveSchemeId;
	}

	public void setEmployeeLeaveSchemeId(Long employeeLeaveSchemeId) {
		this.employeeLeaveSchemeId = employeeLeaveSchemeId;
	}

	public Long getLeaveTypeId() {
		return leaveTypeId;
	}

	public void setLeaveTypeId(Long leaveTypeId) {
		this.leaveTypeId = leaveTypeId;
	}

	public String getLeaveType() {
		return leaveType;
	}

	public void setLeaveType(String leaveType) {
		this.leaveType = leaveType;
	}

	public Long getLeaveReviewerId1() {
		return leaveReviewerId1;
	}

	public void setLeaveReviewerId1(Long leaveReviewerId1) {
		this.leaveReviewerId1 = leaveReviewerId1;
	}

	public Long getLeaveReviewerId2() {
		return leaveReviewerId2;
	}

	public void setLeaveReviewerId2(Long leaveReviewerId2) {
		this.leaveReviewerId2 = leaveReviewerId2;
	}

	public Long getLeaveReviewerId3() {
		return leaveReviewerId3;
	}

	public void setLeaveReviewerId3(Long leaveReviewerId3) {
		this.leaveReviewerId3 = leaveReviewerId3;
	}


	public String getEmployeeStatus() {
		return employeeStatus;
	}

	public void setEmployeeStatus(String employeeStatus) {
		this.employeeStatus = employeeStatus;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long getEmployeeLeaveReviewerId1() {
		return employeeLeaveReviewerId1;
	}

	public void setEmployeeLeaveReviewerId1(Long employeeLeaveReviewerId1) {
		this.employeeLeaveReviewerId1 = employeeLeaveReviewerId1;
	}

	public Long getEmployeeLeaveReviewerId2() {
		return employeeLeaveReviewerId2;
	}

	public void setEmployeeLeaveReviewerId2(Long employeeLeaveReviewerId2) {
		this.employeeLeaveReviewerId2 = employeeLeaveReviewerId2;
	}

	public Long getEmployeeLeaveReviewerId3() {
		return employeeLeaveReviewerId3;
	}

	public void setEmployeeLeaveReviewerId3(Long employeeLeaveReviewerId3) {
		this.employeeLeaveReviewerId3 = employeeLeaveReviewerId3;
	}

	public Long getEmployeeLeaveReviewerId() {
		return employeeLeaveReviewerId;
	}

	public void setEmployeeLeaveReviewerId(Long employeeLeaveReviewerId) {
		this.employeeLeaveReviewerId = employeeLeaveReviewerId;
	}

	public Long getLeaveReviewerId() {
		return leaveReviewerId;
	}

	public void setLeaveReviewerId(Long leaveReviewerId) {
		this.leaveReviewerId = leaveReviewerId;
	}

	public String getLeaveReviewerName() {
		return leaveReviewerName;
	}

	public void setLeaveReviewerName(String leaveReviewerName) {
		this.leaveReviewerName = leaveReviewerName;
	}


	public Long getLeaveReviewerRuleId() {
		return leaveReviewerRuleId;
	}

	public void setLeaveReviewerRuleId(Long leaveReviewerRuleId) {
		this.leaveReviewerRuleId = leaveReviewerRuleId;
	}

	public Long getLeaveReviewerRuleId1() {
		return leaveReviewerRuleId1;
	}

	public void setLeaveReviewerRuleId1(Long leaveReviewerRuleId1) {
		this.leaveReviewerRuleId1 = leaveReviewerRuleId1;
	}

	public Long getLeaveReviewerRuleId2() {
		return leaveReviewerRuleId2;
	}

	public void setLeaveReviewerRuleId2(Long leaveReviewerRuleId2) {
		this.leaveReviewerRuleId2 = leaveReviewerRuleId2;
	}

	public Long getLeaveReviewerRuleId3() {
		return leaveReviewerRuleId3;
	}

	public void setLeaveReviewerRuleId3(Long leaveReviewerRuleId3) {
		this.leaveReviewerRuleId3 = leaveReviewerRuleId3;
	}

	public Long getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}


	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	public String getRuleValue() {
		return ruleValue;
	}

	public void setRuleValue(String ruleValue) {
		this.ruleValue = ruleValue;
	}

	public Long getLeaveSchemeId() {
		return leaveSchemeId;
	}

	public void setLeaveSchemeId(Long leaveSchemeId) {
		this.leaveSchemeId = leaveSchemeId;
	}

	public String getLeaveSchemeName() {
		return leaveSchemeName;
	}

	public void setLeaveSchemeName(String leaveSchemeName) {
		this.leaveSchemeName = leaveSchemeName;
	}

	public String getLeaveReviewer1() {
		return leaveReviewer1;
	}

	public void setLeaveReviewer1(String leaveReviewer1) {
		this.leaveReviewer1 = leaveReviewer1;
	}

	public String getLeaveReviewer2() {
		return leaveReviewer2;
	}

	public void setLeaveReviewer2(String leaveReviewer2) {
		this.leaveReviewer2 = leaveReviewer2;
	}

	public String getLeaveReviewer3() {
		return leaveReviewer3;
	}

	public void setLeaveReviewer3(String leaveReviewer3) {
		this.leaveReviewer3 = leaveReviewer3;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public Long getEmployeeLeaveSchemeTypeId() {
		return employeeLeaveSchemeTypeId;
	}

	public void setEmployeeLeaveSchemeTypeId(Long employeeLeaveSchemeTypeId) {
		this.employeeLeaveSchemeTypeId = employeeLeaveSchemeTypeId;
	}

	public String getFilterIds() {
		return filterIds;
	}

	public void setFilterIds(String filterIds) {
		this.filterIds = filterIds;
	}

	public boolean isDataValid() {
		return dataValid;
	}

	public void setDataValid(boolean dataValid) {
		this.dataValid = dataValid;
	}

	public List<DataImportLogDTO> getDataImportLogDTOs() {
		return dataImportLogDTOs;
	}

	public void setDataImportLogDTOs(List<DataImportLogDTO> dataImportLogDTOs) {
		this.dataImportLogDTOs = dataImportLogDTOs;
	}

	public CommonsMultipartFile getFileUpload() {
		return fileUpload;
	}

	public void setFileUpload(CommonsMultipartFile fileUpload) {
		this.fileUpload = fileUpload;
	}

	public boolean isSameExcelField() {
		return sameExcelField;
	}

	public void setSameExcelField(boolean sameExcelField) {
		this.sameExcelField = sameExcelField;
	}

	public List<String> getColName() {
		return colName;
	}

	public void setColName(List<String> colName) {
		this.colName = colName;
	}

	public List<String> getDuplicateColNames() {
		return duplicateColNames;
	}

	public void setDuplicateColNames(List<String> duplicateColNames) {
		this.duplicateColNames = duplicateColNames;
	}

	public List<HashMap<String, String>> getImportedData() {
		return importedData;
	}

	public void setImportedData(List<HashMap<String, String>> importedData) {
		this.importedData = importedData;
	}
	

}
