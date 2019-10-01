package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-04-09T17:54:19.359+0530")
@StaticMetamodel(LeaveSchemeType.class)
public class LeaveSchemeType_ extends CompanyBaseEntity_ {
	public static volatile SingularAttribute<LeaveSchemeType, Long> leaveSchemeTypeId;
	public static volatile SingularAttribute<LeaveSchemeType, Boolean> visibility;
	public static volatile SingularAttribute<LeaveSchemeType, Boolean> workflowChanged;
	public static volatile SingularAttribute<LeaveSchemeType, LeaveScheme> leaveScheme;
	public static volatile SingularAttribute<LeaveSchemeType, LeaveTypeMaster> leaveTypeMaster;
	public static volatile SetAttribute<LeaveSchemeType, LeaveSchemeTypeGranting> leaveSchemeTypeGrantings;
	public static volatile SetAttribute<LeaveSchemeType, LeaveSchemeTypeProration> leaveSchemeTypeProrations;
	public static volatile SetAttribute<LeaveSchemeType, LeaveSchemeTypeAvailingLeave> LeaveSchemeTypeAvailingLeaves;
	public static volatile SetAttribute<LeaveSchemeType, LeaveSchemeTypeAvailingLeave> leaveSchemeTypeAvailingLeaves1;
	public static volatile SetAttribute<LeaveSchemeType, LeaveSchemeTypeYearEnd> leaveSchemeTypeYearEnds;
	public static volatile SetAttribute<LeaveSchemeType, LeaveSchemeTypeShortList> leaveSchemeTypeShortLists;
	public static volatile SetAttribute<LeaveSchemeType, LeaveSchemeTypeWorkflow> leaveSchemeTypeWorkflows;
	public static volatile SetAttribute<LeaveSchemeType, EmployeeLeaveSchemeType> employeeLeaveSchemeTypes;
	public static volatile SetAttribute<LeaveSchemeType, LeaveGrantBatchDetail> leaveGrantBatchDetails;
	public static volatile SetAttribute<LeaveSchemeType, LeaveSchemeTypeGrantCondition> leaveSchemeTypeGrantConditions;
	public static volatile SetAttribute<LeaveSchemeType, LeaveYearEndBatch> leaveYearEndBatchs;
}
