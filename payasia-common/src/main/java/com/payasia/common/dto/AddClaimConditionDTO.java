package com.payasia.common.dto;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

public class AddClaimConditionDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4345787790295874709L;
	private Long employeeId;
	private Long claimStatusId;
	private Timestamp fromDate;
	private Timestamp toDate;
	private Long claimNumber;
	private String createdDate;
	private String claimGroup;
	private List<String> claimStatus;
	private Boolean visibleToEmployee;
	private String createdDateSortOrder;
	private String claimNumberSortOrder;
	
	
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
	public Long getClaimNumber() {
		return claimNumber;
	}
	public void setClaimNumber(Long claimNumber) {
		this.claimNumber = claimNumber;
	}
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	public String getClaimGroup() {
		return claimGroup;
	}
	public void setClaimGroup(String claimGroup) {
		this.claimGroup = claimGroup;
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
	public String getCreatedDateSortOrder() {
		return createdDateSortOrder;
	}
	public void setCreatedDateSortOrder(String createdDateSortOrder) {
		this.createdDateSortOrder = createdDateSortOrder;
	}
	public String getClaimNumberSortOrder() {
		return claimNumberSortOrder;
	}
	public void setClaimNumberSortOrder(String claimNumberSortOrder) {
		this.claimNumberSortOrder = claimNumberSortOrder;
	}
		
}
