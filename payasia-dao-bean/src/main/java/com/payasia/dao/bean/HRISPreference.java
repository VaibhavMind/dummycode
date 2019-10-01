package com.payasia.dao.bean;

import java.io.Serializable;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * The persistent class for the HRIS_Preference database table.
 * 
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "HRIS_Preference")
public class HRISPreference extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name = "HRIS_Preference_ID")
	private long hrisPreferenceId;

	@ManyToOne
	@JoinColumn(name = "Company_ID")
	private Company company;

	@Column(name = "Save_HR_Letter_In_Document_Center")
	private boolean saveHrLetterInDocumentCenter;

	@Column(name = "Enable_Employee_Change_Workflow")
	private boolean enableEmployeeChangeWorkflow;

	@Column(name = "Allow_Employee_Upload_Doc")
	private boolean allowEmployeeUploadDoc;

	@Column(name = "Password_Protect")
	private boolean passwordProtect;

	@Column(name = "Hide_Get_Password")
	private boolean hideGetPassword;

	@Column(name = "Client_Admin_Edit_Delete_Employee")
	private boolean clientAdminEditDeleteEmployee;

	@Column(name = "Use_System_Mail_As_From_Address")
	private boolean useSystemMailAsFromAddress;

	@Column(name = "Show_Password_As_Plain_Text")
	private boolean showPasswordAsPlainText;

	@Column(name = "Use_Email_And_EmployeeNumber_For_Login")
	private boolean UseEmailAndEmployeeNumberForLogin;

	@Column(name = "Send_Payslip_Release_Mail")
	private boolean sendPayslipReleaseMail;

	@Column(name = "Discussion_Board_Default_Email_To")
	private String discussionBoardDefaultEmailTo;

	@Column(name = "Discussion_Board_Default_Email_CC")
	private String discussionBoardDefaultEmailCC;

	@Column(name = "Discussion_Board_PayAsia_Users")
	private String discussionBoardPayAsiaUsers;
	
	@Column(name = "Enable_Visibility")
	private Boolean enableVisibility;
	
	@ManyToOne
	@JoinColumn(name = "External_ID")
	private DataDictionary externalId;
	
	@Column(name = "Payslip_Default_Email_To")
	private String paySlipDefaultEmailTo;

	public Boolean getEnableVisibility() {
		return enableVisibility;
	}

	public void setEnableVisibility(Boolean enableVisibility) {
		this.enableVisibility = enableVisibility;
	}

	public HRISPreference() {
	}
	
	public Boolean isEnableVisibility() {
		return enableVisibility;
	}


	public long getHrisPreferenceId() {
		return hrisPreferenceId;
	}

	public void setHrisPreferenceId(long hrisPreferenceId) {
		this.hrisPreferenceId = hrisPreferenceId;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public boolean isSaveHrLetterInDocumentCenter() {
		return saveHrLetterInDocumentCenter;
	}

	public void setSaveHrLetterInDocumentCenter(
			boolean saveHrLetterInDocumentCenter) {
		this.saveHrLetterInDocumentCenter = saveHrLetterInDocumentCenter;
	}

	public boolean isEnableEmployeeChangeWorkflow() {
		return enableEmployeeChangeWorkflow;
	}

	public void setEnableEmployeeChangeWorkflow(
			boolean enableEmployeeChangeWorkflow) {
		this.enableEmployeeChangeWorkflow = enableEmployeeChangeWorkflow;
	}

	public boolean isPasswordProtect() {
		return passwordProtect;
	}

	public void setPasswordProtect(boolean passwordProtect) {
		this.passwordProtect = passwordProtect;
	}

	public boolean isAllowEmployeeUploadDoc() {
		return allowEmployeeUploadDoc;
	}

	public void setAllowEmployeeUploadDoc(boolean allowEmployeeUploadDoc) {
		this.allowEmployeeUploadDoc = allowEmployeeUploadDoc;
	}

	public boolean isHideGetPassword() {
		return hideGetPassword;
	}

	public void setHideGetPassword(boolean hideGetPassword) {
		this.hideGetPassword = hideGetPassword;
	}

	public boolean isClientAdminEditDeleteEmployee() {
		return clientAdminEditDeleteEmployee;
	}

	public void setClientAdminEditDeleteEmployee(
			boolean clientAdminEditDeleteEmployee) {
		this.clientAdminEditDeleteEmployee = clientAdminEditDeleteEmployee;
	}

	public boolean isUseSystemMailAsFromAddress() {
		return useSystemMailAsFromAddress;
	}

	public void setUseSystemMailAsFromAddress(boolean useSystemMailAsFromAddress) {
		this.useSystemMailAsFromAddress = useSystemMailAsFromAddress;
	}

	public boolean isShowPasswordAsPlainText() {
		return showPasswordAsPlainText;
	}

	public void setShowPasswordAsPlainText(boolean showPasswordAsPlainText) {
		this.showPasswordAsPlainText = showPasswordAsPlainText;
	}

	public boolean isUseEmailAndEmployeeNumberForLogin() {
		return UseEmailAndEmployeeNumberForLogin;
	}

	public void setUseEmailAndEmployeeNumberForLogin(
			boolean useEmailAndEmployeeNumberForLogin) {
		UseEmailAndEmployeeNumberForLogin = useEmailAndEmployeeNumberForLogin;
	}

	public boolean isSendPayslipReleaseMail() {
		return sendPayslipReleaseMail;
	}

	public void setSendPayslipReleaseMail(boolean sendPayslipReleaseMail) {
		this.sendPayslipReleaseMail = sendPayslipReleaseMail;
	}

	public DataDictionary getExternalId() {
		return externalId;
	}

	public void setExternalId(DataDictionary externalId) {
		this.externalId = externalId;
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

	public void setDiscussionBoardPayAsiaUsers(
			String discussionBoardPayAsiaUsers) {
		this.discussionBoardPayAsiaUsers = discussionBoardPayAsiaUsers;
	}

	public String getPaySlipDefaultEmailTo() {
		return paySlipDefaultEmailTo;
	}

	public void setPaySlipDefaultEmailTo(String paySlipDefaultEmailTo) {
		this.paySlipDefaultEmailTo = paySlipDefaultEmailTo;
	}
	

}