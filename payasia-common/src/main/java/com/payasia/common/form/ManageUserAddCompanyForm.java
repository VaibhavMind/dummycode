package com.payasia.common.form;

import java.io.Serializable;

public class ManageUserAddCompanyForm implements Serializable{
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -5837983917453339302L;
	private Long companyId;
	private String companyName;
	private String companyCode;
	private Long groupId;
	private String groupName;
	private String groupCode;
	private String shortList;
	
	public Long getGroupId() {
		return groupId;
	}
	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getGroupCode() {
		return groupCode;
	}
	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}
	public Long getCompanyId() {
		return companyId;
	}
	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getCompanyCode() {
		return companyCode;
	}
	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}
	public String getShortList() {
		return shortList;
	}
	public void setShortList(String shortList) {
		this.shortList = shortList;
	}


}
