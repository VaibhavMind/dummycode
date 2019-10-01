package com.payasia.dao.bean;

import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2015-05-01T11:15:18.791+0530")
@StaticMetamodel(EmployeeMobileDetails.class)
public class EmployeeMobileDetails_ extends CompanyBaseEntity_ {
	public static volatile SingularAttribute<EmployeeMobileDetails, Long> employeeMobileDetails;
	public static volatile SingularAttribute<EmployeeMobileDetails, EmployeeActivationCode> employeeActivationCode;
	public static volatile SingularAttribute<EmployeeMobileDetails, String> resolution;
	public static volatile SingularAttribute<EmployeeMobileDetails, String> pixelDensity;
	public static volatile SingularAttribute<EmployeeMobileDetails, Boolean> active;
	public static volatile SingularAttribute<EmployeeMobileDetails, Boolean> expired;
	public static volatile SingularAttribute<EmployeeMobileDetails, String> deviceID;
	public static volatile SingularAttribute<EmployeeMobileDetails, String> deviceIMEI;
	public static volatile SingularAttribute<EmployeeMobileDetails, String> deviceMacId;
	public static volatile SingularAttribute<EmployeeMobileDetails, String> deviceOS;
	public static volatile SingularAttribute<EmployeeMobileDetails, String> deviceOSVersion;
	public static volatile SingularAttribute<EmployeeMobileDetails, String> deviceToken;
	public static volatile SingularAttribute<EmployeeMobileDetails, Timestamp> activationDate;
}
