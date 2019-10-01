package com.payasia.dao.bean;

import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-06-14T18:06:46.080+0530")
@StaticMetamodel(WorkflowDelegate.class)
public class WorkflowDelegate_ extends BaseEntity_ {
	public static volatile SingularAttribute<WorkflowDelegate, Long> workflowDelegateId;
	public static volatile SingularAttribute<WorkflowDelegate, Timestamp> endDate;
	public static volatile SingularAttribute<WorkflowDelegate, Timestamp> startDate;
	public static volatile SingularAttribute<WorkflowDelegate, AppCodeMaster> workflowType;
	public static volatile SingularAttribute<WorkflowDelegate, Company> company;
	public static volatile SingularAttribute<WorkflowDelegate, Employee> employee1;
	public static volatile SingularAttribute<WorkflowDelegate, Employee> employee2;
}
