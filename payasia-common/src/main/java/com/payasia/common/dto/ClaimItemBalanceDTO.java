package com.payasia.common.dto;

import java.io.Serializable;

public class ClaimItemBalanceDTO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -460206139744635484L;
	private Float claimItemBalance;
	private Integer errorCode;
	private String errorKey;
	private String errorValue;
	private String entitlementType;
	private Boolean hasClaimLimit;
	
	
	
	

	public Boolean getHasClaimLimit() {
		return hasClaimLimit;
	}

	public void setHasClaimLimit(Boolean hasClaimLimit) {
		this.hasClaimLimit = hasClaimLimit;
	}

	public Integer getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(Integer errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorKey() {
		return errorKey;
	}

	public void setErrorKey(String errorKey) {
		this.errorKey = errorKey;
	}

	public String getErrorValue() {
		return errorValue;
	}

	public void setErrorValue(String errorValue) {
		this.errorValue = errorValue;
	}

	public String getEntitlementType() {
		return entitlementType;
	}

	public void setEntitlementType(String entitlementType) {
		this.entitlementType = entitlementType;
	}

	public Float getClaimItemBalance() {
		return claimItemBalance;
	}

	public void setClaimItemBalance(Float claimItemBalance) {
		this.claimItemBalance = claimItemBalance;
	}
	
	
	

}
