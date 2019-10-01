package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2016-11-21T15:22:44.299+0530")
@StaticMetamodel(LionEmployeeTimesheet.class)
public class LionEmployeeTimesheet_ extends CompanyBaseEntity_ {
	public static volatile SingularAttribute<LionEmployeeTimesheet, Long> lionEmployeeTimesheetID;
	public static volatile SingularAttribute<LionEmployeeTimesheet, EmployeeTimesheetApplication> employeeTimesheetApplication;
	public static volatile SingularAttribute<LionEmployeeTimesheet, Double> TimesheetTotalHours;
	public static volatile SingularAttribute<LionEmployeeTimesheet, Double> excessHoursWorked;
}
