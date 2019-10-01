package com.payasia.common.form;

import java.util.HashMap;
import java.util.List;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.payasia.common.dto.DataImportLogDTO;

public class HRISReviewerForm {
	
	
	private String status;
	private String employeeStatus;
	private String employeeName;
	private String hrisReviewer1;
	private String hrisReviewer2;
	private String hrisReviewer3;
	
	private Long hrisReviewerId;
	private String hrisReviewerName;
	private Long employeeHRISReviewerId;
	
	private Long employeeHRISReviewerId1;
	private Long employeeHRISReviewerId2;
	private Long employeeHRISReviewerId3;
	
	private String ruleName;
	private String ruleValue;
	
	private Long hrisReviewerId1;
	private Long hrisReviewerId2;
	private Long hrisReviewerId3;
	
	
	private Long hrisReviewerRuleId;
	
	private Long hrisReviewerRuleId1;
	private Long hrisReviewerRuleId2;
	private Long hrisReviewerRuleId3;
	
	
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


	public String getHrisReviewer1() {
		return hrisReviewer1;
	}


	public void setHrisReviewer1(String hrisReviewer1) {
		this.hrisReviewer1 = hrisReviewer1;
	}


	public String getHrisReviewer2() {
		return hrisReviewer2;
	}


	public void setHrisReviewer2(String hrisReviewer2) {
		this.hrisReviewer2 = hrisReviewer2;
	}


	public String getHrisReviewer3() {
		return hrisReviewer3;
	}


	public void setHrisReviewer3(String hrisReviewer3) {
		this.hrisReviewer3 = hrisReviewer3;
	}


	public Long getHrisReviewerId() {
		return hrisReviewerId;
	}


	public void setHrisReviewerId(Long hrisReviewerId) {
		this.hrisReviewerId = hrisReviewerId;
	}


	public String getHrisReviewerName() {
		return hrisReviewerName;
	}


	public void setHrisReviewerName(String hrisReviewerName) {
		this.hrisReviewerName = hrisReviewerName;
	}


	public Long getEmployeeHRISReviewerId() {
		return employeeHRISReviewerId;
	}


	public void setEmployeeHRISReviewerId(Long employeeHRISReviewerId) {
		this.employeeHRISReviewerId = employeeHRISReviewerId;
	}


	public Long getEmployeeHRISReviewerId1() {
		return employeeHRISReviewerId1;
	}


	public void setEmployeeHRISReviewerId1(Long employeeHRISReviewerId1) {
		this.employeeHRISReviewerId1 = employeeHRISReviewerId1;
	}


	public Long getEmployeeHRISReviewerId2() {
		return employeeHRISReviewerId2;
	}


	public void setEmployeeHRISReviewerId2(Long employeeHRISReviewerId2) {
		this.employeeHRISReviewerId2 = employeeHRISReviewerId2;
	}


	public Long getEmployeeHRISReviewerId3() {
		return employeeHRISReviewerId3;
	}


	public void setEmployeeHRISReviewerId3(Long employeeHRISReviewerId3) {
		this.employeeHRISReviewerId3 = employeeHRISReviewerId3;
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


	public Long getHrisReviewerId1() {
		return hrisReviewerId1;
	}


	public void setHrisReviewerId1(Long hrisReviewerId1) {
		this.hrisReviewerId1 = hrisReviewerId1;
	}


	public Long getHrisReviewerId2() {
		return hrisReviewerId2;
	}


	public void setHrisReviewerId2(Long hrisReviewerId2) {
		this.hrisReviewerId2 = hrisReviewerId2;
	}


	public Long getHrisReviewerId3() {
		return hrisReviewerId3;
	}


	public void setHrisReviewerId3(Long hrisReviewerId3) {
		this.hrisReviewerId3 = hrisReviewerId3;
	}


	public Long getHrisReviewerRuleId() {
		return hrisReviewerRuleId;
	}


	public void setHrisReviewerRuleId(Long hrisReviewerRuleId) {
		this.hrisReviewerRuleId = hrisReviewerRuleId;
	}


	public Long getHrisReviewerRuleId1() {
		return hrisReviewerRuleId1;
	}


	public void setHrisReviewerRuleId1(Long hrisReviewerRuleId1) {
		this.hrisReviewerRuleId1 = hrisReviewerRuleId1;
	}


	public Long getHrisReviewerRuleId2() {
		return hrisReviewerRuleId2;
	}


	public void setHrisReviewerRuleId2(Long hrisReviewerRuleId2) {
		this.hrisReviewerRuleId2 = hrisReviewerRuleId2;
	}


	public Long getHrisReviewerRuleId3() {
		return hrisReviewerRuleId3;
	}


	public void setHrisReviewerRuleId3(Long hrisReviewerRuleId3) {
		this.hrisReviewerRuleId3 = hrisReviewerRuleId3;
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
