package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-04-09T18:14:01.442+0530")
@StaticMetamodel(EmployeeLeaveReviewer.class)
public class EmployeeLeaveReviewer_ extends CompanyBaseEntity_ {
	public static volatile SingularAttribute<EmployeeLeaveReviewer, Long> employeeLeaveReviewerID;
	public static volatile SingularAttribute<EmployeeLeaveReviewer, Employee> employee1;
	public static volatile SingularAttribute<EmployeeLeaveReviewer, Employee> employee2;
	public static volatile SingularAttribute<EmployeeLeaveReviewer, EmployeeLeaveScheme> employeeLeaveScheme;
	public static volatile SingularAttribute<EmployeeLeaveReviewer, EmployeeLeaveSchemeType> employeeLeaveSchemeType;
	public static volatile SingularAttribute<EmployeeLeaveReviewer, WorkFlowRuleMaster> workFlowRuleMaster;
}
