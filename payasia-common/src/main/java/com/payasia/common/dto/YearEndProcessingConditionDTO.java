package com.payasia.common.dto;

import java.io.Serializable;

public class YearEndProcessingConditionDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private Integer year;
	private Long groupId;
	private Long companyId;
	private Long leaveSchemeId;
	private Long leaveTypeId;
	public Integer getYear() {
		return year;
	}
	public void setYear(Integer year) {
		this.year = year;
	}
	public Long getGroupId() {
		return groupId;
	}
	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}
	public Long getCompanyId() {
		return companyId;
	}
	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}
	public Long getLeaveSchemeId() {
		return leaveSchemeId;
	}
	public void setLeaveSchemeId(Long leaveSchemeId) {
		this.leaveSchemeId = leaveSchemeId;
	}
	public Long getLeaveTypeId() {
		return leaveTypeId;
	}
	public void setLeaveTypeId(Long leaveTypeId) {
		this.leaveTypeId = leaveTypeId;
	}
	
	
	
	
	
	

}
