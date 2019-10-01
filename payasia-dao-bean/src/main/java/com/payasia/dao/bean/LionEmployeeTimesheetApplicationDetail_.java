package com.payasia.dao.bean;

import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2018-08-14T17:23:31.344+0530")
@StaticMetamodel(LionEmployeeTimesheetApplicationDetail.class)
public class LionEmployeeTimesheetApplicationDetail_ extends CompanyBaseEntity_ {
	public static volatile SingularAttribute<LionEmployeeTimesheetApplicationDetail, Long> employeeTimesheetDetailID;
	public static volatile SingularAttribute<LionEmployeeTimesheetApplicationDetail, EmployeeTimesheetApplication> employeeTimesheetApplication;
	public static volatile SingularAttribute<LionEmployeeTimesheetApplicationDetail, Timestamp> timesheetDate;
	public static volatile SingularAttribute<LionEmployeeTimesheetApplicationDetail, Timestamp> inTime;
	public static volatile SingularAttribute<LionEmployeeTimesheetApplicationDetail, String> remarks;
	public static volatile SingularAttribute<LionEmployeeTimesheetApplicationDetail, Timestamp> outTime;
	public static volatile SingularAttribute<LionEmployeeTimesheetApplicationDetail, Timestamp> breakTimeHours;
	public static volatile SingularAttribute<LionEmployeeTimesheetApplicationDetail, Double> totalHoursWorked;
	public static volatile SingularAttribute<LionEmployeeTimesheetApplicationDetail, Boolean> inTimeChanged;
	public static volatile SingularAttribute<LionEmployeeTimesheetApplicationDetail, Boolean> outTimeChanged;
	public static volatile SingularAttribute<LionEmployeeTimesheetApplicationDetail, Boolean> breakTimeHoursChanged;
	public static volatile SingularAttribute<LionEmployeeTimesheetApplicationDetail, TimesheetStatusMaster> timesheetStatusMaster;
}
