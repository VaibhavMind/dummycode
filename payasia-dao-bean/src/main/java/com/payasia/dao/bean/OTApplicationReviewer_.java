package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2012-07-12T19:14:34.739+0530")
@StaticMetamodel(OTApplicationReviewer.class)
public class OTApplicationReviewer_ {
	public static volatile SingularAttribute<OTApplicationReviewer, Long> otApplicationReviewerId;
	public static volatile SingularAttribute<OTApplicationReviewer, Boolean> pending;
	public static volatile SingularAttribute<OTApplicationReviewer, Employee> employee;
	public static volatile SingularAttribute<OTApplicationReviewer, OTApplication> otApplication;
	public static volatile SingularAttribute<OTApplicationReviewer, WorkFlowRuleMaster> workFlowRuleMaster;
}
