package com.payasia.dao.bean;

import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-09-27T15:58:32.845+0530")
@StaticMetamodel(HRISChangeRequestWorkflow.class)
public class HRISChangeRequestWorkflow_ extends CompanyUpdatedBaseEntity_ {
	public static volatile SingularAttribute<HRISChangeRequestWorkflow, Long> hrisChangeRequestWorkflowId;
	public static volatile SingularAttribute<HRISChangeRequestWorkflow, HRISChangeRequest> hrisChangeRequest;
	public static volatile SingularAttribute<HRISChangeRequestWorkflow, String> newValue;
	public static volatile SingularAttribute<HRISChangeRequestWorkflow, String> remarks;
	public static volatile SingularAttribute<HRISChangeRequestWorkflow, String> forwardTo;
	public static volatile SingularAttribute<HRISChangeRequestWorkflow, String> emailCC;
	public static volatile SingularAttribute<HRISChangeRequestWorkflow, HRISStatusMaster> hrisStatusMaster;
	public static volatile SingularAttribute<HRISChangeRequestWorkflow, Employee> employee;
	public static volatile SingularAttribute<HRISChangeRequestWorkflow, Timestamp> createdDate;
}
