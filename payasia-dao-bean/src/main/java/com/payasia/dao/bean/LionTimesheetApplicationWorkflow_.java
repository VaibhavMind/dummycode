package com.payasia.dao.bean;

import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2016-11-21T15:20:52.578+0530")
@StaticMetamodel(LionTimesheetApplicationWorkflow.class)
public class LionTimesheetApplicationWorkflow_ extends CompanyUpdatedBaseEntity_ {
	public static volatile SingularAttribute<LionTimesheetApplicationWorkflow, Long> timesheetWorkflowId;
	public static volatile SingularAttribute<LionTimesheetApplicationWorkflow, LionEmployeeTimesheetApplicationDetail> employeeTimesheetApplicationDetail;
	public static volatile SingularAttribute<LionTimesheetApplicationWorkflow, TimesheetStatusMaster> timesheetStatusMaster;
	public static volatile SingularAttribute<LionTimesheetApplicationWorkflow, Timestamp> createdDate;
	public static volatile SingularAttribute<LionTimesheetApplicationWorkflow, Employee> createdBy;
}
