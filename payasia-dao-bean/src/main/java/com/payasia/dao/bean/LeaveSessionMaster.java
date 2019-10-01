package com.payasia.dao.bean;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * The persistent class for the Leave_Session_Master database table.
 * 
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Table(name = "Leave_Session_Master")
public class LeaveSessionMaster implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Leave_Session_ID")
	private long leaveSessionId;

	@Column(name = "Session")
	private String session;

	@Column(name = "Session_Desc")
	private String sessionDesc;

	@Column(name = "Session_Label_Key")
	private String sessionLabelKey;

	 
	@OneToMany(mappedBy = "leaveSessionMaster1")
	private Set<LeaveApplication> leaveApplications1;

	 
	@OneToMany(mappedBy = "leaveSessionMaster2")
	private Set<LeaveApplication> leaveApplications2;

	 
	@OneToMany(mappedBy = "startSessionMaster")
	private Set<LeaveApplicationWorkflow> startLeaveApplicationWorkflows;

	 
	@OneToMany(mappedBy = "endSessionMaster")
	private Set<LeaveApplicationWorkflow> endLeaveApplicationWorkflows;

	 
	@OneToMany(mappedBy = "startSessionMaster")
	private Set<EmployeeLeaveSchemeTypeHistory> startEmployeeLeaveSchemeTypeHistories;

	 
	@OneToMany(mappedBy = "endSessionMaster")
	private Set<EmployeeLeaveSchemeTypeHistory> endEmployeeLeaveSchemeTypeHistories;

	public LeaveSessionMaster() {
	}

	public long getLeaveSessionId() {
		return leaveSessionId;
	}

	public void setLeaveSessionId(long leaveSessionId) {
		this.leaveSessionId = leaveSessionId;
	}

	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
	}

	public String getSessionDesc() {
		return sessionDesc;
	}

	public void setSessionDesc(String sessionDesc) {
		this.sessionDesc = sessionDesc;
	}

	public Set<LeaveApplication> getLeaveApplications1() {
		return leaveApplications1;
	}

	public void setLeaveApplications1(Set<LeaveApplication> leaveApplications1) {
		this.leaveApplications1 = leaveApplications1;
	}

	public Set<LeaveApplication> getLeaveApplications2() {
		return leaveApplications2;
	}

	public void setLeaveApplications2(Set<LeaveApplication> leaveApplications2) {
		this.leaveApplications2 = leaveApplications2;
	}

	public Set<LeaveApplicationWorkflow> getStartLeaveApplicationWorkflows() {
		return startLeaveApplicationWorkflows;
	}

	public void setStartLeaveApplicationWorkflows(
			Set<LeaveApplicationWorkflow> startLeaveApplicationWorkflows) {
		this.startLeaveApplicationWorkflows = startLeaveApplicationWorkflows;
	}

	public Set<LeaveApplicationWorkflow> getEndLeaveApplicationWorkflows() {
		return endLeaveApplicationWorkflows;
	}

	public void setEndLeaveApplicationWorkflows(
			Set<LeaveApplicationWorkflow> endLeaveApplicationWorkflows) {
		this.endLeaveApplicationWorkflows = endLeaveApplicationWorkflows;
	}

	public Set<EmployeeLeaveSchemeTypeHistory> getStartEmployeeLeaveSchemeTypeHistories() {
		return startEmployeeLeaveSchemeTypeHistories;
	}

	public void setStartEmployeeLeaveSchemeTypeHistories(
			Set<EmployeeLeaveSchemeTypeHistory> startEmployeeLeaveSchemeTypeHistories) {
		this.startEmployeeLeaveSchemeTypeHistories = startEmployeeLeaveSchemeTypeHistories;
	}

	public Set<EmployeeLeaveSchemeTypeHistory> getEndEmployeeLeaveSchemeTypeHistories() {
		return endEmployeeLeaveSchemeTypeHistories;
	}

	public void setEndEmployeeLeaveSchemeTypeHistories(
			Set<EmployeeLeaveSchemeTypeHistory> endEmployeeLeaveSchemeTypeHistories) {
		this.endEmployeeLeaveSchemeTypeHistories = endEmployeeLeaveSchemeTypeHistories;
	}

	public String getSessionLabelKey() {
		return sessionLabelKey;
	}

	public void setSessionLabelKey(String sessionLabelKey) {
		this.sessionLabelKey = sessionLabelKey;
	}

}