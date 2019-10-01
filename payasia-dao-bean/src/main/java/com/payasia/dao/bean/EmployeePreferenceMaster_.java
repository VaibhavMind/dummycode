package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2012-09-26T13:44:15.175+0530")
@StaticMetamodel(EmployeePreferenceMaster.class)
public class EmployeePreferenceMaster_ extends BaseEntity_ {
	public static volatile SingularAttribute<EmployeePreferenceMaster, Long> empPrefID;
	public static volatile SingularAttribute<EmployeePreferenceMaster, Boolean> allowFutureDateJoining;
	public static volatile SingularAttribute<EmployeePreferenceMaster, Boolean> displayDirectIndirectEmployees;
	public static volatile SingularAttribute<EmployeePreferenceMaster, Boolean> displayResignedEmployees;
	public static volatile SingularAttribute<EmployeePreferenceMaster, Integer> probationPeriod;
	public static volatile SingularAttribute<EmployeePreferenceMaster, Company> company;
	public static volatile SingularAttribute<EmployeePreferenceMaster, EmployeeTypeMaster> employeeTypeMaster;
}
