package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-09-27T15:58:32.848+0530")
@StaticMetamodel(HRISChangeWorkflow.class)
public class HRISChangeWorkflow_ extends BaseEntity_ {
	public static volatile SingularAttribute<HRISChangeWorkflow, Long> hrisChangeWorkflowId;
	public static volatile SingularAttribute<HRISChangeWorkflow, Company> company;
	public static volatile SingularAttribute<HRISChangeWorkflow, WorkFlowRuleMaster> workFlowRuleMaster;
}
