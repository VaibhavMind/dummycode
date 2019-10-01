package com.payasia.dao.bean;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;


/**
 * The persistent class for the Leave_Status_Master database table.
 * 
 */
@Entity
@Table(name="Leave_Status_Master")
public class LeaveStatusMaster implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="Leave_Status_ID")
	private long leaveStatusID;

	@Column(name="Leave_Status_Desc")
	private String leaveStatusDesc;

	@Column(name="Leave_Status_Name")
	private String leaveStatusName;

	 
	@OneToMany(mappedBy="leaveStatusMaster")
	private Set<EmployeeLeaveSchemeTypeHistory> employeeLeaveSchemeTypeHistories;

	 
	@OneToMany(mappedBy="leaveStatusMaster")
	private Set<LeaveApplication> leaveApplications;

	 
	@OneToMany(mappedBy="leaveStatusMaster")
	private Set<LeaveApplicationWorkflow> leaveApplicationWorkflows;

    public LeaveStatusMaster() {
    }

	public long getLeaveStatusID() {
		return this.leaveStatusID;
	}

	public void setLeaveStatusID(long leaveStatusID) {
		this.leaveStatusID = leaveStatusID;
	}

	public String getLeaveStatusDesc() {
		return this.leaveStatusDesc;
	}

	public void setLeaveStatusDesc(String leaveStatusDesc) {
		this.leaveStatusDesc = leaveStatusDesc;
	}

	public String getLeaveStatusName() {
		return this.leaveStatusName;
	}

	public void setLeaveStatusName(String leaveStatusName) {
		this.leaveStatusName = leaveStatusName;
	}

	public Set<EmployeeLeaveSchemeTypeHistory> getEmployeeLeaveSchemeTypeHistories() {
		return this.employeeLeaveSchemeTypeHistories;
	}

	public void setEmployeeLeaveSchemeTypeHistories(Set<EmployeeLeaveSchemeTypeHistory> employeeLeaveSchemeTypeHistories) {
		this.employeeLeaveSchemeTypeHistories = employeeLeaveSchemeTypeHistories;
	}
	
	public Set<LeaveApplication> getLeaveApplications() {
		return this.leaveApplications;
	}

	public void setLeaveApplications(Set<LeaveApplication> leaveApplications) {
		this.leaveApplications = leaveApplications;
	}
	
	public Set<LeaveApplicationWorkflow> getLeaveApplicationWorkflows() {
		return this.leaveApplicationWorkflows;
	}

	public void setLeaveApplicationWorkflows(Set<LeaveApplicationWorkflow> leaveApplicationWorkflows) {
		this.leaveApplicationWorkflows = leaveApplicationWorkflows;
	}
	
}