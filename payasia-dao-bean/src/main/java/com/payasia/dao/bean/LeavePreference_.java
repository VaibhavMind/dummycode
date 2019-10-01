package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2018-04-05T20:34:48.322+0530")
@StaticMetamodel(LeavePreference.class)
public class LeavePreference_ extends BaseEntity_ {
	public static volatile SingularAttribute<LeavePreference, Long> leavePreferenceId;
	public static volatile SingularAttribute<LeavePreference, Company> company;
	public static volatile SingularAttribute<LeavePreference, MonthMaster> startMonth;
	public static volatile SingularAttribute<LeavePreference, MonthMaster> endMonth;
	public static volatile SingularAttribute<LeavePreference, DataDictionary> leaveCalendarBasedOnCustomField;
	public static volatile SingularAttribute<LeavePreference, Integer> cancelLeaveValidationDays;
	public static volatile SingularAttribute<LeavePreference, Boolean> useSystemMailAsFromAddress;
	public static volatile SingularAttribute<LeavePreference, Boolean> allowManagerSelfApproveLeave;
	public static volatile SingularAttribute<LeavePreference, Boolean> allowYEPIfLeaveApplicationsArePending;
	public static volatile SingularAttribute<LeavePreference, Boolean> hideLeaveTypeInLeaveCalendarDetail;
	public static volatile SingularAttribute<LeavePreference, Boolean> considerOffInLeaveConjunction;
	public static volatile SingularAttribute<LeavePreference, AppCodeMaster> leaveTransactionToShow;
	public static volatile SingularAttribute<LeavePreference, Boolean> showEncashed;
	public static volatile SingularAttribute<LeavePreference, Boolean> dashboardSameAsAdmin;
	public static volatile SingularAttribute<LeavePreference, Boolean> showFullEntitlement;
	public static volatile SingularAttribute<LeavePreference, Boolean> showLeaveCalendarBasedOnCustomField;
	public static volatile SingularAttribute<LeavePreference, Boolean> showLeaveBalanceCustomReport;
	public static volatile SingularAttribute<LeavePreference, Boolean> defaultEmailCC;
	public static volatile SingularAttribute<LeavePreference, Boolean> leaveWorkflowNotRequired;
	public static volatile SingularAttribute<LeavePreference, String> keypayPayRunParameter;
	public static volatile SingularAttribute<LeavePreference, Double> workingHoursInADay;
	public static volatile SingularAttribute<LeavePreference, Boolean> leaveHideAddColumn;
	public static volatile SingularAttribute<LeavePreference, Boolean> preApprovalRequired;
	public static volatile SingularAttribute<LeavePreference, Boolean> leaveExtensionRequired;
	public static volatile SingularAttribute<LeavePreference, AppCodeMaster> leaveUnit;
}
