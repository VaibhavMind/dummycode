package com.payasia.dao.bean;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * The persistent class for the Workflow_Delegate database table.
 * 
 */
@Entity
@Table(name = "Workflow_Delegate")
public class WorkflowDelegate extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY )
	@Column(name = "Workflow_Delegate_ID")
	private long workflowDelegateId;

	@Column(name = "End_Date")
	private Timestamp endDate;

	@Column(name = "Start_Date")
	private Timestamp startDate;

	@ManyToOne
	@JoinColumn(name = "Workflow_Type")
	private AppCodeMaster workflowType;

	 
	@ManyToOne
	@JoinColumn(name = "Company_ID")
	private Company company;

	 
	@ManyToOne
	@JoinColumn(name = "User_ID")
	private Employee employee1;

	 
	@ManyToOne
	@JoinColumn(name = "Delegatee_ID")
	private Employee employee2;

	public WorkflowDelegate() {
	}

	public long getWorkflowDelegateId() {
		return this.workflowDelegateId;
	}

	public void setWorkflowDelegateId(long workflowDelegateId) {
		this.workflowDelegateId = workflowDelegateId;
	}

	public Timestamp getEndDate() {
		return this.endDate;
	}

	public void setEndDate(Timestamp endDate) {
		this.endDate = endDate;
	}

	public Timestamp getStartDate() {
		return this.startDate;
	}

	public void setStartDate(Timestamp startDate) {
		this.startDate = startDate;
	}

	public Company getCompany() {
		return this.company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public Employee getEmployee1() {
		return this.employee1;
	}

	public void setEmployee1(Employee employee1) {
		this.employee1 = employee1;
	}

	public Employee getEmployee2() {
		return this.employee2;
	}

	public void setEmployee2(Employee employee2) {
		this.employee2 = employee2;
	}

	public AppCodeMaster getWorkflowType() {
		return workflowType;
	}

	public void setWorkflowType(AppCodeMaster workflowType) {
		this.workflowType = workflowType;
	}

}