package com.payasia.dao.bean;

import java.io.Serializable;
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
 * The persistent class for the Claim_Application database table.
 * 
 */
@Entity
@Table(name = "Employee_Claim_Template_Item")
public class EmployeeClaimTemplateItem extends CompanyBaseEntity implements
		Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Employee_Claim_Template_Item_ID")
	private long employeeClaimTemplateItemId;

	 
	@ManyToOne
	@JoinColumn(name = "Employee_Claim_Template_ID")
	private EmployeeClaimTemplate employeeClaimTemplate;

	 
	@ManyToOne
	@JoinColumn(name = "Claim_Template_Item_ID")
	private ClaimTemplateItem claimTemplateItem;

	 
	@OneToMany(mappedBy = "employeeClaimTemplateItem")
	private Set<ClaimApplicationItem> claimApplicationItems;

	@Column(name = "Active")
	private Boolean active = true;

	public EmployeeClaimTemplateItem() {
	}

	public long getEmployeeClaimTemplateItemId() {
		return employeeClaimTemplateItemId;
	}

	public void setEmployeeClaimTemplateItemId(long employeeClaimTemplateItemId) {
		this.employeeClaimTemplateItemId = employeeClaimTemplateItemId;
	}

	public EmployeeClaimTemplate getEmployeeClaimTemplate() {
		return employeeClaimTemplate;
	}

	public void setEmployeeClaimTemplate(
			EmployeeClaimTemplate employeeClaimTemplate) {
		this.employeeClaimTemplate = employeeClaimTemplate;
	}

	public ClaimTemplateItem getClaimTemplateItem() {
		return claimTemplateItem;
	}

	public void setClaimTemplateItem(ClaimTemplateItem claimTemplateItem) {
		this.claimTemplateItem = claimTemplateItem;
	}

	public Set<ClaimApplicationItem> getClaimApplicationItems() {
		return claimApplicationItems;
	}

	public void setClaimApplicationItems(
			Set<ClaimApplicationItem> claimApplicationItems) {
		this.claimApplicationItems = claimApplicationItems;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

}