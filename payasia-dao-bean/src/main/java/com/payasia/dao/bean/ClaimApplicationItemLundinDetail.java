package com.payasia.dao.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * The persistent class for the Claim_Application_Item_Lundin_Detail database
 * table.
 * 
 */
@Entity
@Table(name = "Claim_Application_Item_Lundin_Detail")
public class ClaimApplicationItemLundinDetail extends CompanyBaseEntity
		implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Lundin_Detail_ID")
	private long lundinDetailId;

	@ManyToOne
	@JoinColumn(name = "Claim_Application_Item_ID")
	private ClaimApplicationItem claimApplicationItem;

	@ManyToOne
	@JoinColumn(name = "Block_ID")
	private LundinBlock lundinBlock;

	@ManyToOne
	@JoinColumn(name = "AFE_ID")
	private LundinAFE lundinAFE;

	public ClaimApplicationItemLundinDetail() {
	}

	public long getLundinDetailId() {
		return lundinDetailId;
	}

	public void setLundinDetailId(long lundinDetailId) {
		this.lundinDetailId = lundinDetailId;
	}

	public ClaimApplicationItem getClaimApplicationItem() {
		return claimApplicationItem;
	}

	public void setClaimApplicationItem(
			ClaimApplicationItem claimApplicationItem) {
		this.claimApplicationItem = claimApplicationItem;
	}

	public LundinBlock getLundinBlock() {
		return lundinBlock;
	}

	public void setLundinBlock(LundinBlock lundinBlock) {
		this.lundinBlock = lundinBlock;
	}

	public LundinAFE getLundinAFE() {
		return lundinAFE;
	}

	public void setLundinAFE(LundinAFE lundinAFE) {
		this.lundinAFE = lundinAFE;
	}

}