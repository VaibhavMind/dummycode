package com.payasia.common.form;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.payasia.common.dto.DataImportLogDTO;

public class LundinTimesheetReviewerForm implements Serializable{
	
	private static final long serialVersionUID = -9041056258686795781L;
	private String status;
	private String employeeStatus;
	private String employeeName;
	private String lundinReviewer1;
	private String lundinReviewer2;
	private String lundinReviewer3;
	
	private Long lundinReviewerId;
	private String lundinReviewerName;
	private Long employeeLundinReviewerId;
	
	private Long employeeLundinReviewerId1;
	private Long employeeLundinReviewerId2;
	private Long employeeLundinReviewerId3;
	
	private String ruleName;
	private String ruleValue;
	
	private Long lundinReviewerId1;
	private Long lundinReviewerId2;
	private Long lundinReviewerId3;
	
	
	private Long lundinReviewerRuleId;
	
	private Long lundinReviewerRuleId1;
	private Long lundinReviewerRuleId2;
	private Long lundinReviewerRuleId3;
	
	private String email;
	
	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	private Long employeeId;
   private String filterIds;
   
   
   private String workFlowLevel;
	private Boolean allowOverrideL1;
	private Boolean allowOverrideL2;
	private Boolean allowOverrideL3;
	private Boolean allowRejectL1;
	private Boolean allowRejectL2;
	private Boolean allowRejectL3;
	
	private Boolean  allowForward1;
	private Boolean  allowForward2;
	private Boolean  allowForward3;
	
	private Boolean  allowApprove1;
	private Boolean  allowApprove2;
	private Boolean  allowApprove3;
	
	private boolean dataValid;
	private List<DataImportLogDTO> dataImportLogDTOs;

	private CommonsMultipartFile fileUpload;

	private boolean sameExcelField = false;
	private List<String> colName;
	private List<String> duplicateColNames;
	private List<HashMap<String, String>> importedData;
	
	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public String getEmployeeStatus() {
		return employeeStatus;
	}


	public void setEmployeeStatus(String employeeStatus) {
		this.employeeStatus = employeeStatus;
	}


