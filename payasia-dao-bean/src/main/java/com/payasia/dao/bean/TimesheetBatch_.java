package com.payasia.dao.bean;

import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2016-11-22T17:06:46.311+0530")
@StaticMetamodel(TimesheetBatch.class)
public class TimesheetBatch_ extends BaseEntity_ {
	public static volatile SingularAttribute<TimesheetBatch, Long> timesheetBatchId;
	public static volatile SingularAttribute<TimesheetBatch, String> timesheetBatchDesc;
	public static volatile SingularAttribute<TimesheetBatch, Timestamp> endDate;
	public static volatile SingularAttribute<TimesheetBatch, Timestamp> startDate;
	public static volatile SingularAttribute<TimesheetBatch, Company> company;
	public static volatile SetAttribute<TimesheetBatch, EmployeeTimesheetApplication> employeeTimesheetApplications;
	public static volatile SetAttribute<TimesheetBatch, CoherentOvertimeApplication> coherentOvertimeApplications;
}
