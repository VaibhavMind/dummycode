package com.payasia.common.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public class ClaimAmtReviewersTemplateDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6957923591269701892L;
	private Long claimAmountReviewerTemplateId;
	private Long claimTemplateId;
	private BigDecimal fromClaimAmount;
	private BigDecimal toClaimAmount;
	private String level1ReviewerId;
	private String level2ReviewerId;
	private String level3ReviewerId;

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

	public Long getClaimAmountReviewerTemplateId() {
		return claimAmountReviewerTemplateId;
	}

	public void setClaimAmountReviewerTemplateId(
			Long claimAmountReviewerTemplateId) {
		this.claimAmountReviewerTemplateId = claimAmountReviewerTemplateId;
	}

	public Long getClaimTemplateId() {
		return claimTemplateId;
	}

	public void setClaimTemplateId(Long claimTemplateId) {
		this.claimTemplateId = claimTemplateId;
	}

	public String getLevel1ReviewerId() {
		return level1ReviewerId;
	}

	public void setLevel1ReviewerId(String level1ReviewerId) {
		this.level1ReviewerId = level1ReviewerId;
	}

	public String getLevel2ReviewerId() {
		return level2ReviewerId;
	}

	public void setLevel2ReviewerId(String level2ReviewerId) {
		this.level2ReviewerId = level2ReviewerId;
	}

	public String getLevel3ReviewerId() {
		return level3ReviewerId;
	}

	public void setLevel3ReviewerId(String level3ReviewerId) {
		this.level3ReviewerId = level3ReviewerId;
	}

}
