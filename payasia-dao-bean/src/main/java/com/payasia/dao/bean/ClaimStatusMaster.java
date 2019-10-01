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
 * The persistent class for the Claim_Status_Master database table.
 * 
 */
@Entity
@Table(name = "Claim_Status_Master")
public class ClaimStatusMaster implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Claim_Status_ID")
	private long claimStatusId;

	@Column(name = "Claim_Status_Desc")
	private String claimStatusDesc;

	@Column(name = "Claim_Status_Name")
	private String claimStatusName;

	@Column(name = "Label_Key")
	private String labelKey;

	@OneToMany(mappedBy = "claimStatusMaster")
	private Set<ClaimApplication> claimApplications;

	@OneToMany(mappedBy = "claimStatusMaster")
	private Set<ClaimApplicationWorkflow> claimApplicationWorkflows;

	public ClaimStatusMaster() {
	}

	public long getClaimStatusId() {
		return this.claimStatusId;
	}

	public void setClaimStatusId(long claimStatusId) {
		this.claimStatusId = claimStatusId;
	}

	public String getClaimStatusDesc() {
		return this.claimStatusDesc;
	}

	public void setClaimStatusDesc(String claimStatusDesc) {
		this.claimStatusDesc = claimStatusDesc;
	}

	public String getClaimStatusName() {
		return this.claimStatusName;
	}

	public void setClaimStatusName(String claimStatusName) {
		this.claimStatusName = claimStatusName;
	}

	public Set<ClaimApplication> getClaimApplications() {
		return this.claimApplications;
	}

	public void setClaimApplications(Set<ClaimApplication> claimApplications) {
		this.claimApplications = claimApplications;
	}

	public Set<ClaimApplicationWorkflow> getClaimApplicationWorkflows() {
		return this.claimApplicationWorkflows;
	}

	public void setClaimApplicationWorkflows(
			Set<ClaimApplicationWorkflow> claimApplicationWorkflows) {
		this.claimApplicationWorkflows = claimApplicationWorkflows;
	}

	public String getLabelKey() {
		return labelKey;
	}

	public void setLabelKey(String labelKey) {
		this.labelKey = labelKey;
	}
}