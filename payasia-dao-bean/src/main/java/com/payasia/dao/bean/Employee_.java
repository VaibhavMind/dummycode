package com.payasia.dao.bean;

import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2016-11-12T16:46:41.202+0530")
@StaticMetamodel(Employee.class)
public class Employee_ extends BaseEntity_ {
	public static volatile SingularAttribute<Employee, Long> employeeId;
	public static volatile SingularAttribute<Employee, Timestamp> confirmationDate;
	public static volatile SingularAttribute<Employee, String> email;
	public static volatile SingularAttribute<Employee, String> employeeNumber;
	public static volatile SingularAttribute<Employee, String> firstName;
	public static volatile SingularAttribute<Employee, Timestamp> hireDate;
	public static volatile SingularAttribute<Employee, String> lastName;
	public static volatile SingularAttribute<Employee, String> middleName;
	public static volatile SingularAttribute<Employee, Timestamp> originalHireDate;
	public static volatile SingularAttribute<Employee, Timestamp> resignationDate;
	public static volatile SingularAttribute<Employee, Boolean> status;
	public static volatile SetAttribute<Employee, ClaimApplication> claimApplications;
	public static volatile SetAttribute<Employee, ClaimApplicationReviewer> claimApplicationReviewers;
	public static volatile SetAttribute<Employee, ClaimApplicationWorkflow> claimApplicationWorkflows;
	public static volatile SetAttribute<Employee, EmpNumberHistory> empNumberHistories1;
	public static volatile SetAttribute<Employee, EmpNumberHistory> empNumberHistories2;
	public static volatile SingularAttribute<Employee, Company> company;
	public static volatile SetAttribute<Employee, EmployeeClaimReviewer> employeeClaimReviewers1;
	public static volatile SetAttribute<Employee, EmployeeClaimReviewer> employeeClaimReviewers2;
	public static volatile SetAttribute<Employee, EmployeeDocument> employeeDocuments;
	public static volatile SetAttribute<Employee, EmployeeLeaveReviewer> employeeLeaveReviewers1;
	public static volatile SetAttribute<Employee, EmployeeLeaveReviewer> employeeLeaveReviewers2;
	public static volatile SetAttribute<Employee, EmployeeLoginHistory> employeeLoginHistories;
	public static volatile SetAttribute<Employee, EmployeeOTReviewer> employeeOtReviewers1;
	public static volatile SetAttribute<Employee, EmployeeOTReviewer> employeeOtReviewers2;
	public static volatile SetAttribute<Employee, EmployeePasswordChangeHistory> employeePasswordChangeHistories;
	public static volatile SetAttribute<Employee, EmployeePhoto> employeePhotos;
	public static volatile SetAttribute<Employee, EmployeeRoleMapping> employeeRoleMappings;
	public static volatile SetAttribute<Employee, LeaveApplication> leaveApplications;
	public static volatile SetAttribute<Employee, LeaveApplicationReviewer> leaveApplicationReviewers;
	public static volatile SetAttribute<Employee, LeaveApplicationWorkflow> leaveApplicationWorkflows;
	public static volatile SetAttribute<Employee, OTApplication> otApplications;
	public static volatile SetAttribute<Employee, OTApplicationReviewer> otApplicationReviewers;
	public static volatile SetAttribute<Employee, OTApplicationWorkflow> otApplicationWorkflows;
	public static volatile SetAttribute<Employee, PayDataCollection> payDataCollections;
	public static volatile SetAttribute<Employee, Payslip> payslips;
	public static volatile SetAttribute<Employee, WorkflowDelegate> workflowDelegates1;
	public static volatile SetAttribute<Employee, WorkflowDelegate> workflowDelegates2;
	public static volatile SetAttribute<Employee, EmployeeLeaveScheme> employeeLeaveSchemes;
	public static volatile SetAttribute<Employee, EmployeeLoginDetail> employeeLoginDetails;
	public static volatile SetAttribute<Employee, EmployeeCalendarConfig> employeeCalendarConfigs;
	public static volatile SetAttribute<Employee, LeaveGrantBatchEmployeeDetail> leaveGrantBatchEmployeeDetails;
	public static volatile SetAttribute<Employee, LeaveYearEndEmployeeDetail> leaveYearEndEmployeeDetails;
	public static volatile SetAttribute<Employee, EmployeeHolidayCalendar> employeeHolidayCalendars;
	public static volatile SetAttribute<Employee, EmployeeClaimTemplate> employeeClaimTemplates;
	public static volatile SingularAttribute<Employee, String> employmentStatus;
	public static volatile SetAttribute<Employee, EmployeeActivationCode> employeeActivationCodes;
	public static volatile SetAttribute<Employee, EmployeeRoleSectionMapping> employeeRoleSectionMappings;
	public static volatile SetAttribute<Employee, NotificationAlert> notificationAlerts;
	public static volatile SetAttribute<Employee, EmployeeTimesheetReviewer> employeeTimesheetReviewer1;
	public static volatile SetAttribute<Employee, EmployeeTimesheetReviewer> employeeTimesheetReviewer2;
}
