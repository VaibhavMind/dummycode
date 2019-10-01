package com.payasia.common.dto;

import java.io.Serializable;

public class YearEndProcessDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2782706283628058868L;
	
	
	private Long companyId;
	private String leaveRollOverDate;
	private String leaveActivateDate;
	public Long getCompanyId() {
		return companyId;
	}
	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}
	public String getLeaveRollOverDate() {
		return leaveRollOverDate;
	}
	public void setLeaveRollOverDate(String leaveRollOverDate) {
		this.leaveRollOverDate = leaveRollOverDate;
	}
	public String getLeaveActivateDate() {
		return leaveActivateDate;
	}
	public void setLeaveActivateDate(String leaveActivateDate) {
		this.leaveActivateDate = leaveActivateDate;
	}
	
	
	

}
