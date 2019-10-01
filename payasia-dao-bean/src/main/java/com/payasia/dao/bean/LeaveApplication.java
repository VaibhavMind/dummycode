package com.payasia.dao.bean;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * The persistent class for the Leave_Application database table.
 * 
 */
@Entity
@Table(name = "Leave_Application")
public class LeaveApplication extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "Leave_Application_ID")
	private long leaveApplicationId;

	@Column(name = "Apply_To")
	private String applyTo;

	@Column(name = "Email_CC")
	private String emailCC;

	@Column(name = "End_Date")
	private Timestamp endDate;

	@Column(name = "Reason")
	private String reason;

	@Column(name = "Start_Date")
	private Timestamp startDate;

	@Column(name = "Total_Days")
	private float totalDays;

	@Column(name = "Excess_Days")
	private Float excessDays;

	@Column(name = "Pre_Approval_Request")
	private Boolean preApprovalRequest;

	@Column(name = "Leave_Extension")
	private Boolean leaveExtension;

	@OneToMany(mappedBy = "leaveApplication")
	private Set<EmployeeLeaveSchemeTypeHistory> employeeLeaveSchemeTypeHistories;

	@OneToMany(mappedBy = "leaveApplication", cascade = CascadeType.REMOVE)
	private Set<LeaveApplicationCustomField> leaveApplicationCustomFields;

	@OneToMany(mappedBy = "leaveApplication", cascade = CascadeType.REMOVE)
	private Set<LeaveApplicationExtensionDetails> leaveApplicationExtensionDetails;

	@ManyToOne
	@JoinColumn(name = "Company_ID")
	private Company company;

	@ManyToOne
	@JoinColumn(name = "Employee_ID")
	private Employee employee;

	@ManyToOne
	@JoinColumn(name = "Employee_Leave_Scheme_Type_ID")
	private EmployeeLeaveSchemeType employeeLeaveSchemeType;

	@ManyToOne
	@JoinColumn(name = "Leave_Status_ID")
	private LeaveStatusMaster leaveStatusMaster;

	@ManyToOne
	@JoinColumn(name = "Start_Session")
	private LeaveSessionMaster leaveSessionMaster1;

	@ManyToOne
	@JoinColumn(name = "End_Session")
	private LeaveSessionMaster leaveSessionMaster2;

	@OneToMany(mappedBy = "leaveApplication", cascade = CascadeType.REMOVE)
	private Set<LeaveApplicationAttachment> leaveApplicationAttachments;

	@OneToMany(mappedBy = "leaveApplication")
	private Set<LeaveApplicationReviewer> leaveApplicationReviewers;

	@OneToMany(mappedBy = "leaveApplication")
	private Set<LeaveApplicationWorkflow> leaveApplicationWorkflows;

	@ManyToOne
	@JoinColumn(name = "Cancel_Leave_Application_ID")
	private LeaveApplication leaveCancelApplication;

	public LeaveApplication() {
	}

	public Boolean getLeaveExtension() {

		if (leaveExtension == null) {
			return false;
		} else {
			return leaveExtension;
		}

	}

	public void setLeaveExtension(Boolean leaveExtension) {
		this.leaveExtension = leaveExtension;
	}

	public Boolean getPreApprovalRequest() {

		if (preApprovalRequest == null) {
			return false;
		} else {
			return preApprovalRequest;
		}

	}

	public void setPreApprovalRequest(Boolean preApprovalRequest) {
		this.preApprovalRequest = preApprovalRequest;
	}

	public long getLeaveApplicationId() {
		return this.leaveApplicationId;
	}

	public void setLeaveApplicationId(long leaveApplicationId) {
		this.leaveApplicationId = leaveApplicationId;
	}

	public String getApplyTo() {
		return this.applyTo;
	}

	public void setApplyTo(String applyTo) {
		this.applyTo = applyTo;
	}

	public String getEmailCC() {
		return this.emailCC;
	}

	public void setEmailCC(String emailCC) {
		this.emailCC = emailCC;
	}

	public Timestamp getEndDate() {
		return this.endDate;
	}

	public void setEndDate(Timestamp endDate) {
		this.endDate = endDate;
	}

	public String getReason() {
		return this.reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public Timestamp getStartDate() {
		return this.startDate;
	}

	public void setStartDate(Timestamp startDate) {
		this.startDate = startDate;
	}

	public float getTotalDays() {
		return this.totalDays;
	}

	public void setTotalDays(float totalDays) {
		this.totalDays = totalDays;
	}

	public Float getExcessDays() {
		return excessDays;
	}

	public void setExcessDays(Float excessDays) {
		this.excessDays = excessDays;
	}

	public LeaveApplication getLeaveCancelApplication() {
		return leaveCancelApplication;
	}

	public void setLeaveCancelApplication(LeaveApplication leaveCancelApplication) {
		this.leaveCancelApplication = leaveCancelApplication;
	}

	public Set<EmployeeLeaveSchemeTypeHistory> getEmployeeLeaveSchemeTypeHistories() {
		return this.employeeLeaveSchemeTypeHistories;
	}

	public void setEmployeeLeaveSchemeTypeHistories(
			Set<EmployeeLeaveSchemeTypeHistory> employeeLeaveSchemeTypeHistories) {
		this.employeeLeaveSchemeTypeHistories = employeeLeaveSchemeTypeHistories;
	}

	public Company getCompany() {
		return this.company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public Employee getEmployee() {
		return this.employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public EmployeeLeaveSchemeType getEmployeeLeaveSchemeType() {
		return employeeLeaveSchemeType;
	}

	public void setEmployeeLeaveSchemeType(EmployeeLeaveSchemeType employeeLeaveSchemeType) {
		this.employeeLeaveSchemeType = employeeLeaveSchemeType;
	}

	public LeaveStatusMaster getLeaveStatusMaster() {
		return this.leaveStatusMaster;
	}

	public void setLeaveStatusMaster(LeaveStatusMaster leaveStatusMaster) {
		this.leaveStatusMaster = leaveStatusMaster;
	}

	public Set<LeaveApplicationAttachment> getLeaveApplicationAttachments() {
		return this.leaveApplicationAttachments;
	}

	public void setLeaveApplicationAttachments(Set<LeaveApplicationAttachment> leaveApplicationAttachments) {
		this.leaveApplicationAttachments = leaveApplicationAttachments;
	}

	public Set<LeaveApplicationReviewer> getLeaveApplicationReviewers() {
		return this.leaveApplicationReviewers;
	}

	public void setLeaveApplicationReviewers(Set<LeaveApplicationReviewer> leaveApplicationReviewers) {
		this.leaveApplicationReviewers = leaveApplicationReviewers;
	}

	public Set<LeaveApplicationWorkflow> getLeaveApplicationWorkflows() {
		return this.leaveApplicationWorkflows;
	}

	public void setLeaveApplicationWorkflows(Set<LeaveApplicationWorkflow> leaveApplicationWorkflows) {
		this.leaveApplicationWorkflows = leaveApplicationWorkflows;
	}

	public LeaveSessionMaster getLeaveSessionMaster1() {
		return leaveSessionMaster1;
	}

	public void setLeaveSessionMaster1(LeaveSessionMaster leaveSessionMaster1) {
		this.leaveSessionMaster1 = leaveSessionMaster1;
	}

	public LeaveSessionMaster getLeaveSessionMaster2() {
		return leaveSessionMaster2;
	}

	public void setLeaveSessionMaster2(LeaveSessionMaster leaveSessionMaster2) {
		this.leaveSessionMaster2 = leaveSessionMaster2;
	}

	public Set<LeaveApplicationCustomField> getLeaveApplicationCustomFields() {
		return leaveApplicationCustomFields;
	}

	public void setLeaveApplicationCustomFields(Set<LeaveApplicationCustomField> leaveApplicationCustomFields) {
		this.leaveApplicationCustomFields = leaveApplicationCustomFields;
	}

	public Set<LeaveApplicationExtensionDetails> getLeaveApplicationExtensionDetails() {
		return leaveApplicationExtensionDetails;
	}

	public void setLeaveApplicationExtensionDetails(
			Set<LeaveApplicationExtensionDetails> leaveApplicationExtensionDetails) {
		this.leaveApplicationExtensionDetails = leaveApplicationExtensionDetails;
	}

}