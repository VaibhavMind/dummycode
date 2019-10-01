package com.payasia.dao.bean;

import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2012-10-05T13:01:53.374+0530")
@StaticMetamodel(EmpNumberHistory.class)
public class EmpNumberHistory_ extends CompanyBaseEntity_ {
	public static volatile SingularAttribute<EmpNumberHistory, Long> empNoHistoryId;
	public static volatile SingularAttribute<EmpNumberHistory, Timestamp> changedDate;
	public static volatile SingularAttribute<EmpNumberHistory, String> changedEmpNo;
	public static volatile SingularAttribute<EmpNumberHistory, String> prevEmpNo;
	public static volatile SingularAttribute<EmpNumberHistory, String> reason;
	public static volatile SingularAttribute<EmpNumberHistory, Employee> employee1;
	public static volatile SingularAttribute<EmpNumberHistory, Employee> employee2;
}
