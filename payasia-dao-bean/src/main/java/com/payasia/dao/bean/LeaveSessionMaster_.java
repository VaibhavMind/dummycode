package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2015-05-01T11:15:18.853+0530")
@StaticMetamodel(LeaveSessionMaster.class)
public class LeaveSessionMaster_ {
	public static volatile SingularAttribute<LeaveSessionMaster, Long> leaveSessionId;
	public static volatile SingularAttribute<LeaveSessionMaster, String> session;
	public static volatile SingularAttribute<LeaveSessionMaster, String> sessionDesc;
	public static volatile SingularAttribute<LeaveSessionMaster, String> sessionLabelKey;
	public static volatile SetAttribute<LeaveSessionMaster, LeaveApplication> leaveApplications1;
	public static volatile SetAttribute<LeaveSessionMaster, LeaveApplication> leaveApplications2;
	public static volatile SetAttribute<LeaveSessionMaster, LeaveApplicationWorkflow> startLeaveApplicationWorkflows;
	public static volatile SetAttribute<LeaveSessionMaster, LeaveApplicationWorkflow> endLeaveApplicationWorkflows;
	public static volatile SetAttribute<LeaveSessionMaster, EmployeeLeaveSchemeTypeHistory> startEmployeeLeaveSchemeTypeHistories;
	public static volatile SetAttribute<LeaveSessionMaster, EmployeeLeaveSchemeTypeHistory> endEmployeeLeaveSchemeTypeHistories;
}
