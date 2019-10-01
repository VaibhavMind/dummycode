package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2016-11-09T18:17:42.348+0530")
@StaticMetamodel(EmployeeTimesheetReviewer.class)
public class EmployeeTimesheetReviewer_ extends CompanyBaseEntity_ {
	public static volatile SingularAttribute<EmployeeTimesheetReviewer, Long> employeeTimesheetReviewerId;
	public static volatile SingularAttribute<EmployeeTimesheetReviewer, Employee> employee;
	public static volatile SingularAttribute<EmployeeTimesheetReviewer, Employee> employeeReviewer;
	public static volatile SingularAttribute<EmployeeTimesheetReviewer, WorkFlowRuleMaster> workFlowRuleMaster;
	public static volatile SingularAttribute<EmployeeTimesheetReviewer, String> reviewerEmail;
}
