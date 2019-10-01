package com.payasia.common.form;


public class ClaimPreferenceForm {

	private String startDayMonth;
	private String endDayMonth;
	private Boolean payasiaAdminCanApprove;
	private Boolean useSystemMailAsFromAddress;
	private boolean defaultEmailCC;
	private boolean adminCanAmendDataBeforeApproval;
	private boolean showPaidStatusForClaimBatch;
	private String createdDateSortOrder;
	private String claimNumberSortOrder;
	private String claimItemNameSortOrder;
	private String claimItemDateSortOrder;
	private boolean showMonthlyConsFinReportGroupingByEmp;
	
	private Long bankAccountNumDictId;
	private Long bankAccountNameDictId;
	private Long bankNameDictId;
	
	private Boolean showAttachmentForMail;
	private Long attachmentMailBasedOnCustomField;
	private String bankAccountNum;
	private String bankAccountName;
	private String bankName;
	private Boolean additionalBalanceFrom;

	
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
	public String getStartDayMonth() {
		return startDayMonth;
	}

	public void setStartDayMonth(String startDayMonth) {
		this.startDayMonth = startDayMonth;
	}

	public String getEndDayMonth() {
		return endDayMonth;
	}

	public void setEndDayMonth(String endDayMonth) {
		this.endDayMonth = endDayMonth;
	}

	public Boolean getPayasiaAdminCanApprove() {
		return payasiaAdminCanApprove;
	}

	public void setPayasiaAdminCanApprove(Boolean payasiaAdminCanApprove) {
		this.payasiaAdminCanApprove = payasiaAdminCanApprove;
	}

	public Boolean getUseSystemMailAsFromAddress() {
		return useSystemMailAsFromAddress;
	}

	public void setUseSystemMailAsFromAddress(Boolean useSystemMailAsFromAddress) {
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

	public void setAdminCanAmendDataBeforeApproval(
			boolean adminCanAmendDataBeforeApproval) {
		this.adminCanAmendDataBeforeApproval = adminCanAmendDataBeforeApproval;
	}

	public boolean isShowPaidStatusForClaimBatch() {
		return showPaidStatusForClaimBatch;
	}

	public void setShowPaidStatusForClaimBatch(boolean showPaidStatusForClaimBatch) {
		this.showPaidStatusForClaimBatch = showPaidStatusForClaimBatch;
	}

	public String getCreatedDateSortOrder() {
		return createdDateSortOrder;
	}

	public void setCreatedDateSortOrder(String createdDateSortOrder) {
		this.createdDateSortOrder = createdDateSortOrder;
	}

	public String getClaimNumberSortOrder() {
		return claimNumberSortOrder;
	}

	public void setClaimNumberSortOrder(String claimNumberSortOrder) {
		this.claimNumberSortOrder = claimNumberSortOrder;
	}

	public boolean isShowMonthlyConsFinReportGroupingByEmp() {
		return showMonthlyConsFinReportGroupingByEmp;
	}

	public void setShowMonthlyConsFinReportGroupingByEmp(
			boolean showMonthlyConsFinReportGroupingByEmp) {
		this.showMonthlyConsFinReportGroupingByEmp = showMonthlyConsFinReportGroupingByEmp;
	}

	public Long getBankAccountNumDictId() {
		return bankAccountNumDictId;
	}

	public void setBankAccountNumDictId(Long bankAccountNumDictId) {
		this.bankAccountNumDictId = bankAccountNumDictId;
	}

	public Long getBankAccountNameDictId() {
		return bankAccountNameDictId;
	}

	public void setBankAccountNameDictId(Long bankAccountNameDictId) {
		this.bankAccountNameDictId = bankAccountNameDictId;
	}

	public Long getBankNameDictId() {
		return bankNameDictId;
	}

	public void setBankNameDictId(Long bankNameDictId) {
		this.bankNameDictId = bankNameDictId;
	}

	public String getBankAccountNum() {
		return bankAccountNum;
	}

	public void setBankAccountNum(String bankAccountNum) {
		this.bankAccountNum = bankAccountNum;
	}

	public String getBankAccountName() {
		return bankAccountName;
	}

	public void setBankAccountName(String bankAccountName) {
		this.bankAccountName = bankAccountName;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public Boolean getAdditionalBalanceFrom() {
		return additionalBalanceFrom;
	}

	public void setAdditionalBalanceFrom(Boolean additionalBalanceFrom) {
		this.additionalBalanceFrom = additionalBalanceFrom;
	}
	
	public Boolean getShowAttachmentForMail() {
		return showAttachmentForMail;
	}
	public void setShowAttachmentForMail(Boolean showAttachmentForMail) {
		this.showAttachmentForMail = showAttachmentForMail;
	}
	public Long getAttachmentMailBasedOnCustomField() {
		return attachmentMailBasedOnCustomField;
	}
	public void setAttachmentMailBasedOnCustomField(final Long attachmentMailBasedOnCustomField) {
		this.attachmentMailBasedOnCustomField = attachmentMailBasedOnCustomField;
	}
}
