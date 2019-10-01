package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-04-09T17:56:38.648+0530")
@StaticMetamodel(LeaveSchemeWorkflow.class)
public class LeaveSchemeWorkflow_ extends CompanyBaseEntity_ {
	public static volatile SingularAttribute<LeaveSchemeWorkflow, Long> leaveSchemeWorkflowId;
	public static volatile SingularAttribute<LeaveSchemeWorkflow, LeaveScheme> leaveScheme;
	public static volatile SingularAttribute<LeaveSchemeWorkflow, WorkFlowRuleMaster> workFlowRuleMaster;
}
