package com.payasia.common.form;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.payasia.common.dto.DataImportLogDTO;

public class ClaimReviewerForm extends PageResponse implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7956288788254413087L;
	private String status;
	private String employeeStatus;
	private String employeeName;
	
	private String claimReviewer1;
	private String claimReviewer2;
	private String claimReviewer3;
	
	private Long claimSchemeId;
	private String claimSchemeName;
	private Long claimReviewerId;
	private String claimReviewerName;
	
	private Long employeeClaimReviewerId;
	private Long employeeClaimReviewerId1;
	private Long employeeClaimReviewerId2;
	private Long employeeClaimReviewerId3;
	
	private String ruleName;
	private String ruleValue;
	
	private Long claimReiveiwerId1;
	private Long claimReiveiwerId2;
	private Long claimReiveiwerId3;
	
	private Long claimReviewerRuleId;
	private Long claimReviewerRuleId1;
	private Long claimReviewerRuleId2;
	private Long claimReviewerRuleId3;
	private Long employeeId;
	private List<ClaimReviewerForm> rows;
	
	private Long employeeClaimTemplateId;
	private Long claimTemplateId;
	private String claimTemplateName;
	
	private String filterIds;
	
	private boolean dataValid;
	private List<DataImportLogDTO> dataImportLogDTOs;
	private CommonsMultipartFile fileUpload;
	private boolean sameExcelField = false;
	private List<String> colName;
	private List<String> duplicateColNames;
	private List<HashMap<String, String>> importedData;
	
	
	public String getFilterIds() {
		return filterIds;
	}

	public void setFilterIds(String filterIds) {
		this.filterIds = filterIds;
	}

	public Long getEmployeeClaimTemplateId() {
		return employeeClaimTemplateId;
	}

	public void setEmployeeClaimTemplateId(Long employeeClaimTemplateId) {
		this.employeeClaimTemplateId = employeeClaimTemplateId;
	}

	public Long getClaimTemplateId() {
		return claimTemplateId;
	}

	public void setClaimTemplateId(Long claimTemplateId) {
		this.claimTemplateId = claimTemplateId;
	}

	public String getClaimTemplateName() {
		return claimTemplateName;
	}

	public void setClaimTemplateName(String claimTemplateName) {
		this.claimTemplateName = claimTemplateName;
	}

	/** The search employee list. */
	private List<EmployeeListForm> searchEmployeeList;
	
	public String getEmployeeStatus() {
		return employeeStatus;
	}

	public void setEmployeeStatus(String employeeStatus) {
		this.employeeStatus = employeeStatus;
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


	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public String getClaimReviewer1() {
		return claimReviewer1;
	}

	public void setClaimReviewer1(String claimReviewer1) {
		this.claimReviewer1 = claimReviewer1;
	}

	public String getClaimReviewer2() {
		return claimReviewer2;
	}

	public void setClaimReviewer2(String claimReviewer2) {
		this.claimReviewer2 = claimReviewer2;
	}

	public String getClaimReviewer3() {
		return claimReviewer3;
	}

	public void setClaimReviewer3(String claimReviewer3) {
		this.claimReviewer3 = claimReviewer3;
	}

	public Long getClaimSchemeId() {
		return claimSchemeId;
	}

	public void setClaimSchemeId(Long claimSchemeId) {
		this.claimSchemeId = claimSchemeId;
	}

	public String getClaimSchemeName() {
		return claimSchemeName;
	}

	public void setClaimSchemeName(String claimSchemeName) {
		this.claimSchemeName = claimSchemeName;
	}

	public Long getClaimReviewerId() {
		return claimReviewerId;
	}

	public void setClaimReviewerId(Long claimReviewerId) {
		this.claimReviewerId = claimReviewerId;
	}

	public String getClaimReviewerName() {
		return claimReviewerName;
	}

	public void setClaimReviewerName(String claimReviewerName) {
		this.claimReviewerName = claimReviewerName;
	}
 


	

	



	public Long getEmployeeClaimReviewerId() {
		return employeeClaimReviewerId;
	}

	public void setEmployeeClaimReviewerId(Long employeeClaimReviewerId) {
		this.employeeClaimReviewerId = employeeClaimReviewerId;
	}

	public Long getEmployeeClaimReviewerId1() {
		return employeeClaimReviewerId1;
	}

	public void setEmployeeClaimReviewerId1(Long employeeClaimReviewerId1) {
		this.employeeClaimReviewerId1 = employeeClaimReviewerId1;
	}

	public Long getEmployeeClaimReviewerId2() {
		return employeeClaimReviewerId2;
	}

	public void setEmployeeClaimReviewerId2(Long employeeClaimReviewerId2) {
		this.employeeClaimReviewerId2 = employeeClaimReviewerId2;
	}

	public Long getEmployeeClaimReviewerId3() {
		return employeeClaimReviewerId3;
	}

	public void setEmployeeClaimReviewerId3(Long employeeClaimReviewerId3) {
		this.employeeClaimReviewerId3 = employeeClaimReviewerId3;
	}

	public Long getClaimReiveiwerId1() {
		return claimReiveiwerId1;
	}

	public void setClaimReiveiwerId1(Long claimReiveiwerId1) {
		this.claimReiveiwerId1 = claimReiveiwerId1;
	}

	public Long getClaimReiveiwerId2() {
		return claimReiveiwerId2;
	}

	public void setClaimReiveiwerId2(Long claimReiveiwerId2) {
		this.claimReiveiwerId2 = claimReiveiwerId2;
	}

	public Long getClaimReiveiwerId3() {
		return claimReiveiwerId3;
	}

	public void setClaimReiveiwerId3(Long claimReiveiwerId3) {
		this.claimReiveiwerId3 = claimReiveiwerId3;
	}

	public Long getClaimReviewerRuleId() {
		return claimReviewerRuleId;
	}

	public void setClaimReviewerRuleId(Long claimReviewerRuleId) {
		this.claimReviewerRuleId = claimReviewerRuleId;
	}

	public Long getClaimReviewerRuleId1() {
		return claimReviewerRuleId1;
	}

	public void setClaimReviewerRuleId1(Long claimReviewerRuleId1) {
		this.claimReviewerRuleId1 = claimReviewerRuleId1;
	}

	public Long getClaimReviewerRuleId2() {
		return claimReviewerRuleId2;
	}

	public void setClaimReviewerRuleId2(Long claimReviewerRuleId2) {
		this.claimReviewerRuleId2 = claimReviewerRuleId2;
	}

	public Long getClaimReviewerRuleId3() {
		return claimReviewerRuleId3;
	}

	public void setClaimReviewerRuleId3(Long claimReviewerRuleId3) {
		this.claimReviewerRuleId3 = claimReviewerRuleId3;
	}

	public Long getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}

	public List<ClaimReviewerForm> getRows() {
		return rows;
	}

	public void setRows(List<ClaimReviewerForm> rows) {
		this.rows = rows;
	}

	public List<EmployeeListForm> getSearchEmployeeList() {
		return searchEmployeeList;
	}

	public void setSearchEmployeeList(List<EmployeeListForm> searchEmployeeList) {
		this.searchEmployeeList = searchEmployeeList;
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
