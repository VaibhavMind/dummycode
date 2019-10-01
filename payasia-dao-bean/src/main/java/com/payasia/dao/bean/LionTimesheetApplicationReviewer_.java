package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2016-11-21T15:21:53.741+0530")
@StaticMetamodel(LionTimesheetApplicationReviewer.class)
public class LionTimesheetApplicationReviewer_ extends CompanyBaseEntity_ {
	public static volatile SingularAttribute<LionTimesheetApplicationReviewer, Long> employeeTimesheetReviewerId;
	public static volatile SingularAttribute<LionTimesheetApplicationReviewer, LionEmployeeTimesheetApplicationDetail> lionEmployeeTimesheetApplicationDetail;
	public static volatile SingularAttribute<LionTimesheetApplicationReviewer, Employee> employeeReviewer;
	public static volatile SingularAttribute<LionTimesheetApplicationReviewer, WorkFlowRuleMaster> workFlowRuleMaster;
	public static volatile SingularAttribute<LionTimesheetApplicationReviewer, String> reviewerEmail;
	public static volatile SingularAttribute<LionTimesheetApplicationReviewer, Boolean> pending;
}
