package com.payasia.dao.bean;

import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2016-09-26T10:07:10.520+0530")
@StaticMetamodel(TimesheetApplicationWorkflow.class)
public class TimesheetApplicationWorkflow_ extends CompanyUpdatedBaseEntity_ {
	public static volatile SingularAttribute<TimesheetApplicationWorkflow, Long> timesheetWorkflowId;
	public static volatile SingularAttribute<TimesheetApplicationWorkflow, String> emailCC;
	public static volatile SingularAttribute<TimesheetApplicationWorkflow, String> forwardTo;
	public static volatile SingularAttribute<TimesheetApplicationWorkflow, String> remarks;
	public static volatile SingularAttribute<TimesheetApplicationWorkflow, Timestamp> createdDate;
	public static volatile SingularAttribute<TimesheetApplicationWorkflow, Employee> createdBy;
	public static volatile SingularAttribute<TimesheetApplicationWorkflow, EmployeeTimesheetApplication> employeeTimesheetApplication;
	public static volatile SingularAttribute<TimesheetApplicationWorkflow, TimesheetStatusMaster> timesheetStatusMaster;
}
