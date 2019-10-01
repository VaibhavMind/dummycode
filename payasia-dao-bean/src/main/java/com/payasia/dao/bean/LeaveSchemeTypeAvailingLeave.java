package com.payasia.dao.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * The persistent class for the Leave_Scheme_Type_Availing_Leave database table.
 * 
 */
@Entity
@Table(name = "Leave_Scheme_Type_Availing_Leave")
public class LeaveSchemeTypeAvailingLeave extends CompanyBaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Leave_Scheme_Type_Availing_Leave_ID")
	private long leaveSchemeTypeAvailingLeaveId;

	@Column(name = "Holidays_Inclusive")
	private boolean holidaysInclusive;

	@Column(name = "Leave_Extension")
	private boolean leaveExtension;

	@Column(name = "Off_Inclusive")
	private boolean offInclusive;

	@Column(name = "Min_Block_Leave")
	private BigDecimal minBlockLeave;

	@Column(name = "Max_Block_Leave")
	private BigDecimal maxBlockLeave;

	@Column(name = "Allow_Excess_Leave")
	private boolean allowExcessLeave;

	@Column(name = "Excess_Leave_Max_Days")
	private BigDecimal excessLeaveMaxDays;

	@Column(name = "Excess_Leave_Max_Frequency")
	private Integer excessLeaveMaxFrequency;

	@Column(name = "Allow_Advance_Posting")
	private Boolean allowAdvancePosting;

	@Column(name = "Advance_Leave_Apply_Before_Days")
	private Integer advanceLeaveApplyBeforeDays;

	@Column(name = "Advance_Leave_Post_Before_Days")
	private Integer advanceLeavePostBeforeDays;

	@Column(name = "Allow_Backdate_Posting")
	private Boolean allowBackdatePosting;

	@Column(name = "Backdate_Posting_After_days")
	private Integer backdatePostingAfterDays;

	@Column(name = "Next_Year_Posting_Before_Days")
	private Integer nextYearPostingBeforeDays;

	@ManyToOne
	@JoinColumn(name = "Consider_Leave_Balance_From")
	private LeaveSchemeType considerLeaveBalanceFrom;

	@Column(name = "Approval_Not_Required")
	private Boolean approvalNotRequired;

	@Column(name = "Default_CC_Email")
	private String defaultCCEmail;

	@Column(name = "Remarks")
	private String remarks;

	@Column(name = "Send_Mail_To_Default_CC")
	private Boolean sendMailToDefaultCC;

	@Column(name = "Attachment_Mandatory")
	private boolean attachmentMandatory;

	@Column(name = "Attachment_Exemption_Days")
	private BigDecimal attachmentExemptionDays;

	@Column(name = "Apply_After_Days")
	private Integer applyAfterDays;

	@ManyToOne
	@JoinColumn(name = "Apply_After_From")
	private AppCodeMaster applyAfterFrom;

	@ManyToOne
	@JoinColumn(name = "Leave_Scheme_Type_ID")
	private LeaveSchemeType leaveSchemeType;

	@Column(name = "Excess_Leave_Allow_Full_Entitlement")
	private boolean excessLeaveAllowFullEntitlement;

	@OneToMany(mappedBy = "leaveSchemeTypeAvailingLeave", cascade = { CascadeType.REMOVE })
	private Set<LeaveSchemeTypeCustomField> leaveSchemeTypeCustomFields;

	@OneToMany(mappedBy = "leaveSchemeTypeAvailingLeave", cascade = { CascadeType.REMOVE })
	private Set<LeaveSchemeTypeGrantCondition> leaveSchemeTypeGrantConditions;
	@Column(name = "Min_Days_Gap_Between_Leave")
	private BigDecimal minDaysGapBetweenLeave;

	@Column(name = "Max_Days_Allow_Per_Year")
	private BigDecimal maxDaysAllowPerYear;

	@Column(name = "Auto_Approve")
	private boolean autoApprove;

	@Column(name = "Auto_Approve_After_Days")
	private Integer autoApproveAfterDays;

	@Column(name = "Leave_Visibility_Start_Date")
	private Timestamp leaveVisibilityStartDate;

	@Column(name = "Leave_Visibility_End_Date")
	private Timestamp leaveVisibilityEndDate;

	public LeaveSchemeTypeAvailingLeave() {
	}

	public long getLeaveSchemeTypeAvailingLeaveId() {
		return leaveSchemeTypeAvailingLeaveId;
	}

	public void setLeaveSchemeTypeAvailingLeaveId(
			long leaveSchemeTypeAvailingLeaveId) {
		this.leaveSchemeTypeAvailingLeaveId = leaveSchemeTypeAvailingLeaveId;
	}

	public boolean isHolidaysInclusive() {
		return holidaysInclusive;
	}

	public void setHolidaysInclusive(boolean holidaysInclusive) {
		this.holidaysInclusive = holidaysInclusive;
	}

	public boolean isOffInclusive() {
		return offInclusive;
	}

	public void setOffInclusive(boolean offInclusive) {
		this.offInclusive = offInclusive;
	}

	public BigDecimal getMinBlockLeave() {
		return minBlockLeave;
	}

	public boolean isLeaveExtension() {
		return leaveExtension;
	}

	public void setLeaveExtension(boolean leaveExtension) {
		this.leaveExtension = leaveExtension;
	}

	public void setMinBlockLeave(BigDecimal minBlockLeave) {
		this.minBlockLeave = minBlockLeave;
	}

	public BigDecimal getMaxBlockLeave() {
		return maxBlockLeave;
	}

	public void setMaxBlockLeave(BigDecimal maxBlockLeave) {
		this.maxBlockLeave = maxBlockLeave;
	}

	public boolean isAllowExcessLeave() {
		return allowExcessLeave;
	}

	public void setAllowExcessLeave(boolean allowExcessLeave) {
		this.allowExcessLeave = allowExcessLeave;
	}

	public BigDecimal getExcessLeaveMaxDays() {
		return excessLeaveMaxDays;
	}

	public void setExcessLeaveMaxDays(BigDecimal excessLeaveMaxDays) {
		this.excessLeaveMaxDays = excessLeaveMaxDays;
	}

	public Integer getExcessLeaveMaxFrequency() {
		return excessLeaveMaxFrequency;
	}

	public void setExcessLeaveMaxFrequency(Integer excessLeaveMaxFrequency) {
		this.excessLeaveMaxFrequency = excessLeaveMaxFrequency;
	}

	public Boolean getAllowAdvancePosting() {
		return allowAdvancePosting;
	}

	public void setAllowAdvancePosting(Boolean allowAdvancePosting) {
		this.allowAdvancePosting = allowAdvancePosting;
	}

	public Integer getAdvanceLeaveApplyBeforeDays() {
		return advanceLeaveApplyBeforeDays;
	}

	public void setAdvanceLeaveApplyBeforeDays(
			Integer advanceLeaveApplyBeforeDays) {
		this.advanceLeaveApplyBeforeDays = advanceLeaveApplyBeforeDays;
	}

	public Integer getAdvanceLeavePostBeforeDays() {
		return advanceLeavePostBeforeDays;
	}

	public void setAdvanceLeavePostBeforeDays(Integer advanceLeavePostBeforeDays) {
		this.advanceLeavePostBeforeDays = advanceLeavePostBeforeDays;
	}

	public Boolean getAllowBackdatePosting() {
		return allowBackdatePosting;
	}

	public void setAllowBackdatePosting(Boolean allowBackdatePosting) {
		this.allowBackdatePosting = allowBackdatePosting;
	}

	public Integer getBackdatePostingAfterDays() {
		return backdatePostingAfterDays;
	}

	public void setBackdatePostingAfterDays(Integer backdatePostingAfterDays) {
		this.backdatePostingAfterDays = backdatePostingAfterDays;
	}

	public Integer getNextYearPostingBeforeDays() {
		return nextYearPostingBeforeDays;
	}

	public void setNextYearPostingBeforeDays(Integer nextYearPostingBeforeDays) {
		this.nextYearPostingBeforeDays = nextYearPostingBeforeDays;
	}

	public LeaveSchemeType getConsiderLeaveBalanceFrom() {
		return considerLeaveBalanceFrom;
	}

	public void setConsiderLeaveBalanceFrom(
			LeaveSchemeType considerLeaveBalanceFrom) {
		this.considerLeaveBalanceFrom = considerLeaveBalanceFrom;
	}

	public Boolean getApprovalNotRequired() {
		return approvalNotRequired;
	}

	public void setApprovalNotRequired(Boolean approvalNotRequired) {
		this.approvalNotRequired = approvalNotRequired;
	}

	public String getDefaultCCEmail() {
		return defaultCCEmail;
	}

	public void setDefaultCCEmail(String defaultCCEmail) {
		this.defaultCCEmail = defaultCCEmail;
	}

	public Boolean getSendMailToDefaultCC() {
		return sendMailToDefaultCC;
	}

	public void setSendMailToDefaultCC(Boolean sendMailToDefaultCC) {
		this.sendMailToDefaultCC = sendMailToDefaultCC;
	}

	public LeaveSchemeType getLeaveSchemeType() {
		return leaveSchemeType;
	}

	public void setLeaveSchemeType(LeaveSchemeType leaveSchemeType) {
		this.leaveSchemeType = leaveSchemeType;
	}

	public Set<LeaveSchemeTypeCustomField> getLeaveSchemeTypeCustomFields() {
		return leaveSchemeTypeCustomFields;
	}

	public void setLeaveSchemeTypeCustomFields(
			Set<LeaveSchemeTypeCustomField> leaveSchemeTypeCustomFields) {
		this.leaveSchemeTypeCustomFields = leaveSchemeTypeCustomFields;
	}

	public boolean isAttachmentMandatory() {
		return attachmentMandatory;
	}

	public void setAttachmentMandatory(boolean attachmentMandatory) {
		this.attachmentMandatory = attachmentMandatory;
	}

	public BigDecimal getAttachmentExemptionDays() {
		return attachmentExemptionDays;
	}

	public void setAttachmentExemptionDays(BigDecimal attachmentExemptionDays) {
		this.attachmentExemptionDays = attachmentExemptionDays;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Set<LeaveSchemeTypeGrantCondition> getLeaveSchemeTypeGrantConditions() {
		return leaveSchemeTypeGrantConditions;
	}

	public void setLeaveSchemeTypeGrantConditions(
			Set<LeaveSchemeTypeGrantCondition> leaveSchemeTypeGrantConditions) {
		this.leaveSchemeTypeGrantConditions = leaveSchemeTypeGrantConditions;
	}

	public Integer getApplyAfterDays() {
		return applyAfterDays;
	}

	public void setApplyAfterDays(Integer applyAfterDays) {
		this.applyAfterDays = applyAfterDays;
	}

	public AppCodeMaster getApplyAfterFrom() {
		return applyAfterFrom;
	}

	public void setApplyAfterFrom(AppCodeMaster applyAfterFrom) {
		this.applyAfterFrom = applyAfterFrom;
	}

	public boolean isExcessLeaveAllowFullEntitlement() {
		return excessLeaveAllowFullEntitlement;
	}

	public void setExcessLeaveAllowFullEntitlement(
			boolean excessLeaveAllowFullEntitlement) {
		this.excessLeaveAllowFullEntitlement = excessLeaveAllowFullEntitlement;
	}

	public BigDecimal getMinDaysGapBetweenLeave() {
		return minDaysGapBetweenLeave;
	}

	public void setMinDaysGapBetweenLeave(BigDecimal minDaysGapBetweenLeave) {
		this.minDaysGapBetweenLeave = minDaysGapBetweenLeave;
	}

	public BigDecimal getMaxDaysAllowPerYear() {
		return maxDaysAllowPerYear;
	}

	public void setMaxDaysAllowPerYear(BigDecimal maxDaysAllowPerYear) {
		this.maxDaysAllowPerYear = maxDaysAllowPerYear;
	}

	public boolean isAutoApprove() {
		return autoApprove;
	}

	public void setAutoApprove(boolean autoApprove) {
		this.autoApprove = autoApprove;
	}

	public Integer getAutoApproveAfterDays() {
		return autoApproveAfterDays;
	}

	public void setAutoApproveAfterDays(Integer autoApproveAfterDays) {
		this.autoApproveAfterDays = autoApproveAfterDays;
	}

	public Timestamp getLeaveVisibilityStartDate() {
		return leaveVisibilityStartDate;
	}

	public void setLeaveVisibilityStartDate(Timestamp leaveVisibilityStartDate) {
		this.leaveVisibilityStartDate = leaveVisibilityStartDate;
	}

	public Timestamp getLeaveVisibilityEndDate() {
		return leaveVisibilityEndDate;
	}

	public void setLeaveVisibilityEndDate(Timestamp leaveVisibilityEndDate) {
		this.leaveVisibilityEndDate = leaveVisibilityEndDate;
	}

}