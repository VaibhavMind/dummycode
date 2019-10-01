package com.payasia.dao.bean;

import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2018-07-30T12:55:30.766+0530")
@StaticMetamodel(LeaveApplication.class)
public class LeaveApplication_ extends BaseEntity_ {
	public static volatile SingularAttribute<LeaveApplication, Long> leaveApplicationId;
	public static volatile SingularAttribute<LeaveApplication, String> applyTo;
	public static volatile SingularAttribute<LeaveApplication, String> emailCC;
	public static volatile SingularAttribute<LeaveApplication, Timestamp> endDate;
	public static volatile SingularAttribute<LeaveApplication, String> reason;
	public static volatile SingularAttribute<LeaveApplication, Timestamp> startDate;
	public static volatile SingularAttribute<LeaveApplication, Float> totalDays;
	public static volatile SingularAttribute<LeaveApplication, Float> excessDays;
	public static volatile SingularAttribute<LeaveApplication, Boolean> preApprovalRequest;
	public static volatile SingularAttribute<LeaveApplication, Boolean> leaveExtension;
	public static volatile SetAttribute<LeaveApplication, EmployeeLeaveSchemeTypeHistory> employeeLeaveSchemeTypeHistories;
	public static volatile SetAttribute<LeaveApplication, LeaveApplicationCustomField> leaveApplicationCustomFields;
	public static volatile SetAttribute<LeaveApplication, LeaveApplicationExtensionDetails> leaveApplicationExtensionDetails;
	public static volatile SingularAttribute<LeaveApplication, Company> company;
	public static volatile SingularAttribute<LeaveApplication, Employee> employee;
	public static volatile SingularAttribute<LeaveApplication, EmployeeLeaveSchemeType> employeeLeaveSchemeType;
	public static volatile SingularAttribute<LeaveApplication, LeaveStatusMaster> leaveStatusMaster;
	public static volatile SingularAttribute<LeaveApplication, LeaveSessionMaster> leaveSessionMaster1;
	public static volatile SingularAttribute<LeaveApplication, LeaveSessionMaster> leaveSessionMaster2;
	public static volatile SetAttribute<LeaveApplication, LeaveApplicationAttachment> leaveApplicationAttachments;
	public static volatile SetAttribute<LeaveApplication, LeaveApplicationReviewer> leaveApplicationReviewers;
	public static volatile SetAttribute<LeaveApplication, LeaveApplicationWorkflow> leaveApplicationWorkflows;
	public static volatile SingularAttribute<LeaveApplication, LeaveApplication> leaveCancelApplication;
}
