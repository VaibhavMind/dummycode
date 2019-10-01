package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-06-07T14:15:06.947+0530")
@StaticMetamodel(ClaimApplicationReviewer.class)
public class ClaimApplicationReviewer_ extends CompanyBaseEntity_ {
	public static volatile SingularAttribute<ClaimApplicationReviewer, Long> claimApplicationReviewerId;
	public static volatile SingularAttribute<ClaimApplicationReviewer, Boolean> pending;
	public static volatile SingularAttribute<ClaimApplicationReviewer, ClaimApplication> claimApplication;
	public static volatile SingularAttribute<ClaimApplicationReviewer, Employee> employee;
	public static volatile SingularAttribute<ClaimApplicationReviewer, WorkFlowRuleMaster> workFlowRuleMaster;
}
