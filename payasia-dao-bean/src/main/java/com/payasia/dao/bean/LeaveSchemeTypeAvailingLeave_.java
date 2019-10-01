package com.payasia.dao.bean;

import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2018-04-05T20:34:48.375+0530")
@StaticMetamodel(LeaveSchemeTypeAvailingLeave.class)
public class LeaveSchemeTypeAvailingLeave_ extends CompanyBaseEntity_ {
	public static volatile SingularAttribute<LeaveSchemeTypeAvailingLeave, Long> leaveSchemeTypeAvailingLeaveId;
	public static volatile SingularAttribute<LeaveSchemeTypeAvailingLeave, Boolean> holidaysInclusive;
	public static volatile SingularAttribute<LeaveSchemeTypeAvailingLeave, Boolean> leaveExtension;
	public static volatile SingularAttribute<LeaveSchemeTypeAvailingLeave, Boolean> offInclusive;
	public static volatile SingularAttribute<LeaveSchemeTypeAvailingLeave, BigDecimal> minBlockLeave;
	public static volatile SingularAttribute<LeaveSchemeTypeAvailingLeave, BigDecimal> maxBlockLeave;
	public static volatile SingularAttribute<LeaveSchemeTypeAvailingLeave, Boolean> allowExcessLeave;
	public static volatile SingularAttribute<LeaveSchemeTypeAvailingLeave, BigDecimal> excessLeaveMaxDays;
	public static volatile SingularAttribute<LeaveSchemeTypeAvailingLeave, Integer> excessLeaveMaxFrequency;
	public static volatile SingularAttribute<LeaveSchemeTypeAvailingLeave, Boolean> allowAdvancePosting;
	public static volatile SingularAttribute<LeaveSchemeTypeAvailingLeave, Integer> advanceLeaveApplyBeforeDays;
	public static volatile SingularAttribute<LeaveSchemeTypeAvailingLeave, Integer> advanceLeavePostBeforeDays;
	public static volatile SingularAttribute<LeaveSchemeTypeAvailingLeave, Boolean> allowBackdatePosting;
	public static volatile SingularAttribute<LeaveSchemeTypeAvailingLeave, Integer> backdatePostingAfterDays;
	public static volatile SingularAttribute<LeaveSchemeTypeAvailingLeave, Integer> nextYearPostingBeforeDays;
	public static volatile SingularAttribute<LeaveSchemeTypeAvailingLeave, LeaveSchemeType> considerLeaveBalanceFrom;
	public static volatile SingularAttribute<LeaveSchemeTypeAvailingLeave, Boolean> approvalNotRequired;
	public static volatile SingularAttribute<LeaveSchemeTypeAvailingLeave, String> defaultCCEmail;
	public static volatile SingularAttribute<LeaveSchemeTypeAvailingLeave, String> remarks;
	public static volatile SingularAttribute<LeaveSchemeTypeAvailingLeave, Boolean> sendMailToDefaultCC;
	public static volatile SingularAttribute<LeaveSchemeTypeAvailingLeave, Boolean> attachmentMandatory;
	public static volatile SingularAttribute<LeaveSchemeTypeAvailingLeave, BigDecimal> attachmentExemptionDays;
	public static volatile SingularAttribute<LeaveSchemeTypeAvailingLeave, Integer> applyAfterDays;
	public static volatile SingularAttribute<LeaveSchemeTypeAvailingLeave, AppCodeMaster> applyAfterFrom;
	public static volatile SingularAttribute<LeaveSchemeTypeAvailingLeave, LeaveSchemeType> leaveSchemeType;
	public static volatile SingularAttribute<LeaveSchemeTypeAvailingLeave, Boolean> excessLeaveAllowFullEntitlement;
	public static volatile SetAttribute<LeaveSchemeTypeAvailingLeave, LeaveSchemeTypeCustomField> leaveSchemeTypeCustomFields;
	public static volatile SetAttribute<LeaveSchemeTypeAvailingLeave, LeaveSchemeTypeGrantCondition> leaveSchemeTypeGrantConditions;
	public static volatile SingularAttribute<LeaveSchemeTypeAvailingLeave, BigDecimal> minDaysGapBetweenLeave;
	public static volatile SingularAttribute<LeaveSchemeTypeAvailingLeave, BigDecimal> maxDaysAllowPerYear;
	public static volatile SingularAttribute<LeaveSchemeTypeAvailingLeave, Boolean> autoApprove;
	public static volatile SingularAttribute<LeaveSchemeTypeAvailingLeave, Integer> autoApproveAfterDays;
	public static volatile SingularAttribute<LeaveSchemeTypeAvailingLeave, Timestamp> leaveVisibilityStartDate;
	public static volatile SingularAttribute<LeaveSchemeTypeAvailingLeave, Timestamp> leaveVisibilityEndDate;
}
