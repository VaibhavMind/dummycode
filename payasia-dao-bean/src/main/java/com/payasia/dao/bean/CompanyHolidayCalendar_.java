package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-04-09T17:46:19.220+0530")
@StaticMetamodel(CompanyHolidayCalendar.class)
public class CompanyHolidayCalendar_ extends BaseEntity_ {
	public static volatile SingularAttribute<CompanyHolidayCalendar, Long> companyHolidayCalendarId;
	public static volatile SingularAttribute<CompanyHolidayCalendar, String> calendarName;
	public static volatile SingularAttribute<CompanyHolidayCalendar, String> calendarDesc;
	public static volatile SingularAttribute<CompanyHolidayCalendar, Company> company;
	public static volatile SetAttribute<CompanyHolidayCalendar, CompanyHolidayCalendarDetail> companyHolidayCalendarDetails;
	public static volatile SetAttribute<CompanyHolidayCalendar, EmployeeHolidayCalendar> employeeHolidayCalendars;
}
