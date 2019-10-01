package com.payasia.dao.bean;

import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-11-08T10:13:08.687+0530")
@StaticMetamodel(YearEndProcessSchedule.class)
public class YearEndProcessSchedule_ extends BaseEntity_ {
	public static volatile SingularAttribute<YearEndProcessSchedule, Long> yearEndProcessScheduleId;
	public static volatile SingularAttribute<YearEndProcessSchedule, Company> company;
	public static volatile SingularAttribute<YearEndProcessSchedule, Timestamp> leaveRollOver;
	public static volatile SingularAttribute<YearEndProcessSchedule, Timestamp> leave_Activate;
}
