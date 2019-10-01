package com.payasia.common.dto;

import java.io.Serializable;

public class NotificationAlertConditionDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7791274820155818247L;
	
	private Long employeeId;
	private Boolean shownStatus;
	public Long getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}
	public Boolean getShownStatus() {
		return shownStatus;
	}
	public void setShownStatus(Boolean shownStatus) {
		this.shownStatus = shownStatus;
	}
	
	 

}
