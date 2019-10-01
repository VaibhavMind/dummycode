package com.payasia.common.dto;

import java.io.Serializable;

public class CompanyDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6742808022730258730L;
	
	private Long id;
	private String name;
	private String leaveRollOverValue;
	private String leaveActivateValue;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLeaveRollOverValue() {
		return leaveRollOverValue;
	}
	public void setLeaveRollOverValue(String leaveRollOverValue) {
		this.leaveRollOverValue = leaveRollOverValue;
	}
	public String getLeaveActivateValue() {
		return leaveActivateValue;
	}
	public void setLeaveActivateValue(String leaveActivateValue) {
		this.leaveActivateValue = leaveActivateValue;
	}
	
	

}
