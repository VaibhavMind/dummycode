package com.payasia.common.dto;

import java.io.Serializable;

public class EmpWiseTemplateClaimedEntitlementDataDTO implements Serializable {
	private static final long serialVersionUID = 6083504707284690235L;
	
	private String claimItemName;
	
	private String entitlement;
	private String claimed;
	private String entitlementBalance;
	
	public String getClaimItemName() {
		return claimItemName;
	}
	public void setClaimItemName(String claimItemName) {
		this.claimItemName = claimItemName;
	}
	public String getEntitlement() {
		return entitlement;
	}
	public void setEntitlement(String entitlement) {
		this.entitlement = entitlement;
	}
	public String getClaimed() {
		return claimed;
	}
	public void setClaimed(String claimed) {
		this.claimed = claimed;
	}
	public String getEntitlementBalance() {
		return entitlementBalance;
	}
	public void setEntitlementBalance(String entitlementBalance) {
		this.entitlementBalance = entitlementBalance;
	}
	
	
	

}
