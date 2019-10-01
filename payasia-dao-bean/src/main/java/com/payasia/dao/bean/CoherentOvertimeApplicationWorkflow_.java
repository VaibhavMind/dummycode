package com.payasia.dao.bean;

import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2016-11-22T18:37:53.890+0530")
@StaticMetamodel(CoherentOvertimeApplicationWorkflow.class)
public class CoherentOvertimeApplicationWorkflow_ extends CompanyUpdatedBaseEntity_ {
	public static volatile SingularAttribute<CoherentOvertimeApplicationWorkflow, Long> overtimeApplicationWorkflowID;
	public static volatile SingularAttribute<CoherentOvertimeApplicationWorkflow, CoherentOvertimeApplication> coherentOvertimeApplication;
	public static volatile SingularAttribute<CoherentOvertimeApplicationWorkflow, Timestamp> createdDate;
	public static volatile SingularAttribute<CoherentOvertimeApplicationWorkflow, Employee> createdBy;
	public static volatile SingularAttribute<CoherentOvertimeApplicationWorkflow, TimesheetStatusMaster> timesheetStatusMaster;
	public static volatile SingularAttribute<CoherentOvertimeApplicationWorkflow, String> Remarks;
}
