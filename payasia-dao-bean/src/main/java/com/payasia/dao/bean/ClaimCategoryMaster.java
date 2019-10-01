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
 * The persistent class for the Claim_Category_Master database table.
 * 
 */
@Entity
@Table(name = "Claim_Category_Master")
public class ClaimCategoryMaster extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name = "Claim_Category_ID")
	private long claimCategoryId;

	@Column(name = "Claim_Category_Desc")
	private String claimCategoryDesc;

	@Column(name = "Claim_Category_Name")
	private String claimCategoryName;

	@Column(name = "Code")
	private String code;

	 
	@ManyToOne
	@JoinColumn(name = "Company_ID")
	private Company company;

	 
	@OneToMany(mappedBy = "claimCategoryMaster")
	private Set<ClaimItemMaster> claimItemMasters;

	public ClaimCategoryMaster() {
	}

	public long getClaimCategoryId() {
		return this.claimCategoryId;
	}

	public void setClaimCategoryId(long claimCategoryId) {
		this.claimCategoryId = claimCategoryId;
	}

	public String getClaimCategoryDesc() {
		return this.claimCategoryDesc;
	}

	public void setClaimCategoryDesc(String claimCategoryDesc) {
		this.claimCategoryDesc = claimCategoryDesc;
	}

	public String getClaimCategoryName() {
		return this.claimCategoryName;
	}

	public void setClaimCategoryName(String claimCategoryName) {
		this.claimCategoryName = claimCategoryName;
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Company getCompany() {
		return this.company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public Set<ClaimItemMaster> getClaimItemMasters() {
		return this.claimItemMasters;
	}

	public void setClaimItemMasters(Set<ClaimItemMaster> claimItemMasters) {
		this.claimItemMasters = claimItemMasters;
	}

}