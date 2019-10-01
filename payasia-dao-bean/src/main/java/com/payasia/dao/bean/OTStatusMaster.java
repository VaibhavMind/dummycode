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
 * The persistent class for the OT_Status_Master database table.
 * 
 */
@Entity
@Table(name="OT_Status_Master")
public class OTStatusMaster implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="OT_Status_ID")
	private long otStatusId;

	@Column(name="OT_Status_Desc")
	private String otStatusDesc;

	@Column(name="OT_Status_Name")
	private String otStatusName;

	 
	@OneToMany(mappedBy="otStatusMaster")
	private Set<OTApplication> otApplications;

	 
	@OneToMany(mappedBy="otStatusMaster")
	private Set<OTApplicationWorkflow> otApplicationWorkflows;

    public OTStatusMaster() {
    }

	public long getOtStatusId() {
		return this.otStatusId;
	}

	public void setOtStatusId(long otStatusId) {
		this.otStatusId = otStatusId;
	}

	public String getOtStatusDesc() {
		return this.otStatusDesc;
	}

	public void setOtStatusDesc(String otStatusDesc) {
		this.otStatusDesc = otStatusDesc;
	}

	public String getOtStatusName() {
		return this.otStatusName;
	}

	public void setOtStatusName(String otStatusName) {
		this.otStatusName = otStatusName;
	}

	public Set<OTApplication> getOtApplications() {
		return this.otApplications;
	}

	public void setOtApplications(Set<OTApplication> otApplications) {
		this.otApplications = otApplications;
	}
	
	public Set<OTApplicationWorkflow> getOtApplicationWorkflows() {
		return this.otApplicationWorkflows;
	}

	public void setOtApplicationWorkflows(Set<OTApplicationWorkflow> otApplicationWorkflows) {
		this.otApplicationWorkflows = otApplicationWorkflows;
	}
	
}