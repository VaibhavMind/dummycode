package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2016-09-22T14:33:06.568+0530")
@StaticMetamodel(TimesheetWorkflow.class)
public class TimesheetWorkflow_ extends BaseEntity_ {
	public static volatile SingularAttribute<TimesheetWorkflow, Long> workflowId;
	public static volatile SingularAttribute<TimesheetWorkflow, Company> company;
	public static volatile SingularAttribute<TimesheetWorkflow, WorkFlowRuleMaster> workFlowRuleMaster;
}
