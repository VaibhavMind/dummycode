package com.payasia.dao.bean;

import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-04-09T18:27:00.872+0530")
@StaticMetamodel(EmployeeCalendarConfig.class)
public class EmployeeCalendarConfig_ extends CompanyBaseEntity_ {
	public static volatile SingularAttribute<EmployeeCalendarConfig, Long> employeeCalendarConfigId;
	public static volatile SingularAttribute<EmployeeCalendarConfig, Employee> employee;
	public static volatile SingularAttribute<EmployeeCalendarConfig, Timestamp> startDate;
	public static volatile SingularAttribute<EmployeeCalendarConfig, Timestamp> endDate;
	public static volatile SingularAttribute<EmployeeCalendarConfig, CompanyCalendarTemplate> companyCalendarTemplate;
	public static volatile SetAttribute<EmployeeCalendarConfig, EmployeeCalendar> employeeCalendar;
}
