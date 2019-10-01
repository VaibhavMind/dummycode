package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2016-11-22T15:50:10.462+0530")
@StaticMetamodel(CoherentOvertimeApplicationReviewer.class)
public class CoherentOvertimeApplicationReviewer_ extends CompanyBaseEntity_ {
	public static volatile SingularAttribute<CoherentOvertimeApplicationReviewer, Long> overtimeApplciationReviewerID;
	public static volatile SingularAttribute<CoherentOvertimeApplicationReviewer, CoherentOvertimeApplication> coherentOvertimeApplication;
	public static volatile SingularAttribute<CoherentOvertimeApplicationReviewer, WorkFlowRuleMaster> workFlowRuleMaster;
	public static volatile SingularAttribute<CoherentOvertimeApplicationReviewer, Employee> employeeReviewer;
	public static volatile SingularAttribute<CoherentOvertimeApplicationReviewer, Boolean> pending;
}
