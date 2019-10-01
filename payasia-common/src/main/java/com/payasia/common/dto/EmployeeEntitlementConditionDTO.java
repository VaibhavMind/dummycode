package com.payasia.common.dto;

import java.io.Serializable;

public class EmployeeEntitlementConditionDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9168377678594114047L;
	
	private long employeeId;
	
	private long claimTemplateId;
	
	private int year;

	public long getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(long employeeId) {
		this.employeeId = employeeId;
	}

	public long getClaimTemplateId() {
		return claimTemplateId;
	}

	public void setClaimTemplateId(long claimTemplateId) {
		this.claimTemplateId = claimTemplateId;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	
	
	

}
