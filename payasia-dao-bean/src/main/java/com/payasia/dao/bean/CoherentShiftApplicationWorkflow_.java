package com.payasia.dao.bean;

import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2016-11-29T11:49:50.031+0530")
@StaticMetamodel(CoherentShiftApplicationWorkflow.class)
public class CoherentShiftApplicationWorkflow_ extends CompanyUpdatedBaseEntity_ {
	public static volatile SingularAttribute<CoherentShiftApplicationWorkflow, Long> shiftApplicationWorkflowID;
	public static volatile SingularAttribute<CoherentShiftApplicationWorkflow, CoherentShiftApplication> coherentShiftApplication;
	public static volatile SingularAttribute<CoherentShiftApplicationWorkflow, TimesheetStatusMaster> timesheetStatusMaster;
	public static volatile SingularAttribute<CoherentShiftApplicationWorkflow, Employee> createdBy;
	public static volatile SingularAttribute<CoherentShiftApplicationWorkflow, Timestamp> createdDate;
	public static volatile SingularAttribute<CoherentShiftApplicationWorkflow, String> remarks;
}
