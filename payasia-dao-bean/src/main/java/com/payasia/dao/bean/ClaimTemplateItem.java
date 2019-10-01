package com.payasia.dao.bean;

import java.io.Serializable;
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
 * The persistent class for the Claim_Template_Item database table.
 * 
 */
@Entity
@Table(name = "Claim_Template_Item")
public class ClaimTemplateItem extends CompanyBaseEntity implements
		Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Claim_Template_Item_ID")
	private long claimTemplateItemId;

	@Column(name = "Visibility")
	private boolean visibility;

	@Column(name = "Workflow_Changed")
	private boolean workflowChanged;

	 
	@ManyToOne
	@JoinColumn(name = "Claim_Item_ID")
	private ClaimItemMaster claimItemMaster;

	 
	@ManyToOne
	@JoinColumn(name = "Claim_Template_ID")
	private ClaimTemplate claimTemplate;

	 
	@OneToMany(mappedBy = "claimTemplateItem", cascade = { CascadeType.REMOVE })
	private Set<ClaimTemplateItemWorkflow> claimTemplateItemWorkflows;

	 
	@OneToMany(mappedBy = "claimTemplateItem", cascade = { CascadeType.REMOVE })
	private Set<ClaimTemplateItemShortlist> claimTemplateItemShortlists;

	 
	@OneToMany(mappedBy = "claimTemplateItem", cascade = { CascadeType.REMOVE })
	private Set<ClaimTemplateItemClaimType> claimTemplateItemClaimTypes;

	 
	@OneToMany(mappedBy = "claimTemplateItem", cascade = { CascadeType.REMOVE })
	private Set<ClaimTemplateItemGeneral> claimTemplateItemGenerals;

	 
	@OneToMany(mappedBy = "claimTemplateItem", cascade = { CascadeType.REMOVE })
	private Set<ClaimTemplateItemCustomField> claimTemplateItemCustomFields;

	 
	@OneToMany(mappedBy = "claimTemplateItem")
	private Set<EmployeeClaimTemplateItem> employeeClaimTemplateItems;

	public ClaimTemplateItem() {
	}

	public long getClaimTemplateItemId() {
		return this.claimTemplateItemId;
	}

	public void setClaimTemplateItemId(long claimTemplateItemId) {
		this.claimTemplateItemId = claimTemplateItemId;
	}

	public boolean getVisibility() {
		return this.visibility;
	}

	public void setVisibility(boolean visibility) {
		this.visibility = visibility;
	}

	public ClaimItemMaster getClaimItemMaster() {
		return this.claimItemMaster;
	}

	public void setClaimItemMaster(ClaimItemMaster claimItemMaster) {
		this.claimItemMaster = claimItemMaster;
	}

	public ClaimTemplate getClaimTemplate() {
		return this.claimTemplate;
	}

	public void setClaimTemplate(ClaimTemplate claimTemplate) {
		this.claimTemplate = claimTemplate;
	}

	public Set<ClaimTemplateItemWorkflow> getClaimTemplateItemWorkflows() {
		return claimTemplateItemWorkflows;
	}

	public void setClaimTemplateItemWorkflows(
			Set<ClaimTemplateItemWorkflow> claimTemplateItemWorkflows) {
		this.claimTemplateItemWorkflows = claimTemplateItemWorkflows;
	}

	public Set<ClaimTemplateItemShortlist> getClaimTemplateItemShortlists() {
		return claimTemplateItemShortlists;
	}

	public void setClaimTemplateItemShortlists(
			Set<ClaimTemplateItemShortlist> claimTemplateItemShortlists) {
		this.claimTemplateItemShortlists = claimTemplateItemShortlists;
	}

	public boolean isWorkflowChanged() {
		return workflowChanged;
	}

	public void setWorkflowChanged(boolean workflowChanged) {
		this.workflowChanged = workflowChanged;
	}

	public Set<ClaimTemplateItemClaimType> getClaimTemplateItemClaimTypes() {
		return claimTemplateItemClaimTypes;
	}

	public void setClaimTemplateItemClaimTypes(
			Set<ClaimTemplateItemClaimType> claimTemplateItemClaimTypes) {
		this.claimTemplateItemClaimTypes = claimTemplateItemClaimTypes;
	}

	public Set<ClaimTemplateItemGeneral> getClaimTemplateItemGenerals() {
		return claimTemplateItemGenerals;
	}

	public void setClaimTemplateItemGenerals(
			Set<ClaimTemplateItemGeneral> claimTemplateItemGenerals) {
		this.claimTemplateItemGenerals = claimTemplateItemGenerals;
	}

	public Set<ClaimTemplateItemCustomField> getClaimTemplateItemCustomFields() {
		return claimTemplateItemCustomFields;
	}

	public void setClaimTemplateItemCustomFields(
			Set<ClaimTemplateItemCustomField> claimTemplateItemCustomFields) {
		this.claimTemplateItemCustomFields = claimTemplateItemCustomFields;
	}

	public Set<EmployeeClaimTemplateItem> getEmployeeClaimTemplateItems() {
		return employeeClaimTemplateItems;
	}

	public void setEmployeeClaimTemplateItems(
			Set<EmployeeClaimTemplateItem> employeeClaimTemplateItems) {
		this.employeeClaimTemplateItems = employeeClaimTemplateItems;
	}

}