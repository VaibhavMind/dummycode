package com.payasia.dao.bean;

import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-04-09T18:28:26.107+0530")
@StaticMetamodel(EmployeeCalendar.class)
public class EmployeeCalendar_ extends CompanyBaseEntity_ {
	public static volatile SingularAttribute<EmployeeCalendar, Long> employeeCalendarId;
	public static volatile SingularAttribute<EmployeeCalendar, Timestamp> calendarDate;
	public static volatile SingularAttribute<EmployeeCalendar, CalendarCodeMaster> calendarCodeMaster;
	public static volatile SingularAttribute<EmployeeCalendar, EmployeeCalendarConfig> employeeCalendarConfig;
}
