package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-04-09T18:17:02.125+0530")
@StaticMetamodel(LeaveSchemeTypeWorkflow.class)
public class LeaveSchemeTypeWorkflow_ extends CompanyBaseEntity_ {
	public static volatile SingularAttribute<LeaveSchemeTypeWorkflow, Long> leaveSchemeTypeWorkflowId;
	public static volatile SingularAttribute<LeaveSchemeTypeWorkflow, LeaveSchemeType> leaveSchemeType;
	public static volatile SingularAttribute<LeaveSchemeTypeWorkflow, WorkFlowRuleMaster> workFlowRuleMaster;
}
