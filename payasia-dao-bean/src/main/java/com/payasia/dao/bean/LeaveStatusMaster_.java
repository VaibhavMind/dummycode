package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-02-11T20:50:40.069+0530")
@StaticMetamodel(LeaveStatusMaster.class)
public class LeaveStatusMaster_ {
	public static volatile SingularAttribute<LeaveStatusMaster, Long> leaveStatusID;
	public static volatile SingularAttribute<LeaveStatusMaster, String> leaveStatusDesc;
	public static volatile SingularAttribute<LeaveStatusMaster, String> leaveStatusName;
	public static volatile SetAttribute<LeaveStatusMaster, EmployeeLeaveSchemeTypeHistory> employeeLeaveSchemeTypeHistories;
	public static volatile SetAttribute<LeaveStatusMaster, LeaveApplication> leaveApplications;
	public static volatile SetAttribute<LeaveStatusMaster, LeaveApplicationWorkflow> leaveApplicationWorkflows;
}
