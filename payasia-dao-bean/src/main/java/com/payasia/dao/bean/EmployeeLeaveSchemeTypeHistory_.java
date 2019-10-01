package com.payasia.dao.bean;

import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-06-07T17:03:52.027+0530")
@StaticMetamodel(EmployeeLeaveSchemeTypeHistory.class)
public class EmployeeLeaveSchemeTypeHistory_ extends CompanyBaseEntity_ {
	public static volatile SingularAttribute<EmployeeLeaveSchemeTypeHistory, Long> employeeLeaveSchemeTypeHistoryId;
	public static volatile SingularAttribute<EmployeeLeaveSchemeTypeHistory, BigDecimal> days;
	public static volatile SingularAttribute<EmployeeLeaveSchemeTypeHistory, Timestamp> endDate;
	public static volatile SingularAttribute<EmployeeLeaveSchemeTypeHistory, Boolean> forfeitAtEndDate;
	public static volatile SingularAttribute<EmployeeLeaveSchemeTypeHistory, Timestamp> startDate;
	public static volatile SingularAttribute<EmployeeLeaveSchemeTypeHistory, LeaveSessionMaster> startSessionMaster;
	public static volatile SingularAttribute<EmployeeLeaveSchemeTypeHistory, LeaveSessionMaster> endSessionMaster;
	public static volatile SingularAttribute<EmployeeLeaveSchemeTypeHistory, String> reason;
	public static volatile SingularAttribute<EmployeeLeaveSchemeTypeHistory, AppCodeMaster> appCodeMaster;
	public static volatile SingularAttribute<EmployeeLeaveSchemeTypeHistory, EmployeeLeaveSchemeType> employeeLeaveSchemeType;
	public static volatile SingularAttribute<EmployeeLeaveSchemeTypeHistory, LeaveApplication> leaveApplication;
	public static volatile SingularAttribute<EmployeeLeaveSchemeTypeHistory, LeaveStatusMaster> leaveStatusMaster;
}
