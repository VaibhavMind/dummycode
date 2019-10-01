package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-04-09T18:22:08.619+0530")
@StaticMetamodel(CalendarPatternDetail.class)
public class CalendarPatternDetail_ extends CompanyBaseEntity_ {
	public static volatile SingularAttribute<CalendarPatternDetail, Long> calendarPatternDetailId;
	public static volatile SingularAttribute<CalendarPatternDetail, Integer> patternIndex;
	public static volatile SingularAttribute<CalendarPatternDetail, CalendarCodeMaster> calendarCodeMaster;
	public static volatile SingularAttribute<CalendarPatternDetail, CalendarPatternMaster> calendarPatternMaster;
}
