package com.payasia.dao.bean;

import java.math.BigDecimal;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-06-14T18:06:45.898+0530")
@StaticMetamodel(EmployeeLeaveSchemeType.class)
public class EmployeeLeaveSchemeType_ extends CompanyBaseEntity_ {
	public static volatile SingularAttribute<EmployeeLeaveSchemeType, Long> employeeLeaveSchemeTypeId;
	public static volatile SingularAttribute<EmployeeLeaveSchemeType, BigDecimal> balance;
	public static volatile SingularAttribute<EmployeeLeaveSchemeType, BigDecimal> carriedForward;
	public static volatile SingularAttribute<EmployeeLeaveSchemeType, BigDecimal> credited;
	public static volatile SingularAttribute<EmployeeLeaveSchemeType, BigDecimal> encashed;
	public static volatile SingularAttribute<EmployeeLeaveSchemeType, BigDecimal> forfeited;
	public static volatile SingularAttribute<EmployeeLeaveSchemeType, BigDecimal> pending;
	public static volatile SingularAttribute<EmployeeLeaveSchemeType, BigDecimal> taken;
	public static volatile SingularAttribute<EmployeeLeaveSchemeType, Boolean> active;
	public static volatile SingularAttribute<EmployeeLeaveSchemeType, EmployeeLeaveScheme> employeeLeaveScheme;
	public static volatile SingularAttribute<EmployeeLeaveSchemeType, LeaveSchemeType> leaveSchemeType;
	public static volatile SetAttribute<EmployeeLeaveSchemeType, EmployeeLeaveSchemeTypeHistory> employeeLeaveSchemeTypeHistories;
	public static volatile SetAttribute<EmployeeLeaveSchemeType, EmployeeLeaveReviewer> employeeLeaveReviewers;
	public static volatile SetAttribute<EmployeeLeaveSchemeType, LeaveApplication> leaveApplications;
	public static volatile SetAttribute<EmployeeLeaveSchemeType, EmployeeLeaveDistribution> employeeLeaveDistributions;
}
