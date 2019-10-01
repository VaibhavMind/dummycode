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
 * The persistent class for the HRIS_Change_Request_Workflow database table.
 * 
 */
@Entity
@Table(name = "HRIS_Change_Request_Workflow")
public class HRISChangeRequestWorkflow extends CompanyUpdatedBaseEntity
		implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY )
	@Column(name = "HRIS_Change_Request_Workflow_ID")
	private long hrisChangeRequestWorkflowId;

	 
	@ManyToOne
	@JoinColumn(name = "HRIS_Change_Request_ID")
	private HRISChangeRequest hrisChangeRequest;

	@Column(name = "New_Value")
	private String newValue;

	@Column(name = "Remarks")
	private String remarks;

	@Column(name = "Forward_To")
	private String forwardTo;

	@Column(name = "Email_CC")
	private String emailCC;

	 
	@ManyToOne
	@JoinColumn(name = "HRIS_Status_ID")
	private HRISStatusMaster hrisStatusMaster;

	 
	@ManyToOne
	@JoinColumn(name = "Created_By")
	private Employee employee;

	@Column(name = "Created_Date")
	private Timestamp createdDate;

	public HRISChangeRequestWorkflow() {
	}

	public long getHrisChangeRequestWorkflowId() {
		return hrisChangeRequestWorkflowId;
	}

	public void setHrisChangeRequestWorkflowId(long hrisChangeRequestWorkflowId) {
		this.hrisChangeRequestWorkflowId = hrisChangeRequestWorkflowId;
	}

	public HRISChangeRequest getHrisChangeRequest() {
		return hrisChangeRequest;
	}

	public void setHrisChangeRequest(HRISChangeRequest hrisChangeRequest) {
		this.hrisChangeRequest = hrisChangeRequest;
	}

	public String getNewValue() {
		return newValue;
	}

	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getForwardTo() {
		return forwardTo;
	}

	public void setForwardTo(String forwardTo) {
		this.forwardTo = forwardTo;
	}

	public String getEmailCC() {
		return emailCC;
	}

	public void setEmailCC(String emailCC) {
		this.emailCC = emailCC;
	}

	public HRISStatusMaster getHrisStatusMaster() {
		return hrisStatusMaster;
	}

	public void setHrisStatusMaster(HRISStatusMaster hrisStatusMaster) {
		this.hrisStatusMaster = hrisStatusMaster;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public Timestamp getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Timestamp createdDate) {
		this.createdDate = createdDate;
	}

}