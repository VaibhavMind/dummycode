package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-04-09T17:41:30.708+0530")
@StaticMetamodel(CalendarCodeMaster.class)
public class CalendarCodeMaster_ extends BaseEntity_ {
	public static volatile SingularAttribute<CalendarCodeMaster, Long> calendarCodeId;
	public static volatile SingularAttribute<CalendarCodeMaster, String> code;
	public static volatile SingularAttribute<CalendarCodeMaster, AppCodeMaster> appCodeMaster;
	public static volatile SingularAttribute<CalendarCodeMaster, ModuleMaster> moduleMaster;
	public static volatile SetAttribute<CalendarCodeMaster, CalendarPatternDetail> calendarPatternDetails;
	public static volatile SetAttribute<CalendarCodeMaster, CompanyCalendar> companyCalendars;
	public static volatile SetAttribute<CalendarCodeMaster, EmployeeCalendar> employeeCalendar;
	public static volatile SingularAttribute<CalendarCodeMaster, Company> company;
}
