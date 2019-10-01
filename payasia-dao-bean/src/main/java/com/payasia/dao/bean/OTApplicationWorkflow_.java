package com.payasia.dao.bean;

import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2012-07-12T19:14:34.743+0530")
@StaticMetamodel(OTApplicationWorkflow.class)
public class OTApplicationWorkflow_ {
	public static volatile SingularAttribute<OTApplicationWorkflow, Long> otApplicationWorkflowId;
	public static volatile SingularAttribute<OTApplicationWorkflow, Timestamp> createdDate;
	public static volatile SingularAttribute<OTApplicationWorkflow, String> emailCC;
	public static volatile SingularAttribute<OTApplicationWorkflow, String> forwardTo;
	public static volatile SingularAttribute<OTApplicationWorkflow, String> remarks;
	public static volatile SetAttribute<OTApplicationWorkflow, OTApplicationItemWorkflow> otApplicationItemWorkflows;
	public static volatile SingularAttribute<OTApplicationWorkflow, Employee> employee;
	public static volatile SingularAttribute<OTApplicationWorkflow, OTApplication> otApplication;
	public static volatile SingularAttribute<OTApplicationWorkflow, OTStatusMaster> otStatusMaster;
}
