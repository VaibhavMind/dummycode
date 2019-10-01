package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-04-09T17:40:58.009+0530")
@StaticMetamodel(CompanyCalendarTemplate.class)
public class CompanyCalendarTemplate_ extends BaseEntity_ {
	public static volatile SingularAttribute<CompanyCalendarTemplate, Long> companyCalendarTemplateId;
	public static volatile SingularAttribute<CompanyCalendarTemplate, String> templateDesc;
	public static volatile SingularAttribute<CompanyCalendarTemplate, String> templateName;
	public static volatile SingularAttribute<CompanyCalendarTemplate, Integer> Start_Year;
	public static volatile SingularAttribute<CompanyCalendarTemplate, Company> company;
	public static volatile SingularAttribute<CompanyCalendarTemplate, CalendarPatternMaster> calendarPatternMaster;
	public static volatile SetAttribute<CompanyCalendarTemplate, CompanyCalendar> companyCalendars;
	public static volatile SetAttribute<CompanyCalendarTemplate, EmployeeCalendarConfig> employeeCalendarConfigs;
}
