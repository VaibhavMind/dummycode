package com.payasia.common.form;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LeavePreferenceForm {

	private Long leavePreferenceId;
	private Long companyId;
	private Long leaveStartMonth;
	private Long leaveEndMonth;
	private String cancelLeaveValidationDays;
	private boolean useSystemMailAsFromAddress;
	private boolean allowManagerSelfApproveLeave;
	private boolean allowYEPIfLeaveApplicationsArePending;
	private boolean hideLeaveTypeInLeaveCalendarDetail;
	private Long leaveTransactionToShow;
	private boolean considerOffInLeaveConjunction;
	private Long monthMasterId;
	private String monthName;

	private Long leaveTransAppCodeId;
	private String leaveTransName;
	private String leaveTransAppCodeValue;
	private Boolean showEncashed;
	private Boolean dashboardSameAsAdmin;
	private Boolean showFullEntitlement;
	private boolean showLeaveCalendarBasedOnCustomField;
	private Long leaveCalendarBasedOnCustomField;
	private boolean showLeaveBalanceCustomReport;
	private boolean hideCustomFieldsForAngloEasternCompany;
	private boolean defaultEmailCC;
	private boolean leaveWorkflowNotRequired;
	private String keypayPayRunParameter;
	private Double workingHoursInADay;
	private String leaveUnit;
	private String companyCountryName;
	private boolean leavehideAddColumn;
	private boolean preApprovalReq; 
	private boolean leaveExtensionReq;
	private String preApprovalReqRemark;

	public boolean isLeaveExtensionReq() {
		return leaveExtensionReq;
	}

	public void setLeaveExtensionReq(boolean leaveExtensionReq) {
		this.leaveExtensionReq = leaveExtensionReq;
	}

	public boolean isPreApprovalReq() {
		return preApprovalReq;
	}

	public void setPreApprovalReq(boolean preApprovalReq) {
		this.preApprovalReq = preApprovalReq;
	}
	
	public boolean isLeavehideAddColumn() {
		return leavehideAddColumn;
	}

	public void setLeavehideAddColumn(boolean leavehideAddColumn) {
		this.leavehideAddColumn = leavehideAddColumn;
	}

	public Long getLeavePreferenceId() {
		return leavePreferenceId;
	}

	public void setLeavePreferenceId(Long leavePreferenceId) {
		this.leavePreferenceId = leavePreferenceId;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public Long getLeaveStartMonth() {
		return leaveStartMonth;
	}

	public void setLeaveStartMonth(Long leaveStartMonth) {
		this.leaveStartMonth = leaveStartMonth;
	}

	public Long getLeaveEndMonth() {
		return leaveEndMonth;
	}

	public void setLeaveEndMonth(Long leaveEndMonth) {
		this.leaveEndMonth = leaveEndMonth;
	}

	public String getCancelLeaveValidationDays() {
		return cancelLeaveValidationDays;
	}

	public void setCancelLeaveValidationDays(String cancelLeaveValidationDays) {
		this.cancelLeaveValidationDays = cancelLeaveValidationDays;
	}

	public boolean isUseSystemMailAsFromAddress() {
		return useSystemMailAsFromAddress;
	}

	public void setUseSystemMailAsFromAddress(boolean useSystemMailAsFromAddress) {
		this.useSystemMailAsFromAddress = useSystemMailAsFromAddress;
	}

	public boolean isAllowManagerSelfApproveLeave() {
		return allowManagerSelfApproveLeave;
	}

	public void setAllowManagerSelfApproveLeave(
			boolean allowManagerSelfApproveLeave) {
		this.allowManagerSelfApproveLeave = allowManagerSelfApproveLeave;
	}

	public boolean isAllowYEPIfLeaveApplicationsArePending() {
		return allowYEPIfLeaveApplicationsArePending;
	}

	public void setAllowYEPIfLeaveApplicationsArePending(
			boolean allowYEPIfLeaveApplicationsArePending) {
		this.allowYEPIfLeaveApplicationsArePending = allowYEPIfLeaveApplicationsArePending;
	}

	public boolean isHideLeaveTypeInLeaveCalendarDetail() {
		return hideLeaveTypeInLeaveCalendarDetail;
	}

	public void setHideLeaveTypeInLeaveCalendarDetail(
			boolean hideLeaveTypeInLeaveCalendarDetail) {
		this.hideLeaveTypeInLeaveCalendarDetail = hideLeaveTypeInLeaveCalendarDetail;
	}

	public Long getLeaveTransactionToShow() {
		return leaveTransactionToShow;
	}

	public void setLeaveTransactionToShow(Long leaveTransactionToShow) {
		this.leaveTransactionToShow = leaveTransactionToShow;
	}

	public Long getMonthMasterId() {
		return monthMasterId;
	}

	public void setMonthMasterId(Long monthMasterId) {
		this.monthMasterId = monthMasterId;
	}

	public String getMonthName() {
		return monthName;
	}

	public void setMonthName(String monthName) {
		this.monthName = monthName;
	}

	public Long getLeaveTransAppCodeId() {
		return leaveTransAppCodeId;
	}

	public void setLeaveTransAppCodeId(Long leaveTransAppCodeId) {
		this.leaveTransAppCodeId = leaveTransAppCodeId;
	}

	public String getLeaveTransName() {
		return leaveTransName;
	}

	public void setLeaveTransName(String leaveTransName) {
		this.leaveTransName = leaveTransName;
	}

	public boolean isConsiderOffInLeaveConjunction() {
		return considerOffInLeaveConjunction;
	}

	public void setConsiderOffInLeaveConjunction(
			boolean considerOffInLeaveConjunction) {
		this.considerOffInLeaveConjunction = considerOffInLeaveConjunction;
	}

	public Boolean getShowEncashed() {
		return showEncashed;
	}

	public void setShowEncashed(Boolean showEncashed) {
		this.showEncashed = showEncashed;
	}

	public Boolean getDashboardSameAsAdmin() {
		return dashboardSameAsAdmin;
	}

	public void setDashboardSameAsAdmin(Boolean dashboardSameAsAdmin) {
		this.dashboardSameAsAdmin = dashboardSameAsAdmin;
	}

	public String getLeaveTransAppCodeValue() {
		return leaveTransAppCodeValue;
	}

	public void setLeaveTransAppCodeValue(String leaveTransAppCodeValue) {
		this.leaveTransAppCodeValue = leaveTransAppCodeValue;
	}

	public Boolean getShowFullEntitlement() {
		return showFullEntitlement;
	}

	public void setShowFullEntitlement(Boolean showFullEntitlement) {
		this.showFullEntitlement = showFullEntitlement;
	}


	public boolean isShowLeaveCalendarBasedOnCustomField() {
		return showLeaveCalendarBasedOnCustomField;
	}

	public void setShowLeaveCalendarBasedOnCustomField(
			boolean showLeaveCalendarBasedOnCustomField) {
		this.showLeaveCalendarBasedOnCustomField = showLeaveCalendarBasedOnCustomField;
	}

	public Long getLeaveCalendarBasedOnCustomField() {
		return leaveCalendarBasedOnCustomField;
	}

	public void setLeaveCalendarBasedOnCustomField(
			Long leaveCalendarBasedOnCustomField) {
		this.leaveCalendarBasedOnCustomField = leaveCalendarBasedOnCustomField;
	}

	public boolean isShowLeaveBalanceCustomReport() {
		return showLeaveBalanceCustomReport;
	}

	public void setShowLeaveBalanceCustomReport(boolean showLeaveBalanceCustomReport) {
		this.showLeaveBalanceCustomReport = showLeaveBalanceCustomReport;
	}

	public boolean isHideCustomFieldsForAngloEasternCompany() {
		return hideCustomFieldsForAngloEasternCompany;
	}

	public void setHideCustomFieldsForAngloEasternCompany(
			boolean hideCustomFieldsForAngloEasternCompany) {
		this.hideCustomFieldsForAngloEasternCompany = hideCustomFieldsForAngloEasternCompany;
	}

	public void setDefaultEmailCC(boolean defaultEmailCC) {
		this.defaultEmailCC = defaultEmailCC;
	}

	public boolean isDefaultEmailCC() {
		return defaultEmailCC;
	}


	public void setLeaveWorkflowNotRequired(boolean leaveWorkflowNotRequired) {
		this.leaveWorkflowNotRequired = leaveWorkflowNotRequired;
	}

	public boolean isLeaveWorkflowNotRequired() {
		return leaveWorkflowNotRequired;
	}

	public String getKeypayPayRunParameter() {
		return keypayPayRunParameter;
	}

	public void setKeypayPayRunParameter(String keypayPayRunParameter) {
		this.keypayPayRunParameter = keypayPayRunParameter;
	}

	public Double getWorkingHoursInADay() {
		return workingHoursInADay;
	}

	public void setWorkingHoursInADay(Double workingHoursInADay) {
		this.workingHoursInADay = workingHoursInADay;
	}

	public String getLeaveUnit() {
		return leaveUnit;
	}

	public void setLeaveUnit(String leaveUnit) {
		this.leaveUnit = leaveUnit;
	}

	public String getCompanyCountryName() {
		return companyCountryName;
	}

	public void setCompanyCountryName(String companyCountryName) {
		this.companyCountryName = companyCountryName;
	}

	public String getPreApprovalReqRemark() {
		return preApprovalReqRemark;
	}

	public void setPreApprovalReqRemark(String preApprovalReqRemark) {
		this.preApprovalReqRemark = preApprovalReqRemark;
	}
	
}
