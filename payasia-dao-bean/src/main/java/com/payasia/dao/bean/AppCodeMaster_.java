package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2015-05-01T11:15:18.666+0530")
@StaticMetamodel(AppCodeMaster.class)
public class AppCodeMaster_ {
	public static volatile SingularAttribute<AppCodeMaster, Long> appCodeID;
	public static volatile SingularAttribute<AppCodeMaster, String> category;
	public static volatile SingularAttribute<AppCodeMaster, String> codeDesc;
	public static volatile SingularAttribute<AppCodeMaster, String> codeValue;
	public static volatile SingularAttribute<AppCodeMaster, String> labelKey;
	public static volatile SetAttribute<AppCodeMaster, CalendarCodeMaster> calendarCodeMasters;
	public static volatile SetAttribute<AppCodeMaster, LeaveSchemeTypeGranting> leaveSchemeTypeGrantings1;
	public static volatile SetAttribute<AppCodeMaster, LeaveSchemeTypeGranting> leaveSchemeTypeGrantings2;
	public static volatile SetAttribute<AppCodeMaster, LeaveSchemeTypeProration> leaveSchemeTypeProrations1;
	public static volatile SetAttribute<AppCodeMaster, LeaveSchemeTypeProration> leaveSchemeTypeProrations2;
	public static volatile SetAttribute<AppCodeMaster, ClaimTemplate> claimTemplateProration;
	public static volatile SetAttribute<AppCodeMaster, LeaveSchemeTypeProration> leaveSchemeTypeProrations3;
	public static volatile SetAttribute<AppCodeMaster, EmployeeLeaveSchemeTypeHistory> employeeLeaveSchemeTypeHistories;
	public static volatile SetAttribute<AppCodeMaster, LeaveTypeMaster> frontEndViewModes;
	public static volatile SetAttribute<AppCodeMaster, LeaveTypeMaster> backEndViewModes;
	public static volatile SetAttribute<AppCodeMaster, ClaimTemplate> frontEndViewMode;
	public static volatile SetAttribute<AppCodeMaster, ClaimTemplate> backEndViewMode;
	public static volatile SetAttribute<AppCodeMaster, ReminderEventConfig> reminderEventConfigs;
	public static volatile SetAttribute<AppCodeMaster, LeaveSchemeTypeGrantCondition> leaveSchemeTypeGrantConditions;
	public static volatile SetAttribute<AppCodeMaster, LeavePreference> leavePreferences;
	public static volatile SetAttribute<AppCodeMaster, ClaimTemplateItemClaimType> claimTemplateItemClaimTypes;
	public static volatile SetAttribute<AppCodeMaster, ClaimApplicationItemWorkflow> claimTemplateItemWorkflowActions;
	public static volatile SetAttribute<AppCodeMaster, ClaimApplicationItemWorkflow> claimTemplateItemWorkflowStatuses;
	public static volatile SetAttribute<AppCodeMaster, ClaimTemplate> claimTemplates;
	public static volatile SetAttribute<AppCodeMaster, ClaimTemplateItemCustomField> claimTemplateItemCustomFields;
	public static volatile SetAttribute<AppCodeMaster, LeaveSchemeTypeAvailingLeave> leaveSchemeTypeAvailingLeaves;
}
