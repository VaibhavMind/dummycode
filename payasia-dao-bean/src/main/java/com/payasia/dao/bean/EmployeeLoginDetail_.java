package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-05-18T12:25:02.678+0530")
@StaticMetamodel(EmployeeLoginDetail.class)
public class EmployeeLoginDetail_ extends CompanyBaseEntity_ {
	public static volatile SingularAttribute<EmployeeLoginDetail, Long> employeeLoginDetailId;
	public static volatile SingularAttribute<EmployeeLoginDetail, String> loginName;
	public static volatile SingularAttribute<EmployeeLoginDetail, String> password;
	public static volatile SingularAttribute<EmployeeLoginDetail, Boolean> passwordSent;
	public static volatile SingularAttribute<EmployeeLoginDetail, Integer> invalidLoginAttempt;
	public static volatile SingularAttribute<EmployeeLoginDetail, Boolean> passwordReset;
	public static volatile SingularAttribute<EmployeeLoginDetail, String> salt;
	public static volatile SingularAttribute<EmployeeLoginDetail, Employee> employee;
}
