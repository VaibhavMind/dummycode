package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-06-14T18:06:45.985+0530")
@StaticMetamodel(LeaveTypeMaster.class)
public class LeaveTypeMaster_ extends BaseEntity_ {
	public static volatile SingularAttribute<LeaveTypeMaster, Long> leaveTypeId;
	public static volatile SingularAttribute<LeaveTypeMaster, String> accountCode;
	public static volatile SingularAttribute<LeaveTypeMaster, String> code;
	public static volatile SingularAttribute<LeaveTypeMaster, String> leaveTypeDesc;
	public static volatile SingularAttribute<LeaveTypeMaster, Integer> sortOrder;
	public static volatile SingularAttribute<LeaveTypeMaster, AppCodeMaster> frontEndViewMode;
	public static volatile SingularAttribute<LeaveTypeMaster, Boolean> frontEndApplicationMode;
	public static volatile SingularAttribute<LeaveTypeMaster, AppCodeMaster> backEndViewMode;
	public static volatile SingularAttribute<LeaveTypeMaster, Boolean> backEndApplicationMode;
	public static volatile SingularAttribute<LeaveTypeMaster, String> leaveTypeName;
	public static volatile SingularAttribute<LeaveTypeMaster, Boolean> visibility;
	public static volatile SetAttribute<LeaveTypeMaster, LeaveSchemeType> leaveSchemeTypes;
	public static volatile SingularAttribute<LeaveTypeMaster, Company> company;
	public static volatile SetAttribute<LeaveTypeMaster, ReminderEventConfig> reminderEventConfigs;
	public static volatile SingularAttribute<LeaveTypeMaster, AppCodeMaster> leaveType;
}
