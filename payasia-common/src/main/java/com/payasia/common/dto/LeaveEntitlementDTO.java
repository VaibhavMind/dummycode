package com.payasia.common.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public class LeaveEntitlementDTO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3496918523816525190L;
	private Long leaveEntitlementId;
	private Integer year;
	private BigDecimal value;
	
	public Integer getYear() {
		return year;
	}
	public void setYear(Integer year) {
		this.year = year;
	}
	
	public BigDecimal getValue() {
		return value;
	}
	public void setValue(BigDecimal value) {
		this.value = value;
	}
	public Long getLeaveEntitlementId() {
		return leaveEntitlementId;
	}
	public void setLeaveEntitlementId(Long leaveEntitlementId) {
		this.leaveEntitlementId = leaveEntitlementId;
	}

	
}
