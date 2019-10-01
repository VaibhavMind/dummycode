package com.payasia.common.dto;

import java.util.List;

public class LundinPendingTsheetConditionDTO {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4371755847331764799L;
	
	private String employeeName;
	private String createdDate;
	private String batch;
	private String statusname;
	private boolean empStatus;
	private Long employeeId;
	private Long employeeReviewerId;
	private Long batchId;
	private List<String> statusNameList;
	private EmployeeShortListDTO employeeShortListDTO;
	
	public String getOtTimesheetStatusName() {
		return statusname;
	}
	public void setOtTimesheetStatusName(String statusname) {
		this.statusname = statusname;
	}
	public String getEmployeeName() {
		return employeeName;
	}
	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	public String getBatch() {
		return batch;
	}
	public void setBatch(String batch) {
		this.batch = batch;
	}
	public Long getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}
	public List<String> getStatusNameList() {
		return statusNameList;
	}
	public void setStatusNameList(List<String> statusNameList) {
		this.statusNameList = statusNameList;
	}
	public String getStatusname() {
		return statusname;
	}
	public void setStatusname(String statusname) {
		this.statusname = statusname;
	}
	public Long getBatchId() {
		return batchId;
	}
	public void setBatchId(Long batchId) {
		this.batchId = batchId;
	}
	public EmployeeShortListDTO getEmployeeShortListDTO() {
		return employeeShortListDTO;
	}
	public void setEmployeeShortListDTO(EmployeeShortListDTO employeeShortListDTO) {
		this.employeeShortListDTO = employeeShortListDTO;
	}
	public boolean isEmpStatus() {
		return empStatus;
	}
	public void setEmpStatus(boolean empStatus) {
		this.empStatus = empStatus;
	}
	public Long getEmployeeReviewerId() {
		return employeeReviewerId;
	}
	public void setEmployeeReviewerId(Long employeeReviewerId) {
		this.employeeReviewerId = employeeReviewerId;
	}
	
}
