package com.payasia.dao.bean;

import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-04-09T18:25:48.679+0530")
@StaticMetamodel(CompanyCalendar.class)
public class CompanyCalendar_ extends CompanyBaseEntity_ {
	public static volatile SingularAttribute<CompanyCalendar, Long> companyCalendarId;
	public static volatile SingularAttribute<CompanyCalendar, Timestamp> calendarDate;
	public static volatile SingularAttribute<CompanyCalendar, CalendarCodeMaster> calendarCodeMaster;
	public static volatile SingularAttribute<CompanyCalendar, CompanyCalendarTemplate> companyCalendarTemplate;
}
