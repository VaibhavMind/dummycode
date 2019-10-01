package com.payasia.dao.bean;

import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-06-14T18:06:45.888+0530")
@StaticMetamodel(EmployeeLeaveScheme.class)
public class EmployeeLeaveScheme_ extends CompanyBaseEntity_ {
	public static volatile SingularAttribute<EmployeeLeaveScheme, Long> employeeLeaveSchemeId;
	public static volatile SingularAttribute<EmployeeLeaveScheme, Timestamp> startDate;
	public static volatile SingularAttribute<EmployeeLeaveScheme, Timestamp> endDate;
	public static volatile SetAttribute<EmployeeLeaveScheme, EmployeeLeaveReviewer> employeeLeaveReviewers;
	public static volatile SingularAttribute<EmployeeLeaveScheme, Employee> employee;
	public static volatile SingularAttribute<EmployeeLeaveScheme, LeaveScheme> leaveScheme;
	public static volatile SetAttribute<EmployeeLeaveScheme, EmployeeLeaveSchemeType> employeeLeaveSchemeTypes;
}
