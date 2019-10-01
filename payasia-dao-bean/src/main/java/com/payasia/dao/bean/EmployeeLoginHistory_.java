package com.payasia.dao.bean;

import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2018-05-16T16:27:41.693+0530")
@StaticMetamodel(EmployeeLoginHistory.class)
public class EmployeeLoginHistory_ {
	public static volatile SingularAttribute<EmployeeLoginHistory, Long> empLoginHistoryId;
	public static volatile SingularAttribute<EmployeeLoginHistory, Timestamp> loggedInDate;
	public static volatile SingularAttribute<EmployeeLoginHistory, String> status;
	public static volatile SingularAttribute<EmployeeLoginHistory, String> ipAddress;
	public static volatile SingularAttribute<EmployeeLoginHistory, String> loginMode;
	public static volatile SingularAttribute<EmployeeLoginHistory, Employee> employee;
}
