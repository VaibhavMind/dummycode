package com.payasia.common.dto;

import java.sql.Timestamp;
import java.util.List;

public class LundinTsheetConditionDTO {
	private String batch;
	private String status;
	private String reviewers;
	private String fromDate;
	private String toDate;
	private String statusname;
	private boolean empStatus;
	private Long employeeId;
	private Long employeeReviewerId;
	private Long batchId;
	private List<String> statusNameList;
	private EmployeeShortListDTO employeeShortListDTO;
	
	private Timestamp fromBatchDate;
	private Timestamp toBatchDate;
	private String shiftType;
	private Timestamp approvedFromDate;
	private Timestamp approvedToDate;
	
	public String getShiftType() {
		return shiftType;
	}
	public void setShiftType(String shiftType) {
		this.shiftType = shiftType;
	}
	public String getBatch() {
		return batch;
	}
	public void setBatch(String batch) {
		this.batch = batch;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getReviewers() {
		return reviewers;
	}
	public void setReviewers(String reviewers) {
		this.reviewers = reviewers;
	}
	public String getFromDate() {
		return fromDate;
	}
	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}
	public String getToDate() {
		return toDate;
	}
	public void setToDate(String toDate) {
		this.toDate = toDate;
	}
	public String getStatusname() {
		return statusname;
	}
	public void setStatusname(String statusname) {
		this.statusname = statusname;
	}
	public boolean isEmpStatus() {
		return empStatus;
	}
	public void setEmpStatus(boolean empStatus) {
		this.empStatus = empStatus;
	}
	public Long getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}
	public Long getEmployeeReviewerId() {
		return employeeReviewerId;
	}
	public void setEmployeeReviewerId(Long employeeReviewerId) {
		this.employeeReviewerId = employeeReviewerId;
	}
	public List<String> getStatusNameList() {
		return statusNameList;
	}
	public void setStatusNameList(List<String> statusNameList) {
		this.statusNameList = statusNameList;
	}
	public EmployeeShortListDTO getEmployeeShortListDTO() {
		return employeeShortListDTO;
	}
	public void setEmployeeShortListDTO(EmployeeShortListDTO employeeShortListDTO) {
		this.employeeShortListDTO = employeeShortListDTO;
	}
	public Long getBatchId() {
		return batchId;
	}
	public void setBatchId(Long batchId) {
		this.batchId = batchId;
	}
	public Timestamp getFromBatchDate() {
		return fromBatchDate;
	}
	public void setFromBatchDate(Timestamp fromBatchDate) {
		this.fromBatchDate = fromBatchDate;
	}
	public Timestamp getToBatchDate() {
		return toBatchDate;
	}
	public void setToBatchDate(Timestamp toBatchDate) {
		this.toBatchDate = toBatchDate;
	}
	public Timestamp getApprovedFromDate() {
		return approvedFromDate;
	}
	public void setApprovedFromDate(Timestamp approvedFromDate) {
		this.approvedFromDate = approvedFromDate;
	}
	public Timestamp getApprovedToDate() {
		return approvedToDate;
	}
	public void setApprovedToDate(Timestamp approvedToDate) {
		this.approvedToDate = approvedToDate;
	}
	
}