	public String getEmployeeName() {
		return employeeName;
	}


	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}


	public String getLundinReviewer1() {
		return lundinReviewer1;
	}


	public void setLundinReviewer1(String lundinReviewer1) {
		this.lundinReviewer1 = lundinReviewer1;
	}


	public String getLundinReviewer2() {
		return lundinReviewer2;
	}


	public void setLundinReviewer2(String lundinReviewer2) {
		this.lundinReviewer2 = lundinReviewer2;
	}


	public String getLundinReviewer3() {
		return lundinReviewer3;
	}


	public void setLundinReviewer3(String lundinReviewer3) {
		this.lundinReviewer3 = lundinReviewer3;
	}


	public Long getLundinReviewerId() {
		return lundinReviewerId;
	}


	public void setLundinReviewerId(Long lundinReviewerId) {
		this.lundinReviewerId = lundinReviewerId;
	}


	public String getLundinReviewerName() {
		return lundinReviewerName;
	}


	public void setLundinReviewerName(String lundinReviewerName) {
		this.lundinReviewerName = lundinReviewerName;
	}


	public Long getEmployeeLundinReviewerId() {
		return employeeLundinReviewerId;
	}


	public void setEmployeeLundinReviewerId(Long employeeLundinReviewerId) {
		this.employeeLundinReviewerId = employeeLundinReviewerId;
	}


	public Long getEmployeeLundinReviewerId1() {
		return employeeLundinReviewerId1;
	}


	public void setEmployeeLundinReviewerId1(Long employeeLundinReviewerId1) {
		this.employeeLundinReviewerId1 = employeeLundinReviewerId1;
	}


	public Long getEmployeeLundinReviewerId2() {
		return employeeLundinReviewerId2;
	}


	public void setEmployeeLundinReviewerId2(Long employeeLundinReviewerId2) {
		this.employeeLundinReviewerId2 = employeeLundinReviewerId2;
	}


	public Long getEmployeeLundinReviewerId3() {
		return employeeLundinReviewerId3;
	}


	public void setEmployeeLundinReviewerId3(Long employeeLundinReviewerId3) {
		this.employeeLundinReviewerId3 = employeeLundinReviewerId3;
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


	public Long getLundinReviewerId1() {
		return lundinReviewerId1;
	}


	public void setLundinReviewerId1(Long lundinReviewerId1) {
		this.lundinReviewerId1 = lundinReviewerId1;
	}


	public Long getLundinReviewerId2() {
		return lundinReviewerId2;
	}


	public void setLundinReviewerId2(Long lundinReviewerId2) {
		this.lundinReviewerId2 = lundinReviewerId2;
	}


	public Long getLundinReviewerId3() {
		return lundinReviewerId3;
	}


	public void setLundinReviewerId3(Long lundinReviewerId3) {
		this.lundinReviewerId3 = lundinReviewerId3;
	}


	public Long getLundinReviewerRuleId() {
		return lundinReviewerRuleId;
	}


	public void setLundinReviewerRuleId(Long lundinReviewerRuleId) {
		this.lundinReviewerRuleId = lundinReviewerRuleId;
	}


	public Long getLundinReviewerRuleId1() {
		return lundinReviewerRuleId1;
	}


	public void setLundinReviewerRuleId1(Long lundinReviewerRuleId1) {
		this.lundinReviewerRuleId1 = lundinReviewerRuleId1;
	}


	public Long getLundinReviewerRuleId2() {
		return lundinReviewerRuleId2;
	}


	public void setLundinReviewerRuleId2(Long lundinReviewerRuleId2) {
		this.lundinReviewerRuleId2 = lundinReviewerRuleId2;
	}


	public Long getLundinReviewerRuleId3() {
		return lundinReviewerRuleId3;
	}


	public void setLundinReviewerRuleId3(Long lundinReviewerRuleId3) {
		this.lundinReviewerRuleId3 = lundinReviewerRuleId3;
	}


	public Long getEmployeeId() {
		return employeeId;
	}


	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}


	public String getFilterIds() {
		return filterIds;
	}


	public void setFilterIds(String filterIds) {
		this.filterIds = filterIds;
	}


	public String getWorkFlowLevel() {
		return workFlowLevel;
	}


	public void setWorkFlowLevel(String workFlowLevel) {
		this.workFlowLevel = workFlowLevel;
	}


	public Boolean getAllowOverrideL1() {
		return allowOverrideL1;
	}


	public void setAllowOverrideL1(Boolean allowOverrideL1) {
		this.allowOverrideL1 = allowOverrideL1;
	}


	public Boolean getAllowOverrideL2() {
		return allowOverrideL2;
	}


	public void setAllowOverrideL2(Boolean allowOverrideL2) {
		this.allowOverrideL2 = allowOverrideL2;
	}


	public Boolean getAllowOverrideL3() {
		return allowOverrideL3;
	}


	public void setAllowOverrideL3(Boolean allowOverrideL3) {
		this.allowOverrideL3 = allowOverrideL3;
	}


	public Boolean getAllowRejectL1() {
		return allowRejectL1;
	}


	public void setAllowRejectL1(Boolean allowRejectL1) {
		this.allowRejectL1 = allowRejectL1;
	}


	public Boolean getAllowRejectL2() {
		return allowRejectL2;
	}


	public void setAllowRejectL2(Boolean allowRejectL2) {
		this.allowRejectL2 = allowRejectL2;
	}


	public Boolean getAllowRejectL3() {
		return allowRejectL3;
	}


	public void setAllowRejectL3(Boolean allowRejectL3) {
		this.allowRejectL3 = allowRejectL3;
	}


	public Boolean getAllowForward1() {
		return allowForward1;
	}


	public void setAllowForward1(Boolean allowForward1) {
		this.allowForward1 = allowForward1;
	}


	public Boolean getAllowForward2() {
		return allowForward2;
	}


	public void setAllowForward2(Boolean allowForward2) {
		this.allowForward2 = allowForward2;
	}


	public Boolean getAllowForward3() {
		return allowForward3;
	}


	public void setAllowForward3(Boolean allowForward3) {
		this.allowForward3 = allowForward3;
	}


	public Boolean getAllowApprove1() {
		return allowApprove1;
	}


	public void setAllowApprove1(Boolean allowApprove1) {
		this.allowApprove1 = allowApprove1;
	}


	public Boolean getAllowApprove2() {
		return allowApprove2;
	}


	public void setAllowApprove2(Boolean allowApprove2) {
		this.allowApprove2 = allowApprove2;
	}


	public Boolean getAllowApprove3() {
		return allowApprove3;
	}


	public void setAllowApprove3(Boolean allowApprove3) {
		this.allowApprove3 = allowApprove3;
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
