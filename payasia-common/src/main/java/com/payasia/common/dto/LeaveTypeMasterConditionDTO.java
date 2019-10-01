package com.payasia.common.dto;

import java.io.Serializable;

/**
 * 
 * The class LeaveTypeMasterConditionDTO
 * 
 */
public class LeaveTypeMasterConditionDTO implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8348466044103171015L;
	private String name;
	private String code;
	private String accountCode;
	private String visibleOrHidden;
	private Long companyId;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getAccountCode() {
		return accountCode;
	}

	public void setAccountCode(String accountCode) {
		this.accountCode = accountCode;
	}

	public String getVisibleOrHidden() {
		return visibleOrHidden;
	}

	public void setVisibleOrHidden(String visibleOrHidden) {
		this.visibleOrHidden = visibleOrHidden;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

}
