package com.payasia.dao.bean;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * The persistent class for the Claim_Amount_Reviewer_Template database table.
 * 
 */
@Entity
@Table(name = "Claim_Amount_Reviewer_Template")
public class ClaimAmountReviewerTemplate extends CompanyBaseEntity implements
		Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Claim_Amount_Reviewer_Template_ID")
	private long claimAmountReviewerTemplateId;

	 
	@ManyToOne
	@JoinColumn(name = "Claim_Template_ID")
	private ClaimTemplate claimTemplate;

	@Column(name = "From_Claim_Amount")
	private BigDecimal fromClaimAmount;

	@Column(name = "To_Claim_Amount")
	private BigDecimal toClaimAmount;

	 
	@ManyToOne
	@JoinColumn(name = "Level1")
	private WorkFlowRuleMaster Level1;

	 
	@ManyToOne
	@JoinColumn(name = "Level2")
	private WorkFlowRuleMaster Level2;

	 
	@ManyToOne
	@JoinColumn(name = "Level3")
	private WorkFlowRuleMaster Level3;

	public ClaimAmountReviewerTemplate() {
	}

	public long getClaimAmountReviewerTemplateId() {
		return claimAmountReviewerTemplateId;
	}

	public void setClaimAmountReviewerTemplateId(
			long claimAmountReviewerTemplateId) {
		this.claimAmountReviewerTemplateId = claimAmountReviewerTemplateId;
	}

	public BigDecimal getFromClaimAmount() {
		return fromClaimAmount;
	}

	public void setFromClaimAmount(BigDecimal fromClaimAmount) {
		this.fromClaimAmount = fromClaimAmount;
	}

	public BigDecimal getToClaimAmount() {
		return toClaimAmount;
	}

	public void setToClaimAmount(BigDecimal toClaimAmount) {
		this.toClaimAmount = toClaimAmount;
	}

	public WorkFlowRuleMaster getLevel1() {
		return Level1;
	}

	public void setLevel1(WorkFlowRuleMaster level1) {
		Level1 = level1;
	}

	public WorkFlowRuleMaster getLevel2() {
		return Level2;
	}

	public void setLevel2(WorkFlowRuleMaster level2) {
		Level2 = level2;
	}

	public WorkFlowRuleMaster getLevel3() {
		return Level3;
	}

	public void setLevel3(WorkFlowRuleMaster level3) {
		Level3 = level3;
	}

	public ClaimTemplate getClaimTemplate() {
		return claimTemplate;
	}

	public void setClaimTemplate(ClaimTemplate claimTemplate) {
		this.claimTemplate = claimTemplate;
	}

}