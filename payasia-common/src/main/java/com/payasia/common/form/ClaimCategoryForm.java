package com.payasia.common.form;

import java.io.Serializable;

public class ClaimCategoryForm  implements Serializable {

	private static final long serialVersionUID = -8027370500903953684L;
	private Long claimCategoryID;
	private String claimCategoryName;
	public Long getClaimCategoryID() {
		return claimCategoryID;
	}
	public void setClaimCategoryID(Long claimCategoryID) {
		this.claimCategoryID = claimCategoryID;
	}
	public String getClaimCategoryName() {
		return claimCategoryName;
	}
	public void setClaimCategoryName(String claimCategoryName) {
		this.claimCategoryName = claimCategoryName;
	}
	
		
}
