package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-04-09T16:56:04.173+0530")
@StaticMetamodel(LeaveScheme.class)
public class LeaveScheme_ extends BaseEntity_ {
	public static volatile SingularAttribute<LeaveScheme, Long> leaveSchemeId;
	public static volatile SingularAttribute<LeaveScheme, String> schemeName;
	public static volatile SingularAttribute<LeaveScheme, Boolean> visibility;
	public static volatile SingularAttribute<LeaveScheme, Company> company;
	public static volatile SetAttribute<LeaveScheme, LeaveSchemeType> leaveSchemeTypes;
	public static volatile SetAttribute<LeaveScheme, LeaveSchemeWorkflow> leaveSchemeWorkflows;
	public static volatile SetAttribute<LeaveScheme, EmployeeLeaveScheme> employeeLeaveSchemes;
	public static volatile SetAttribute<LeaveScheme, ReminderEventConfig> reminderEventConfigs;
}
