package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2015-05-01T11:15:18.682+0530")
@StaticMetamodel(CalendarPatternMaster.class)
public class CalendarPatternMaster_ extends BaseEntity_ {
	public static volatile SingularAttribute<CalendarPatternMaster, Long> calendarPatternId;
	public static volatile SingularAttribute<CalendarPatternMaster, String> patternName;
	public static volatile SingularAttribute<CalendarPatternMaster, String> patternDesc;
	public static volatile SingularAttribute<CalendarPatternMaster, Company> company;
	public static volatile SetAttribute<CalendarPatternMaster, CalendarPatternDetail> calendarPatternDetails;
}
