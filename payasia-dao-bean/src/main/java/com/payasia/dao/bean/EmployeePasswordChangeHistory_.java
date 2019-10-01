package com.payasia.dao.bean;

import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-02-11T17:40:44.807+0530")
@StaticMetamodel(EmployeePasswordChangeHistory.class)
public class EmployeePasswordChangeHistory_ {
	public static volatile SingularAttribute<EmployeePasswordChangeHistory, Long> empPwdChangeHistoryId;
	public static volatile SingularAttribute<EmployeePasswordChangeHistory, Timestamp> changeDate;
	public static volatile SingularAttribute<EmployeePasswordChangeHistory, String> changedPassword;
	public static volatile SingularAttribute<EmployeePasswordChangeHistory, String> salt;
	public static volatile SingularAttribute<EmployeePasswordChangeHistory, Employee> employee;
}
