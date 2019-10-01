package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2016-09-26T10:06:50.067+0530")
@StaticMetamodel(TimesheetApplicationReviewer.class)
public class TimesheetApplicationReviewer_ extends CompanyBaseEntity_ {
	public static volatile SingularAttribute<TimesheetApplicationReviewer, Long> timesheetReviewerId;
	public static volatile SingularAttribute<TimesheetApplicationReviewer, Boolean> pending;
	public static volatile SingularAttribute<TimesheetApplicationReviewer, EmployeeTimesheetApplication> employeeTimesheetApplication;
	public static volatile SingularAttribute<TimesheetApplicationReviewer, Employee> employee;
	public static volatile SingularAttribute<TimesheetApplicationReviewer, WorkFlowRuleMaster> workFlowRuleMaster;
}
