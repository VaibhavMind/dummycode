package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-06-07T14:50:01.607+0530")
@StaticMetamodel(ClaimTemplateWorkflow.class)
public class ClaimTemplateWorkflow_ extends CompanyBaseEntity_ {
	public static volatile SingularAttribute<ClaimTemplateWorkflow, Long> claimTemplateWorkflowId;
	public static volatile SingularAttribute<ClaimTemplateWorkflow, ClaimTemplate> claimTemplate;
	public static volatile SingularAttribute<ClaimTemplateWorkflow, WorkFlowRuleMaster> workFlowRuleMaster;
}
