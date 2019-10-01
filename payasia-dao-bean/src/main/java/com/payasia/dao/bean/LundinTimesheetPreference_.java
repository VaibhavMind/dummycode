package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2015-05-01T11:15:18.869+0530")
@StaticMetamodel(LundinTimesheetPreference.class)
public class LundinTimesheetPreference_ extends BaseEntity_ {
	public static volatile SingularAttribute<LundinTimesheetPreference, Long> timesheetPreferenceId;
	public static volatile SingularAttribute<LundinTimesheetPreference, Company> company;
	public static volatile SingularAttribute<LundinTimesheetPreference, Integer> cutoff_Day;
	public static volatile SingularAttribute<LundinTimesheetPreference, DataDictionary> dataDictionary;
	public static volatile SingularAttribute<LundinTimesheetPreference, DataDictionary> costCategory;
	public static volatile SingularAttribute<LundinTimesheetPreference, DataDictionary> dailyRate;
	public static volatile SingularAttribute<LundinTimesheetPreference, DataDictionary> autoTimewrite;
	public static volatile SingularAttribute<LundinTimesheetPreference, Boolean> useSystemMailAsFromAddress;
}
