package com.payasia.dao.bean;

import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2016-09-27T11:24:15.855+0530")
@StaticMetamodel(LionTimesheetPreference.class)
public class LionTimesheetPreference_ extends BaseEntity_ {
	public static volatile SingularAttribute<LionTimesheetPreference, Long> timesheetPreferenceID;
	public static volatile SingularAttribute<LionTimesheetPreference, Company> company;
	public static volatile SingularAttribute<LionTimesheetPreference, Timestamp> periodStart;
	public static volatile SingularAttribute<LionTimesheetPreference, AppCodeMaster> cutoffCycle;
	public static volatile SingularAttribute<LionTimesheetPreference, DataDictionary> location;
	public static volatile SingularAttribute<LionTimesheetPreference, Boolean> useSystemMailAsFromAddress;
}
