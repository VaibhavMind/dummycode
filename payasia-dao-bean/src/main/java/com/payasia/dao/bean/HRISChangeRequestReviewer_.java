package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-09-27T15:58:32.839+0530")
@StaticMetamodel(HRISChangeRequestReviewer.class)
public class HRISChangeRequestReviewer_ extends CompanyBaseEntity_ {
	public static volatile SingularAttribute<HRISChangeRequestReviewer, Long> hrisChangeRequestReviewerId;
	public static volatile SingularAttribute<HRISChangeRequestReviewer, HRISChangeRequest> hrisChangeRequest;
	public static volatile SingularAttribute<HRISChangeRequestReviewer, Boolean> pending;
	public static volatile SingularAttribute<HRISChangeRequestReviewer, Employee> employeeReviewer;
	public static volatile SingularAttribute<HRISChangeRequestReviewer, WorkFlowRuleMaster> workFlowRuleMaster;
}
