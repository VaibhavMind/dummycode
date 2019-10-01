package com.payasia.dao.bean;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

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
 * The persistent class for the OT_Application_Workflow database table.
 * 
 */
@Entity
@Table(name="OT_Application_Workflow")
public class OTApplicationWorkflow implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="OT_Application_Workflow_ID")
	private long otApplicationWorkflowId;

	@Column(name="Created_Date")
	private Timestamp createdDate;

	@Column(name="Email_CC")
	private String emailCC;

	@Column(name="Forward_To")
	private String forwardTo;

	@Column(name="Remarks")
	private String remarks;

	 
	@OneToMany(mappedBy="otApplicationWorkflow")
	private Set<OTApplicationItemWorkflow> otApplicationItemWorkflows;

	 
    @ManyToOne
	@JoinColumn(name="Created_By")
	private Employee employee;

	 
    @ManyToOne
	@JoinColumn(name="OT_Application_ID")
	private OTApplication otApplication;

	 
    @ManyToOne
	@JoinColumn(name="OT_Status_ID")
	private OTStatusMaster otStatusMaster;

    public OTApplicationWorkflow() {
    }

	public long getOtApplicationWorkflowId() {
		return this.otApplicationWorkflowId;
	}

	public void setOtApplicationWorkflowId(long otApplicationWorkflowId) {
		this.otApplicationWorkflowId = otApplicationWorkflowId;
	}

	public Timestamp getCreatedDate() {
		return this.createdDate;
	}

	public void setCreatedDate(Timestamp createdDate) {
		this.createdDate = createdDate;
	}

	public String getEmailCC() {
		return this.emailCC;
	}

	public void setEmailCC(String emailCC) {
		this.emailCC = emailCC;
	}

	public String getForwardTo() {
		return this.forwardTo;
	}

	public void setForwardTo(String forwardTo) {
		this.forwardTo = forwardTo;
	}

	public String getRemarks() {
		return this.remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Set<OTApplicationItemWorkflow> getOtApplicationItemWorkflows() {
		return this.otApplicationItemWorkflows;
	}

	public void setOtApplicationItemWorkflows(Set<OTApplicationItemWorkflow> otApplicationItemWorkflows) {
		this.otApplicationItemWorkflows = otApplicationItemWorkflows;
	}
	
	public Employee getEmployee() {
		return this.employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}
	
	public OTApplication getOtApplication() {
		return this.otApplication;
	}

	public void setOtApplication(OTApplication otApplication) {
		this.otApplication = otApplication;
	}
	
	public OTStatusMaster getOtStatusMaster() {
		return this.otStatusMaster;
	}

	public void setOtStatusMaster(OTStatusMaster otStatusMaster) {
		this.otStatusMaster = otStatusMaster;
	}
	
}