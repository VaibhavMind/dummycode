package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-06-07T14:46:41.630+0530")
@StaticMetamodel(ClaimTemplateItemWorkflow.class)
public class ClaimTemplateItemWorkflow_ extends CompanyBaseEntity_ {
	public static volatile SingularAttribute<ClaimTemplateItemWorkflow, Long> claimTemplateItemWorkflowId;
	public static volatile SingularAttribute<ClaimTemplateItemWorkflow, ClaimTemplateItem> claimTemplateItem;
	public static volatile SingularAttribute<ClaimTemplateItemWorkflow, WorkFlowRuleMaster> workFlowRuleMaster;
}
