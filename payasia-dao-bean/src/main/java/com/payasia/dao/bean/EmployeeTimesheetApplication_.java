package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2016-09-26T10:06:10.551+0530")
@StaticMetamodel(EmployeeTimesheetApplication.class)
public class EmployeeTimesheetApplication_ extends BaseEntity_ {
	public static volatile SingularAttribute<EmployeeTimesheetApplication, Long> timesheetId;
	public static volatile SingularAttribute<EmployeeTimesheetApplication, Company> company;
	public static volatile SingularAttribute<EmployeeTimesheetApplication, Employee> employee;
	public static volatile SingularAttribute<EmployeeTimesheetApplication, TimesheetBatch> timesheetBatch;
	public static volatile SingularAttribute<EmployeeTimesheetApplication, TimesheetStatusMaster> timesheetStatusMaster;
	public static volatile SingularAttribute<EmployeeTimesheetApplication, String> remarks;
	public static volatile SetAttribute<EmployeeTimesheetApplication, TimesheetApplicationReviewer> timesheetApplicationReviewers;
	public static volatile SetAttribute<EmployeeTimesheetApplication, TimesheetApplicationWorkflow> timesheetApplicationWorkflows;
	public static volatile SetAttribute<EmployeeTimesheetApplication, LundinTimesheetDetail> lundinTimesheetDetails;
	public static volatile SetAttribute<EmployeeTimesheetApplication, LionEmployeeTimesheet> LionEmployeeTimesheets;
	public static volatile SetAttribute<EmployeeTimesheetApplication, LionEmployeeTimesheetApplicationDetail> lionEmployeeTimesheetApplicationDetails;
}
