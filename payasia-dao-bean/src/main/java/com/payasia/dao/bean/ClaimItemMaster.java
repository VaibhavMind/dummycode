package com.payasia.dao.bean;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * The persistent class for the Claim_Item_Master database table.
 * 
 */
@Entity
@Table(name = "Claim_Item_Master")
public class ClaimItemMaster extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name = "Claim_Item_ID")
	private long claimItemId;

	@Column(name = "Account_Code")
	private String accountCode;

	@Column(name = "Claim_Item_Desc")
	private String claimItemDesc;

	@Column(name = "Claim_Item_Name")
	private String claimItemName;

	@Column(name = "Code")
	private String code;

	@Column(name = "Visibility")
	private boolean visibility;

	@Column(name = "Sort_Order")
	private Integer sortOrder;

	@ManyToOne
	@JoinColumn(name = "Claim_Category_ID")
	private ClaimCategoryMaster claimCategoryMaster;

	@ManyToOne
	@JoinColumn(name = "Company_ID")
	private Company company;

	@OneToMany(mappedBy = "claimItemMaster")
	private Set<ClaimTemplateItem> claimTemplateItems;

	public Integer getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
	}

	public ClaimItemMaster() {
	}

	public long getClaimItemId() {
		return this.claimItemId;
	}

	public void setClaimItemId(long claimItemId) {
		this.claimItemId = claimItemId;
	}

	public String getAccountCode() {
		return this.accountCode;
	}

	public void setAccountCode(String accountCode) {
		this.accountCode = accountCode;
	}

	public String getClaimItemDesc() {
		return this.claimItemDesc;
	}

	public void setClaimItemDesc(String claimItemDesc) {
		this.claimItemDesc = claimItemDesc;
	}

	public String getClaimItemName() {
		return this.claimItemName;
	}

	public void setClaimItemName(String claimItemName) {
		this.claimItemName = claimItemName;
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public boolean getVisibility() {
		return this.visibility;
	}

	public void setVisibility(boolean visibility) {
		this.visibility = visibility;
	}

	public ClaimCategoryMaster getClaimCategoryMaster() {
		return this.claimCategoryMaster;
	}

	public void setClaimCategoryMaster(ClaimCategoryMaster claimCategoryMaster) {
		this.claimCategoryMaster = claimCategoryMaster;
	}

	public Company getCompany() {
		return this.company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public Set<ClaimTemplateItem> getClaimTemplateItems() {
		return this.claimTemplateItems;
	}

	public void setClaimTemplateItems(Set<ClaimTemplateItem> claimTemplateItems) {
		this.claimTemplateItems = claimTemplateItems;
	}

}