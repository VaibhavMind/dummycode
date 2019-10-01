package com.payasia.common.dto;

import java.io.Serializable;
import java.sql.Timestamp;


public class GrantEmployeeLeaveVO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3432705022705963679L;
	private Long companyId;
	private Timestamp currentDate;
	private Boolean isYearEndProcess;
	public Long getCompanyId() {
		return companyId;
	}
	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public Boolean getIsYearEndProcess() {
		return isYearEndProcess;
	}
	public void setIsYearEndProcess(Boolean isYearEndProcess) {
		this.isYearEndProcess = isYearEndProcess;
	}
	public Timestamp getCurrentDate() {
		return currentDate;
	}
	public void setCurrentDate(Timestamp currentDate) {
		this.currentDate = currentDate;
	}
	
	

}
