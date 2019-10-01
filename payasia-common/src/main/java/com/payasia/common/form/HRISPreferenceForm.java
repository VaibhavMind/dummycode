package com.payasia.common.form;

import java.util.List;

import com.payasia.common.dto.EmployeeConditionDTO;

public class HRISPreferenceForm {

	private Boolean saveHrLetterInDocumentCenter;

	private Boolean enableEmployeeChangeWorkflow;

	private Boolean allowEmployeetouploaddocument;

	private Boolean passwordProtect;

	private Boolean hideGetPassword;

	private Boolean clientAdminEditDeleteEmployee;

	private Boolean useSystemMailAsFromAddress;

	private Boolean showPasswordAsPlainText;
	
	private Boolean UseEmailAndEmployeeNumberForLogin;
	
	private Boolean  sendPayslipReleaseMail;
	
	private Long externalDataDictId;
	
	private String discussionBoardDefaultEmailTo;
	private String discussionBoardDefaultEmailCC;
	private String discussionBoardPayAsiaUsers;
	private Boolean enableVisibility;
	
	private List<EmployeeConditionDTO> employeeInfoList;
	private List<EmployeeListForm> searchEmployeeList;
	
	
	
	public Boolean getEnableVisibility() {
		return enableVisibility;
	}

	public void setEnableVisibility(Boolean enableVisibility) {
		this.enableVisibility = enableVisibility;
	}

	public Boolean getSaveHrLetterInDocumentCenter() {
		return saveHrLetterInDocumentCenter;
	}

	public void setSaveHrLetterInDocumentCenter(
			Boolean saveHrLetterInDocumentCenter) {
		this.saveHrLetterInDocumentCenter = saveHrLetterInDocumentCenter;
	}

	public Boolean getEnableEmployeeChangeWorkflow() {
		return enableEmployeeChangeWorkflow;
	}

	public void setEnableEmployeeChangeWorkflow(
			Boolean enableEmployeeChangeWorkflow) {
		this.enableEmployeeChangeWorkflow = enableEmployeeChangeWorkflow;
	}

	public Boolean getPasswordProtect() {
		return passwordProtect;
	}

	public void setPasswordProtect(Boolean passwordProtect) {
		this.passwordProtect = passwordProtect;
	}

	public Boolean getAllowEmployeetouploaddocument() {
		return allowEmployeetouploaddocument;
	}

	public void setAllowEmployeetouploaddocument(
			Boolean allowEmployeetouploaddocument) {
		this.allowEmployeetouploaddocument = allowEmployeetouploaddocument;
	}

	public Boolean getHideGetPassword() {
		return hideGetPassword;
	}

	public void setHideGetPassword(Boolean hideGetPassword) {
		this.hideGetPassword = hideGetPassword;
	}

	public Boolean getClientAdminEditDeleteEmployee() {
		return clientAdminEditDeleteEmployee;
	}

	public void setClientAdminEditDeleteEmployee(
			Boolean clientAdminEditDeleteEmployee) {
		this.clientAdminEditDeleteEmployee = clientAdminEditDeleteEmployee;
	}

	public Boolean getUseSystemMailAsFromAddress() {
		return useSystemMailAsFromAddress;
	}

	public void setUseSystemMailAsFromAddress(Boolean useSystemMailAsFromAddress) {
		this.useSystemMailAsFromAddress = useSystemMailAsFromAddress;
	}

	public Boolean getShowPasswordAsPlainText() {
		return showPasswordAsPlainText;
	}

	public void setShowPasswordAsPlainText(Boolean showPasswordAsPlainText) {
		this.showPasswordAsPlainText = showPasswordAsPlainText;
	}

	public Boolean getUseEmailAndEmployeeNumberForLogin() {
		return UseEmailAndEmployeeNumberForLogin;
	}

	public void setUseEmailAndEmployeeNumberForLogin(
			Boolean useEmailAndEmployeeNumberForLogin) {
		UseEmailAndEmployeeNumberForLogin = useEmailAndEmployeeNumberForLogin;
	}

	public Boolean getSendPayslipReleaseMail() {
		return sendPayslipReleaseMail;
	}

	public void setSendPayslipReleaseMail(Boolean sendPayslipReleaseMail) {
		this.sendPayslipReleaseMail = sendPayslipReleaseMail;
	}

	public Long getExternalDataDictId() {
		return externalDataDictId;
	}

	public void setExternalDataDictId(Long externalDataDictId) {
		this.externalDataDictId = externalDataDictId;
	}

	public String getDiscussionBoardDefaultEmailTo() {
		return discussionBoardDefaultEmailTo;
	}

	public void setDiscussionBoardDefaultEmailTo(
			String discussionBoardDefaultEmailTo) {
		this.discussionBoardDefaultEmailTo = discussionBoardDefaultEmailTo;
	}

	public String getDiscussionBoardDefaultEmailCC() {
		return discussionBoardDefaultEmailCC;
	}

	public void setDiscussionBoardDefaultEmailCC(
			String discussionBoardDefaultEmailCC) {
		this.discussionBoardDefaultEmailCC = discussionBoardDefaultEmailCC;
	}

	public String getDiscussionBoardPayAsiaUsers() {
		return discussionBoardPayAsiaUsers;
	}

	public void setDiscussionBoardPayAsiaUsers(String discussionBoardPayAsiaUsers) {
		this.discussionBoardPayAsiaUsers = discussionBoardPayAsiaUsers;
	}

	public List<EmployeeConditionDTO> getEmployeeInfoList() {
		return employeeInfoList;
	}

	public void setEmployeeInfoList(List<EmployeeConditionDTO> employeeInfoList) {
		this.employeeInfoList = employeeInfoList;
	}

	public List<EmployeeListForm> getSearchEmployeeList() {
		return searchEmployeeList;
	}

	public void setSearchEmployeeList(List<EmployeeListForm> searchEmployeeList) {
		this.searchEmployeeList = searchEmployeeList;
	}
	

	

	


}
