package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2018-07-30T12:55:30.584+0530")
@StaticMetamodel(CoherentTimesheetPreference.class)
public class CoherentTimesheetPreference_ extends BaseEntity_ {
	public static volatile SingularAttribute<CoherentTimesheetPreference, Long> timesheetPreferenceID;
	public static volatile SingularAttribute<CoherentTimesheetPreference, Company> company;
	public static volatile SingularAttribute<CoherentTimesheetPreference, Integer> cutoffDay;
	public static volatile SingularAttribute<CoherentTimesheetPreference, DataDictionary> department;
	public static volatile SingularAttribute<CoherentTimesheetPreference, DataDictionary> costCenter;
	public static volatile SingularAttribute<CoherentTimesheetPreference, DataDictionary> location;
	public static volatile SingularAttribute<CoherentTimesheetPreference, Boolean> useSystemMailAsFromAddress;
	public static volatile SingularAttribute<CoherentTimesheetPreference, Double> workingHoursInADay;
	public static volatile SingularAttribute<CoherentTimesheetPreference, Boolean> is_validation_72_Hours;
}
