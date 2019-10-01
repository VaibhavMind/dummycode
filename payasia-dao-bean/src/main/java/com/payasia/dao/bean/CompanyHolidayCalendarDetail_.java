package com.payasia.dao.bean;

import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-04-09T18:30:17.664+0530")
@StaticMetamodel(CompanyHolidayCalendarDetail.class)
public class CompanyHolidayCalendarDetail_ extends CompanyBaseEntity_ {
	public static volatile SingularAttribute<CompanyHolidayCalendarDetail, Long> companyHolidayCalendarDetailId;
	public static volatile SingularAttribute<CompanyHolidayCalendarDetail, CountryMaster> countryMaster;
	public static volatile SingularAttribute<CompanyHolidayCalendarDetail, StateMaster> stateMaster;
	public static volatile SingularAttribute<CompanyHolidayCalendarDetail, Timestamp> holidayDate;
	public static volatile SingularAttribute<CompanyHolidayCalendarDetail, String> holidayDesc;
	public static volatile SingularAttribute<CompanyHolidayCalendarDetail, CompanyHolidayCalendar> companyHolidayCalendar;
}
