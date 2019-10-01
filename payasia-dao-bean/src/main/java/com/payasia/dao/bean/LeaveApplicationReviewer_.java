package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-04-09T18:37:16.264+0530")
@StaticMetamodel(LeaveApplicationReviewer.class)
public class LeaveApplicationReviewer_ extends CompanyBaseEntity_ {
	public static volatile SingularAttribute<LeaveApplicationReviewer, Long> leaveApplicationReviewerId;
	public static volatile SingularAttribute<LeaveApplicationReviewer, Boolean> pending;
	public static volatile SingularAttribute<LeaveApplicationReviewer, Employee> employee;
	public static volatile SingularAttribute<LeaveApplicationReviewer, LeaveApplication> leaveApplication;
	public static volatile SingularAttribute<LeaveApplicationReviewer, WorkFlowRuleMaster> workFlowRuleMaster;
}
