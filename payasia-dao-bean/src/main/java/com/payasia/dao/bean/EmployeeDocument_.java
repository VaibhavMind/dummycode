package com.payasia.dao.bean;

import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2012-10-05T13:02:05.918+0530")
@StaticMetamodel(EmployeeDocument.class)
public class EmployeeDocument_ extends CompanyBaseEntity_ {
	public static volatile SingularAttribute<EmployeeDocument, Long> empDocumentId;
	public static volatile SingularAttribute<EmployeeDocument, String> description;
	public static volatile SingularAttribute<EmployeeDocument, String> fileName;
	public static volatile SingularAttribute<EmployeeDocument, String> fileType;
	public static volatile SingularAttribute<EmployeeDocument, Timestamp> uploadedDate;
	public static volatile SingularAttribute<EmployeeDocument, Integer> year;
	public static volatile SingularAttribute<EmployeeDocument, Employee> employee;
}
