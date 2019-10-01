package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-09-27T16:08:20.481+0530")
@StaticMetamodel(EmployeeHRISReviewer.class)
public class EmployeeHRISReviewer_ extends CompanyBaseEntity_ {
	public static volatile SingularAttribute<EmployeeHRISReviewer, Long> employeeHRISReviewerId;
	public static volatile SingularAttribute<EmployeeHRISReviewer, Employee> employee;
	public static volatile SingularAttribute<EmployeeHRISReviewer, Employee> employeeReviewer;
	public static volatile SingularAttribute<EmployeeHRISReviewer, WorkFlowRuleMaster> workFlowRuleMaster;
}
