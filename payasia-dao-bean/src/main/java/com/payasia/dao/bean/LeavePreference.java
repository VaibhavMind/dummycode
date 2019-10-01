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
 * The persistent class for the Email_Preference_Master database table.
 * 
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "Leave_Preference")
public class LeavePreference extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name = "Leave_Preference_ID")
	private long leavePreferenceId;

	@ManyToOne
	@JoinColumn(name = "Company_ID")
	private Company company;

	@ManyToOne
	@JoinColumn(name = "Start_Month")
	private MonthMaster startMonth;

	@ManyToOne
	@JoinColumn(name = "End_Month")
	private MonthMaster endMonth;

	@ManyToOne
	@JoinColumn(name = "Leave_Calendar_Based_On_Custom_Field")
	private DataDictionary leaveCalendarBasedOnCustomField;

	@Column(name = "Cancel_Leave_Validation_Days")
	private Integer cancelLeaveValidationDays;

	@Column(name = "Use_System_Mail_As_From_Address")
	private boolean useSystemMailAsFromAddress;

	@Column(name = "Allow_Manager_Self_Approve_Leave")
	private boolean allowManagerSelfApproveLeave;

	@Column(name = "Allow_YEP_If_Leave_Applications_Are_Pending")
	private boolean allowYEPIfLeaveApplicationsArePending;

	@Column(name = "Hide_Leave_Type_In_Leave_Calendar_Detail")
	private boolean hideLeaveTypeInLeaveCalendarDetail;

	@Column(name = "Consider_Off_In_Leave_Conjunction")
	private boolean considerOffInLeaveConjunction;

	@ManyToOne
	@JoinColumn(name = "Leave_Transaction_To_Show")
	private AppCodeMaster leaveTransactionToShow;

	@Column(name = "Show_Encashed")
	private Boolean showEncashed;

	@Column(name = "Dashboard_Same_As_Admin")
	private Boolean dashboardSameAsAdmin;

	@Column(name = "Show_Full_Entitlement")
	private Boolean showFullEntitlement;

	@Column(name = "Show_Leave_Calendar_Based_On_Custom_Field")
	private boolean showLeaveCalendarBasedOnCustomField;

	@Column(name = "Show_Leave_Balance_Custom_Report")
	private boolean showLeaveBalanceCustomReport;

	@Column(name = "Default_Email_CC")
	private boolean defaultEmailCC;

	@Column(name = "Leave_Workflow_Not_Required")
	private boolean leaveWorkflowNotRequired;

	@Column(name = "Keypay_PayRun_Parameter")
	private String keypayPayRunParameter;

	@Column(name = "Working_Hours_In_A_Day")
	private Double workingHoursInADay;

	@Column(name = "Leave_Hide_Add_Column")
	private boolean leaveHideAddColumn;
	@Column(name = "Pre_Approval_Required")
	private boolean preApprovalRequired;

	@Column(name = "Leave_Extension_Required")
	private boolean leaveExtensionRequired;

	@ManyToOne
	@JoinColumn(name = "Leave_Unit")
	private AppCodeMaster leaveUnit;

	@Column(name = "Pre_Approval_Leave_Required_Remark")
	private String preApprovalReqRemark; 
	
	public LeavePreference() {
	}

	public long getLeavePreferenceId() {
		return leavePreferenceId;
	}

	public void setLeavePreferenceId(long leavePreferenceId) {
		this.leavePreferenceId = leavePreferenceId;
	}

	public boolean isLeaveExtensionRequired() {
		return leaveExtensionRequired;
	}

	public void setLeaveExtensionRequired(boolean leaveExtensionRequired) {
		this.leaveExtensionRequired = leaveExtensionRequired;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public boolean isPreApprovalRequired() {
		return preApprovalRequired;
	}

	public void setPreApprovalRequired(boolean preApprovalRequired) {
		this.preApprovalRequired = preApprovalRequired;
	}

	public MonthMaster getStartMonth() {
		return startMonth;
	}

	public void setStartMonth(MonthMaster startMonth) {
		this.startMonth = startMonth;
	}

	public MonthMaster getEndMonth() {
		return endMonth;
	}

	public void setEndMonth(MonthMaster endMonth) {
		this.endMonth = endMonth;
	}

	public Integer getCancelLeaveValidationDays() {
		return cancelLeaveValidationDays;
	}

	public void setCancelLeaveValidationDays(Integer cancelLeaveValidationDays) {
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

	public AppCodeMaster getLeaveTransactionToShow() {
		return leaveTransactionToShow;
	}

	public void setLeaveTransactionToShow(AppCodeMaster leaveTransactionToShow) {
		this.leaveTransactionToShow = leaveTransactionToShow;
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

	public DataDictionary getLeaveCalendarBasedOnCustomField() {
		return leaveCalendarBasedOnCustomField;
	}

	public void setLeaveCalendarBasedOnCustomField(
			DataDictionary leaveCalendarBasedOnCustomField) {
		this.leaveCalendarBasedOnCustomField = leaveCalendarBasedOnCustomField;
	}

	public boolean isShowLeaveBalanceCustomReport() {
		return showLeaveBalanceCustomReport;
	}

	public void setShowLeaveBalanceCustomReport(
			boolean showLeaveBalanceCustomReport) {
		this.showLeaveBalanceCustomReport = showLeaveBalanceCustomReport;
	}

	public boolean isDefaultEmailCC() {
		return defaultEmailCC;
	}

	public void setDefaultEmailCC(boolean defaultEmailCC) {
		this.defaultEmailCC = defaultEmailCC;
	}

	public boolean isLeaveWorkflowNotRequired() {
		return leaveWorkflowNotRequired;
	}

	public void setLeaveWorkflowNotRequired(boolean leaveWorkflowNotRequired) {
		this.leaveWorkflowNotRequired = leaveWorkflowNotRequired;
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

	public AppCodeMaster getLeaveUnit() {
		return leaveUnit;
	}

	public void setLeaveUnit(AppCodeMaster leaveUnit) {
		this.leaveUnit = leaveUnit;
	}

	public boolean isLeaveHideAddColumn() {
		return leaveHideAddColumn;
	}

	public void setLeaveHideAddColumn(boolean leaveHideAddColumn) {
		this.leaveHideAddColumn = leaveHideAddColumn;
	}

	public String getPreApprovalReqRemark() {
		return preApprovalReqRemark;
	}

	public void setPreApprovalReqRemark(String preApprovalReqRemark) {
		this.preApprovalReqRemark = preApprovalReqRemark;
	}
}