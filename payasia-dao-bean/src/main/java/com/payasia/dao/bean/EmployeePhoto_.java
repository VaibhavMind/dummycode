package com.payasia.dao.bean;

import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2016-11-19T17:43:45.893+0530")
@StaticMetamodel(EmployeePhoto.class)
public class EmployeePhoto_ extends CompanyBaseEntity_ {
	public static volatile SingularAttribute<EmployeePhoto, Long> employee_ID;
	public static volatile SingularAttribute<EmployeePhoto, byte[]> photo;
	public static volatile SingularAttribute<EmployeePhoto, Timestamp> uploadedDate;
	public static volatile SingularAttribute<EmployeePhoto, String> fileName;
	public static volatile SingularAttribute<EmployeePhoto, String> fileType;
	public static volatile SingularAttribute<EmployeePhoto, Employee> employee;
}
