package com.payasia.common.dto;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

public class CoherentConditionDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4345787790295874709L;
	private Long employeeId;
	private Long claimStatusId;
	private Timestamp fromDate;
	private Timestamp toDate;
	private String createdDate;
	private List<String> claimStatus;
	private Boolean visibleToEmployee;
	private String batch;
	private String reviewer;
	
	
	
	public Boolean getVisibleToEmployee() {
		return visibleToEmployee;
	}
	public void setVisibleToEmployee(Boolean visibleToEmployee) {
		this.visibleToEmployee = visibleToEmployee;
	}
	public List<String> getClaimStatus() {
		return claimStatus;
	}
	public void setClaimStatus(List<String> claimStatus) {
		this.claimStatus = claimStatus;
	}
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	public Long getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}
	
	public Long getClaimStatusId() {
		return claimStatusId;
	}
	public void setClaimStatusId(Long claimStatusId) {
		this.claimStatusId = claimStatusId;
	}
	public Timestamp getFromDate() {
		return fromDate;
	}
	public void setFromDate(Timestamp fromDate) {
		this.fromDate = fromDate;
	}
	public Timestamp getToDate() {
		return toDate;
	}
	public void setToDate(Timestamp toDate) {
		this.toDate = toDate;
	}
	public String getBatch() {
		return batch;
	}
	public void setBatch(String batch) {
		this.batch = batch;
	}
	public String getReviewer() {
		return reviewer;
	}
	public void setReviewer(String reviewer) {
		this.reviewer = reviewer;
	}
	
		
}
