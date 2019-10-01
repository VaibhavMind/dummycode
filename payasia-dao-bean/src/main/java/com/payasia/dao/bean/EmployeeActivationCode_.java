package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-11-07T13:07:36.759+0530")
@StaticMetamodel(EmployeeActivationCode.class)
public class EmployeeActivationCode_ extends CompanyBaseEntity_ {
	public static volatile SingularAttribute<EmployeeActivationCode, Long> employeeActivationCodeId;
	public static volatile SingularAttribute<EmployeeActivationCode, String> activationCode;
	public static volatile SingularAttribute<EmployeeActivationCode, Employee> employee;
	public static volatile SetAttribute<EmployeeActivationCode, EmployeeMobileDetails> employeeMobileDetails;
	public static volatile SingularAttribute<EmployeeActivationCode, Boolean> active;
}
