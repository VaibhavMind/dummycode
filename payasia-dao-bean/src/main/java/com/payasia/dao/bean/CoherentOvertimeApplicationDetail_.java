package com.payasia.dao.bean;

import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2018-07-30T12:55:30.565+0530")
@StaticMetamodel(CoherentOvertimeApplicationDetail.class)
public class CoherentOvertimeApplicationDetail_ extends CompanyBaseEntity_ {
	public static volatile SingularAttribute<CoherentOvertimeApplicationDetail, Long> overtimeApplicationDetailID;
	public static volatile SingularAttribute<CoherentOvertimeApplicationDetail, CoherentOvertimeApplication> coherentOvertimeApplication;
	public static volatile SingularAttribute<CoherentOvertimeApplicationDetail, Timestamp> overtimeDate;
	public static volatile SingularAttribute<CoherentOvertimeApplicationDetail, Timestamp> startTime;
	public static volatile SingularAttribute<CoherentOvertimeApplicationDetail, Timestamp> endTime;
	public static volatile SingularAttribute<CoherentOvertimeApplicationDetail, Timestamp> mealDuration;
	public static volatile SingularAttribute<CoherentOvertimeApplicationDetail, AppCodeMaster> dayType;
	public static volatile SingularAttribute<CoherentOvertimeApplicationDetail, Double> otHours;
	public static volatile SingularAttribute<CoherentOvertimeApplicationDetail, Double> ot15Hours;
	public static volatile SingularAttribute<CoherentOvertimeApplicationDetail, Double> ot10Day;
	public static volatile SingularAttribute<CoherentOvertimeApplicationDetail, Double> ot20Day;
	public static volatile SingularAttribute<CoherentOvertimeApplicationDetail, String> remarks;
	public static volatile SingularAttribute<CoherentOvertimeApplicationDetail, Boolean> startTimeChanged;
	public static volatile SingularAttribute<CoherentOvertimeApplicationDetail, Boolean> endTimeChanged;
	public static volatile SingularAttribute<CoherentOvertimeApplicationDetail, Boolean> mealDurationChanged;
	public static volatile SingularAttribute<CoherentOvertimeApplicationDetail, Boolean> dayTypeChanged;
}
