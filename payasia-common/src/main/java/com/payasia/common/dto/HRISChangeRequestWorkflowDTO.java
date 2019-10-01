package com.payasia.common.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class HRISChangeRequestWorkflowDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String employeeInfo;
	private String createdDate;
	private String status;
	private String Remarks;
	private String userRemarks;
	private Integer order;
	public String getEmployeeInfo() {
		return employeeInfo;
	}
	public void setEmployeeInfo(String employeeInfo) {
		this.employeeInfo = employeeInfo;
	}
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getRemarks() {
		return Remarks;
	}
	public void setRemarks(String remarks) {
		Remarks = remarks;
	}
	public String getUserRemarks() {
		return userRemarks;
	}
	public void setUserRemarks(String userRemarks) {
		this.userRemarks = userRemarks;
	}
	public Integer getOrder() {
		return order;
	}
	public void setOrder(Integer order) {
		this.order = order;
	}
	
	
	

}
