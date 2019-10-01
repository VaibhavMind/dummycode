package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2016-11-22T18:19:35.587+0530")
@StaticMetamodel(CoherentShiftApplicationReviewer.class)
public class CoherentShiftApplicationReviewer_ extends CompanyBaseEntity_ {
	public static volatile SingularAttribute<CoherentShiftApplicationReviewer, Long> shiftApplicationReviewerID;
	public static volatile SingularAttribute<CoherentShiftApplicationReviewer, CoherentShiftApplication> coherentShiftApplication;
	public static volatile SingularAttribute<CoherentShiftApplicationReviewer, WorkFlowRuleMaster> workFlowRuleMaster;
	public static volatile SingularAttribute<CoherentShiftApplicationReviewer, Employee> employeeReviewer;
	public static volatile SingularAttribute<CoherentShiftApplicationReviewer, Boolean> pending;
}
