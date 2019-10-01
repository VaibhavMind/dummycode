package com.payasia.dao.bean;

import java.io.Serializable;
import java.sql.Timestamp;

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
 * The persistent class for the Email_Preference_Master database table.
 * 
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "Claim_Preference")
public class ClaimPreference extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name = "Claim_Preference_ID")
	private long claimPreferenceId;

	@ManyToOne
	@JoinColumn(name = "Company_ID")
	private Company company;

	@Column(name = "Start_Day_Month")
	private Timestamp startDayMonth;

	@Column(name = "End_Day_Month")
	private Timestamp endDayMonth;

	@Column(name = "PayAsia_Admin_Can_Approve")
	private boolean payAsiaAdminCanApprove;

	@Column(name = "Use_System_Mail_As_From_Address")
	private boolean useSystemMailAsFromAddress;
	@Column(name = "Attachment_For_Mail")
	private Boolean attachmentForMail;

	@Column(name = "Item_Name_Sort_Order")
	private String claimItemNameSortOrder;

	@Column(name = "Claim_Item_Date_Sort_Order")
	private String claimItemDateSortOrder;
	
	public Boolean getAttachmentForMail() {
		return attachmentForMail;
	}

	public void setAttachmentForMail(Boolean attachmentForMail) {
		this.attachmentForMail = attachmentForMail;
	}

	@Column(name = "Default_Email_CC")
	private boolean defaultEmailCC;

	@Column(name = "Admin_Can_Amend_Data_Before_Approval")
	private boolean adminCanAmendDataBeforeApproval;

	@Column(name = "Show_Paid_Status_For_Claim_Batch")
	private boolean showPaidStatusForClaimBatch;

	@Column(name = "Grid_Created_Date_Sort_Order")
	private String gridCreatedDateSortOrder;

	@Column(name = "Grid_Claim_Number_Sort_Order")
	private String gridClaimNumberSortOrder;
	@ManyToOne
	@JoinColumn(name = "Pwd_For_Attachment_Mail")
	private DataDictionary pwdForAttachmentMail;

	@Column(name = "Show_Monthly_Cons_Fin_Report_Group_By_Emp")
	private boolean showMonthlyConsFinReportGroupingByEmp;

	@ManyToOne
	@JoinColumn(name = "Bank_Name")
	private DataDictionary bankNameDictionary;

	@ManyToOne
	@JoinColumn(name = "Bank_Account_Number")
	private DataDictionary bankAccountNumDictionary;

	@ManyToOne
	@JoinColumn(name = "Bank_Account_Name")
	private DataDictionary bankAccountNameDictionary;

	@Column(name = "Additional_Balance_From")
	private Boolean additionalBalanceFrom;

	public ClaimPreference() {
	}

	public long getClaimPreferenceId() {
		return claimPreferenceId;
	}

	public void setClaimPreferenceId(long claimPreferenceId) {
		this.claimPreferenceId = claimPreferenceId;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public Timestamp getStartDayMonth() {
		return startDayMonth;
	}

	public void setStartDayMonth(Timestamp startDayMonth) {
		this.startDayMonth = startDayMonth;
	}

	public Timestamp getEndDayMonth() {
		return endDayMonth;
	}

	public void setEndDayMonth(Timestamp endDayMonth) {
		this.endDayMonth = endDayMonth;
	}

	public boolean isPayAsiaAdminCanApprove() {
		return payAsiaAdminCanApprove;
	}

	public void setPayAsiaAdminCanApprove(boolean payAsiaAdminCanApprove) {
		this.payAsiaAdminCanApprove = payAsiaAdminCanApprove;
	}

	public boolean isUseSystemMailAsFromAddress() {
		return useSystemMailAsFromAddress;
	}

	public void setUseSystemMailAsFromAddress(boolean useSystemMailAsFromAddress) {
		this.useSystemMailAsFromAddress = useSystemMailAsFromAddress;
	}

	public boolean isDefaultEmailCC() {
		return defaultEmailCC;
	}

	public void setDefaultEmailCC(boolean defaultEmailCC) {
		this.defaultEmailCC = defaultEmailCC;
	}

	public boolean isAdminCanAmendDataBeforeApproval() {
		return adminCanAmendDataBeforeApproval;
	}

	public void setAdminCanAmendDataBeforeApproval(boolean adminCanAmendDataBeforeApproval) {
		this.adminCanAmendDataBeforeApproval = adminCanAmendDataBeforeApproval;
	}

	public boolean isShowPaidStatusForClaimBatch() {
		return showPaidStatusForClaimBatch;
	}

	public void setShowPaidStatusForClaimBatch(boolean showPaidStatusForClaimBatch) {
		this.showPaidStatusForClaimBatch = showPaidStatusForClaimBatch;
	}

	public String getGridCreatedDateSortOrder() {
		return gridCreatedDateSortOrder;
	}

	public void setGridCreatedDateSortOrder(String gridCreatedDateSortOrder) {
		this.gridCreatedDateSortOrder = gridCreatedDateSortOrder;
	}

	public String getGridClaimNumberSortOrder() {
		return gridClaimNumberSortOrder;
	}

	public void setGridClaimNumberSortOrder(String gridClaimNumberSortOrder) {
		this.gridClaimNumberSortOrder = gridClaimNumberSortOrder;
	}

	public boolean isShowMonthlyConsFinReportGroupingByEmp() {
		return showMonthlyConsFinReportGroupingByEmp;
	}

	public void setShowMonthlyConsFinReportGroupingByEmp(boolean showMonthlyConsFinReportGroupingByEmp) {
		this.showMonthlyConsFinReportGroupingByEmp = showMonthlyConsFinReportGroupingByEmp;
	}

	public DataDictionary getBankNameDictionary() {
		return bankNameDictionary;
	}

	public void setBankNameDictionary(DataDictionary bankNameDictionary) {
		this.bankNameDictionary = bankNameDictionary;
	}

	public DataDictionary getBankAccountNumDictionary() {
		return bankAccountNumDictionary;
	}

	public void setBankAccountNumDictionary(DataDictionary bankAccountNumDictionary) {
		this.bankAccountNumDictionary = bankAccountNumDictionary;
	}

	public DataDictionary getBankAccountNameDictionary() {
		return bankAccountNameDictionary;
	}

	public void setBankAccountNameDictionary(DataDictionary bankAccountNameDictionary) {
		this.bankAccountNameDictionary = bankAccountNameDictionary;
	}

	public Boolean getAdditionalBalanceFrom() {
		return additionalBalanceFrom;
	}

	public void setAdditionalBalanceFrom(Boolean additionalBalanceFrom) {
		this.additionalBalanceFrom = additionalBalanceFrom;
	}

	public DataDictionary getPwdForAttachmentMail() {
		return pwdForAttachmentMail;
	}

	public void setPwdForAttachmentMail(DataDictionary pwdForAttachmentMail) {
		this.pwdForAttachmentMail = pwdForAttachmentMail;
	}

	public String getClaimItemNameSortOrder() {
		return claimItemNameSortOrder;
	}

	public void setClaimItemNameSortOrder(String claimItemNameSortOrder) {
		this.claimItemNameSortOrder = claimItemNameSortOrder;
	}

	public String getClaimItemDateSortOrder() {
		return claimItemDateSortOrder;
	}

	public void setClaimItemDateSortOrder(String claimItemDateSortOrder) {
		this.claimItemDateSortOrder = claimItemDateSortOrder;
	}

	
}