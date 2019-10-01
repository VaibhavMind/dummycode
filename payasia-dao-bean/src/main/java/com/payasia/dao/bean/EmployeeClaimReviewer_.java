package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-06-07T14:48:53.303+0530")
@StaticMetamodel(EmployeeClaimReviewer.class)
public class EmployeeClaimReviewer_ extends CompanyBaseEntity_ {
	public static volatile SingularAttribute<EmployeeClaimReviewer, Long> employeeClaimReviewerId;
	public static volatile SingularAttribute<EmployeeClaimReviewer, EmployeeClaimTemplate> employeeClaimTemplate;
	public static volatile SingularAttribute<EmployeeClaimReviewer, Employee> employee1;
	public static volatile SingularAttribute<EmployeeClaimReviewer, Employee> employee2;
	public static volatile SingularAttribute<EmployeeClaimReviewer, WorkFlowRuleMaster> workFlowRuleMaster;
}
