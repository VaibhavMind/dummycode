package com.payasia.dao.bean;

import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-04-09T18:41:46.053+0530")
@StaticMetamodel(LeaveApplicationWorkflow.class)
public class LeaveApplicationWorkflow_ extends CompanyUpdatedBaseEntity_ {
	public static volatile SingularAttribute<LeaveApplicationWorkflow, Long> leaveApplicationWorkflowID;
	public static volatile SingularAttribute<LeaveApplicationWorkflow, Timestamp> createdDate;
	public static volatile SingularAttribute<LeaveApplicationWorkflow, String> emailCC;
	public static volatile SingularAttribute<LeaveApplicationWorkflow, Timestamp> endDate;
	public static volatile SingularAttribute<LeaveApplicationWorkflow, String> forwardTo;
	public static volatile SingularAttribute<LeaveApplicationWorkflow, String> remarks;
	public static volatile SingularAttribute<LeaveApplicationWorkflow, Timestamp> startDate;
	public static volatile SingularAttribute<LeaveApplicationWorkflow, Float> totalDays;
	public static volatile SingularAttribute<LeaveApplicationWorkflow, Employee> employee;
	public static volatile SingularAttribute<LeaveApplicationWorkflow, LeaveApplication> leaveApplication;
	public static volatile SingularAttribute<LeaveApplicationWorkflow, LeaveStatusMaster> leaveStatusMaster;
	public static volatile SingularAttribute<LeaveApplicationWorkflow, LeaveSessionMaster> startSessionMaster;
	public static volatile SingularAttribute<LeaveApplicationWorkflow, LeaveSessionMaster> endSessionMaster;
}
