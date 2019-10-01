package com.payasia.dao.bean;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * The persistent class for the Employee database table.
 * 
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "Employee")
public class Employee extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Employee_ID")
	private long employeeId;

	@Column(name = "Confirmation_Date")
	private Timestamp confirmationDate;

	@Column(name = "Email")
	private String email;

	@Column(name = "Employee_Number")
	private String employeeNumber;

	@Column(name = "First_Name")
	private String firstName;

	@Column(name = "Hire_Date")
	private Timestamp hireDate;

	@Column(name = "Last_Name")
	private String lastName;

	@Column(name = "Middle_Name")
	private String middleName;

	@Column(name = "Original_Hire_Date")
	private Timestamp originalHireDate;

	@Column(name = "Resignation_Date")
	private Timestamp resignationDate;

	@Column(name = "Status")
	private boolean status = true;

	@OneToMany(mappedBy = "employee")
	private Set<ClaimApplication> claimApplications;

	@OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
	private Set<ClaimApplicationReviewer> claimApplicationReviewers;

	@OneToMany(mappedBy = "employee")
	private Set<ClaimApplicationWorkflow> claimApplicationWorkflows;

	@OneToMany(mappedBy = "employee1")
	private Set<EmpNumberHistory> empNumberHistories1;

	@OneToMany(mappedBy = "employee2")
	private Set<EmpNumberHistory> empNumberHistories2;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "Company_ID")
	private Company company;

	@OneToMany(mappedBy = "employee1")
	private Set<EmployeeClaimReviewer> employeeClaimReviewers1;

	@OneToMany(mappedBy = "employee2")
	private Set<EmployeeClaimReviewer> employeeClaimReviewers2;

	@OneToMany(mappedBy = "employee")
	private Set<EmployeeDocument> employeeDocuments;

	@OneToMany(mappedBy = "employee1")
	private Set<EmployeeLeaveReviewer> employeeLeaveReviewers1;

	@OneToMany(mappedBy = "employee2")
	private Set<EmployeeLeaveReviewer> employeeLeaveReviewers2;

	@OneToMany(mappedBy = "employee")
	private Set<EmployeeLoginHistory> employeeLoginHistories;

	@OneToMany(mappedBy = "employee1")
	private Set<EmployeeOTReviewer> employeeOtReviewers1;

	@OneToMany(mappedBy = "employee2")
	private Set<EmployeeOTReviewer> employeeOtReviewers2;

	@OneToMany(mappedBy = "employee")
	private Set<EmployeePasswordChangeHistory> employeePasswordChangeHistories;

	@OneToMany(mappedBy = "employee", cascade = { CascadeType.ALL })
	private Set<EmployeePhoto> employeePhotos;

	@OneToMany(mappedBy = "employee", cascade = CascadeType.REMOVE)
	private Set<EmployeeRoleMapping> employeeRoleMappings;

	@OneToMany(mappedBy = "employee")
	private Set<LeaveApplication> leaveApplications;

	@OneToMany(mappedBy = "employee")
	private Set<LeaveApplicationReviewer> leaveApplicationReviewers;

	@OneToMany(mappedBy = "employee")
	private Set<LeaveApplicationWorkflow> leaveApplicationWorkflows;

	@OneToMany(mappedBy = "employee")
	private Set<OTApplication> otApplications;

	@OneToMany(mappedBy = "employee")
	private Set<OTApplicationReviewer> otApplicationReviewers;

	@OneToMany(mappedBy = "employee")
	private Set<OTApplicationWorkflow> otApplicationWorkflows;

	@OneToMany(mappedBy = "employee")
	private Set<PayDataCollection> payDataCollections;

	@OneToMany(mappedBy = "employee")
	private Set<Payslip> payslips;

	@OneToMany(mappedBy = "employee1")
	private Set<WorkflowDelegate> workflowDelegates1;

	@OneToMany(mappedBy = "employee2")
	private Set<WorkflowDelegate> workflowDelegates2;

	@OneToMany(mappedBy = "employee")
	private Set<EmployeeLeaveScheme> employeeLeaveSchemes;

	@OneToMany(mappedBy = "employee", cascade = CascadeType.REMOVE)
	private Set<EmployeeLoginDetail> employeeLoginDetails;

	@OneToMany(mappedBy = "employee")
	private Set<EmployeeCalendarConfig> employeeCalendarConfigs;

	@OneToMany(mappedBy = "employee")
	private Set<LeaveGrantBatchEmployeeDetail> leaveGrantBatchEmployeeDetails;

	@OneToMany(mappedBy = "employee")
	private Set<LeaveYearEndEmployeeDetail> leaveYearEndEmployeeDetails;

	@OneToMany(mappedBy = "employee")
	private Set<EmployeeHolidayCalendar> employeeHolidayCalendars;

	@OneToMany(mappedBy = "employee")
	private Set<EmployeeClaimTemplate> employeeClaimTemplates;

	@Column(name = "Employment_Status")
	private String employmentStatus;

	@OneToMany(mappedBy = "employee")
	private Set<EmployeeActivationCode> employeeActivationCodes;

	@OneToMany(mappedBy = "employee", cascade = CascadeType.REMOVE)
	private Set<EmployeeRoleSectionMapping> employeeRoleSectionMappings;

	@OneToMany(mappedBy = "employee")
	private Set<NotificationAlert> notificationAlerts;

	@OneToMany(mappedBy = "employee")
	private Set<EmployeeTimesheetReviewer> employeeTimesheetReviewer1;

	@OneToMany(mappedBy = "employeeReviewer")
	private Set<EmployeeTimesheetReviewer> employeeTimesheetReviewer2;

	public Employee() {
	}

	public long getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(long employeeId) {
		this.employeeId = employeeId;
	}

	public Timestamp getConfirmationDate() {
		return confirmationDate;
	}

	public void setConfirmationDate(Timestamp confirmationDate) {
		this.confirmationDate = confirmationDate;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmployeeNumber() {
		return employeeNumber;
	}

	public void setEmployeeNumber(String employeeNumber) {
		this.employeeNumber = employeeNumber;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public Timestamp getHireDate() {
		return hireDate;
	}

	public void setHireDate(Timestamp hireDate) {
		this.hireDate = hireDate;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public Timestamp getOriginalHireDate() {
		return originalHireDate;
	}

	public void setOriginalHireDate(Timestamp originalHireDate) {
		this.originalHireDate = originalHireDate;
	}

	public Timestamp getResignationDate() {
		return resignationDate;
	}

	public void setResignationDate(Timestamp resignationDate) {
		this.resignationDate = resignationDate;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public Set<ClaimApplication> getClaimApplications() {
		return claimApplications;
	}

	public void setClaimApplications(Set<ClaimApplication> claimApplications) {
		this.claimApplications = claimApplications;
	}

	public Set<ClaimApplicationReviewer> getClaimApplicationReviewers() {
		return claimApplicationReviewers;
	}

	public void setClaimApplicationReviewers(
			Set<ClaimApplicationReviewer> claimApplicationReviewers) {
		this.claimApplicationReviewers = claimApplicationReviewers;
	}

	public Set<ClaimApplicationWorkflow> getClaimApplicationWorkflows() {
		return claimApplicationWorkflows;
	}

	public void setClaimApplicationWorkflows(
			Set<ClaimApplicationWorkflow> claimApplicationWorkflows) {
		this.claimApplicationWorkflows = claimApplicationWorkflows;
	}

	public Set<EmpNumberHistory> getEmpNumberHistories1() {
		return empNumberHistories1;
	}

	public void setEmpNumberHistories1(Set<EmpNumberHistory> empNumberHistories1) {
		this.empNumberHistories1 = empNumberHistories1;
	}

	public Set<EmpNumberHistory> getEmpNumberHistories2() {
		return empNumberHistories2;
	}

	public void setEmpNumberHistories2(Set<EmpNumberHistory> empNumberHistories2) {
		this.empNumberHistories2 = empNumberHistories2;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public Set<EmployeeClaimReviewer> getEmployeeClaimReviewers1() {
		return employeeClaimReviewers1;
	}

	public void setEmployeeClaimReviewers1(
			Set<EmployeeClaimReviewer> employeeClaimReviewers1) {
		this.employeeClaimReviewers1 = employeeClaimReviewers1;
	}

	public Set<EmployeeClaimReviewer> getEmployeeClaimReviewers2() {
		return employeeClaimReviewers2;
	}

	public void setEmployeeClaimReviewers2(
			Set<EmployeeClaimReviewer> employeeClaimReviewers2) {
		this.employeeClaimReviewers2 = employeeClaimReviewers2;
	}

	public Set<EmployeeDocument> getEmployeeDocuments() {
		return employeeDocuments;
	}

	public void setEmployeeDocuments(Set<EmployeeDocument> employeeDocuments) {
		this.employeeDocuments = employeeDocuments;
	}

	public Set<EmployeeLeaveReviewer> getEmployeeLeaveReviewers1() {
		return employeeLeaveReviewers1;
	}

	public void setEmployeeLeaveReviewers1(
			Set<EmployeeLeaveReviewer> employeeLeaveReviewers1) {
		this.employeeLeaveReviewers1 = employeeLeaveReviewers1;
	}

	public Set<EmployeeLeaveReviewer> getEmployeeLeaveReviewers2() {
		return employeeLeaveReviewers2;
	}

	public void setEmployeeLeaveReviewers2(
			Set<EmployeeLeaveReviewer> employeeLeaveReviewers2) {
		this.employeeLeaveReviewers2 = employeeLeaveReviewers2;
	}

	public Set<EmployeeLoginHistory> getEmployeeLoginHistories() {
		return employeeLoginHistories;
	}

	public void setEmployeeLoginHistories(
			Set<EmployeeLoginHistory> employeeLoginHistories) {
		this.employeeLoginHistories = employeeLoginHistories;
	}

	public Set<EmployeeOTReviewer> getEmployeeOtReviewers1() {
		return employeeOtReviewers1;
	}

	public void setEmployeeOtReviewers1(
			Set<EmployeeOTReviewer> employeeOtReviewers1) {
		this.employeeOtReviewers1 = employeeOtReviewers1;
	}

	public Set<EmployeeOTReviewer> getEmployeeOtReviewers2() {
		return employeeOtReviewers2;
	}

	public void setEmployeeOtReviewers2(
			Set<EmployeeOTReviewer> employeeOtReviewers2) {
		this.employeeOtReviewers2 = employeeOtReviewers2;
	}

	public Set<EmployeePasswordChangeHistory> getEmployeePasswordChangeHistories() {
		return employeePasswordChangeHistories;
	}

	public void setEmployeePasswordChangeHistories(
			Set<EmployeePasswordChangeHistory> employeePasswordChangeHistories) {
		this.employeePasswordChangeHistories = employeePasswordChangeHistories;
	}

	public EmployeePhoto getEmployeePhoto() {
		Iterator<EmployeePhoto> itr = employeePhotos.iterator();
		if (itr.hasNext()) {
			return itr.next();
		}
		return null;
	}

	public Set<EmployeeRoleMapping> getEmployeeRoleMappings() {
		return employeeRoleMappings;
	}

	public Set<EmployeePhoto> getEmployeePhotos() {
		return employeePhotos;
	}

	public void setEmployeePhotos(Set<EmployeePhoto> employeePhotos) {
		this.employeePhotos = employeePhotos;
	}

	public void setEmployeeRoleMappings(
			Set<EmployeeRoleMapping> employeeRoleMappings) {
		this.employeeRoleMappings = employeeRoleMappings;
	}

	public Set<LeaveApplication> getLeaveApplications() {
		return leaveApplications;
	}

	public void setLeaveApplications(Set<LeaveApplication> leaveApplications) {
		this.leaveApplications = leaveApplications;
	}

	public Set<LeaveApplicationReviewer> getLeaveApplicationReviewers() {
		return leaveApplicationReviewers;
	}

	public void setLeaveApplicationReviewers(
			Set<LeaveApplicationReviewer> leaveApplicationReviewers) {
		this.leaveApplicationReviewers = leaveApplicationReviewers;
	}

	public Set<LeaveApplicationWorkflow> getLeaveApplicationWorkflows() {
		return leaveApplicationWorkflows;
	}

	public void setLeaveApplicationWorkflows(
			Set<LeaveApplicationWorkflow> leaveApplicationWorkflows) {
		this.leaveApplicationWorkflows = leaveApplicationWorkflows;
	}

	public Set<OTApplication> getOtApplications() {
		return otApplications;
	}

	public void setOtApplications(Set<OTApplication> otApplications) {
		this.otApplications = otApplications;
	}

	public Set<OTApplicationReviewer> getOtApplicationReviewers() {
		return otApplicationReviewers;
	}

	public void setOtApplicationReviewers(
			Set<OTApplicationReviewer> otApplicationReviewers) {
		this.otApplicationReviewers = otApplicationReviewers;
	}

	public Set<OTApplicationWorkflow> getOtApplicationWorkflows() {
		return otApplicationWorkflows;
	}

	public void setOtApplicationWorkflows(
			Set<OTApplicationWorkflow> otApplicationWorkflows) {
		this.otApplicationWorkflows = otApplicationWorkflows;
	}

	public Set<PayDataCollection> getPayDataCollections() {
		return payDataCollections;
	}

	public void setPayDataCollections(Set<PayDataCollection> payDataCollections) {
		this.payDataCollections = payDataCollections;
	}

	public Set<Payslip> getPayslips() {
		return payslips;
	}

	public void setPayslips(Set<Payslip> payslips) {
		this.payslips = payslips;
	}

	public Set<WorkflowDelegate> getWorkflowDelegates1() {
		return workflowDelegates1;
	}

	public void setWorkflowDelegates1(Set<WorkflowDelegate> workflowDelegates1) {
		this.workflowDelegates1 = workflowDelegates1;
	}

	public Set<WorkflowDelegate> getWorkflowDelegates2() {
		return workflowDelegates2;
	}

	public void setWorkflowDelegates2(Set<WorkflowDelegate> workflowDelegates2) {
		this.workflowDelegates2 = workflowDelegates2;
	}

	public Set<EmployeeLeaveScheme> getEmployeeLeaveSchemes() {
		return employeeLeaveSchemes;
	}

	public void setEmployeeLeaveSchemes(
			Set<EmployeeLeaveScheme> employeeLeaveSchemes) {
		this.employeeLeaveSchemes = employeeLeaveSchemes;
	}

	public Set<EmployeeCalendarConfig> getEmployeeCalendarConfigs() {
		return employeeCalendarConfigs;
	}

	public void setEmployeeCalendarConfigs(
			Set<EmployeeCalendarConfig> employeeCalendarConfigs) {
		this.employeeCalendarConfigs = employeeCalendarConfigs;
	}

	public Set<LeaveGrantBatchEmployeeDetail> getLeaveGrantBatchEmployeeDetails() {
		return leaveGrantBatchEmployeeDetails;
	}

	public void setLeaveGrantBatchEmployeeDetails(
			Set<LeaveGrantBatchEmployeeDetail> leaveGrantBatchEmployeeDetails) {
		this.leaveGrantBatchEmployeeDetails = leaveGrantBatchEmployeeDetails;
	}

	public Set<LeaveYearEndEmployeeDetail> getLeaveYearEndEmployeeDetails() {
		return leaveYearEndEmployeeDetails;
	}

	public void setLeaveYearEndEmployeeDetails(
			Set<LeaveYearEndEmployeeDetail> leaveYearEndEmployeeDetails) {
		this.leaveYearEndEmployeeDetails = leaveYearEndEmployeeDetails;
	}

	public Set<EmployeeHolidayCalendar> getEmployeeHolidayCalendars() {
		return employeeHolidayCalendars;
	}

	public void setEmployeeHolidayCalendars(
			Set<EmployeeHolidayCalendar> employeeHolidayCalendars) {
		this.employeeHolidayCalendars = employeeHolidayCalendars;
	}

	public Set<EmployeeClaimTemplate> getEmployeeClaimTemplates() {
		return employeeClaimTemplates;
	}

	public void setEmployeeClaimTemplates(
			Set<EmployeeClaimTemplate> employeeClaimTemplates) {
		this.employeeClaimTemplates = employeeClaimTemplates;
	}

	public String getEmploymentStatus() {
		return employmentStatus;
	}

	public void setEmploymentStatus(String employmentStatus) {
		this.employmentStatus = employmentStatus;
	}

	public Set<EmployeeLoginDetail> getEmployeeLoginDetails() {
		return employeeLoginDetails;
	}

	public void setEmployeeLoginDetails(
			Set<EmployeeLoginDetail> employeeLoginDetails) {
		this.employeeLoginDetails = employeeLoginDetails;
	}

	public EmployeeLoginDetail getEmployeeLoginDetail() {
		return employeeLoginDetails.iterator().next();
	}

	public Set<EmployeeActivationCode> getEmployeeActivationCodes() {
		return employeeActivationCodes;
	}

	public void setEmployeeActivationCodes(
			Set<EmployeeActivationCode> employeeActivationCodes) {
		this.employeeActivationCodes = employeeActivationCodes;
	}

	public Set<EmployeeRoleSectionMapping> getEmployeeRoleSectionMappings() {
		return employeeRoleSectionMappings;
	}

	public void setEmployeeRoleSectionMappings(
			Set<EmployeeRoleSectionMapping> employeeRoleSectionMappings) {
		this.employeeRoleSectionMappings = employeeRoleSectionMappings;
	}

	public Set<NotificationAlert> getNotificationAlerts() {
		return notificationAlerts;
	}

	public void setNotificationAlerts(Set<NotificationAlert> notificationAlerts) {
		this.notificationAlerts = notificationAlerts;
	}

	public Set<EmployeeTimesheetReviewer> getEmployeeTimesheetReviewer1() {
		return employeeTimesheetReviewer1;
	}

	public void setEmployeeTimesheetReviewer1(
			Set<EmployeeTimesheetReviewer> employeeTimesheetReviewer1) {
		this.employeeTimesheetReviewer1 = employeeTimesheetReviewer1;
	}

	public Set<EmployeeTimesheetReviewer> getEmployeeTimesheetReviewer2() {
		return employeeTimesheetReviewer2;
	}

	public void setEmployeeTimesheetReviewer2(
			Set<EmployeeTimesheetReviewer> employeeTimesheetReviewer2) {
		this.employeeTimesheetReviewer2 = employeeTimesheetReviewer2;
	}

}