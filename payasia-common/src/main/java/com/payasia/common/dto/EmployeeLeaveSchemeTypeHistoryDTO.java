package com.payasia.common.dto;

import java.math.BigDecimal;
import java.util.List;

public class EmployeeLeaveSchemeTypeHistoryDTO {

	private long employeeLeaveSchemeTypeHistoryId;
	private String createdBy;
	private String createdDate;
	private BigDecimal days;
	private String endDate;
	private String startDate;
	private String updatedBy;
	private String updatedDate;
	private long transactionType;
	private String type;
	private long employeeLeaveSchemeTypeId;
	private long leaveApplicationId;
	private long leaveStatusMasterId;
	private String reason;
	private Long fromSessionId;
	private Long toSessionId;
	private Boolean forfeitAtEndDate;
	private Boolean isAdmin;
	private Boolean isValidDateForLeaveCancel;
	private java.sql.Timestamp startDateT;
	private java.sql.Timestamp endDateT;
	private String transactionRowId;
	private String typeName;
	private List<LeaveApplicationWorkflowDTO> workflowList;
	private String fromSessionLabelKey;
	private String toSessionLabelKey;
	private String submittedBy;
	private Boolean leaveExtension;
	private Boolean action;
	private String fromSession;
	private String toSession;
	
	
	public String getFromSession() {
		return fromSession;
	}


	public void setFromSession(String fromSession) {
		this.fromSession = fromSession;
	}


	public String getToSession() {
		return toSession;
	}


	public void setToSession(String toSession) {
		this.toSession = toSession;
	}


	public Boolean isLeaveExtension() {
		return leaveExtension;
	}
	

	public Boolean getLeaveExtension() {
		return leaveExtension;
	}

	public void setLeaveExtension(Boolean leaveExtension) {
		this.leaveExtension = leaveExtension;
	}

	public java.sql.Timestamp getStartDateT() {
		return startDateT;
	}

	public void setStartDateT(java.sql.Timestamp startDateT) {
		this.startDateT = startDateT;
	}

	public java.sql.Timestamp getEndDateT() {
		return endDateT;
	}

	public void setEndDateT(java.sql.Timestamp endDateT) {
		this.endDateT = endDateT;
	}

	public Long getFromSessionId() {
		return fromSessionId;
	}

	public void setFromSessionId(Long fromSessionId) {
		this.fromSessionId = fromSessionId;
	}

	public Long getToSessionId() {
		return toSessionId;
	}

	public void setToSessionId(Long toSessionId) {
		this.toSessionId = toSessionId;
	}

	public long getEmployeeLeaveSchemeTypeHistoryId() {
		return employeeLeaveSchemeTypeHistoryId;
	}

	public void setEmployeeLeaveSchemeTypeHistoryId(
			long employeeLeaveSchemeTypeHistoryId) {
		this.employeeLeaveSchemeTypeHistoryId = employeeLeaveSchemeTypeHistoryId;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public BigDecimal getDays() {
		return days;
	}

	public void setDays(BigDecimal days) {
		this.days = days;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public String getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(String updatedDate) {
		this.updatedDate = updatedDate;
	}

	public long getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(long transactionType) {
		this.transactionType = transactionType;
	}

	public long getEmployeeLeaveSchemeTypeId() {
		return employeeLeaveSchemeTypeId;
	}

	public void setEmployeeLeaveSchemeTypeId(long employeeLeaveSchemeTypeId) {
		this.employeeLeaveSchemeTypeId = employeeLeaveSchemeTypeId;
	}

	public long getLeaveApplicationId() {
		return leaveApplicationId;
	}

	public void setLeaveApplicationId(long leaveApplicationId) {
		this.leaveApplicationId = leaveApplicationId;
	}

	public long getLeaveStatusMasterId() {
		return leaveStatusMasterId;
	}

	public void setLeaveStatusMasterId(long leaveStatusMasterId) {
		this.leaveStatusMasterId = leaveStatusMasterId;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Boolean getIsAdmin() {
		return isAdmin;
	}

	public void setIsAdmin(Boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

	public Boolean getForfeitAtEndDate() {
		return forfeitAtEndDate;
	}

	public void setForfeitAtEndDate(Boolean forfeitAtEndDate) {
		this.forfeitAtEndDate = forfeitAtEndDate;
	}

	public Boolean getIsValidDateForLeaveCancel() {
		return isValidDateForLeaveCancel;
	}

	public void setIsValidDateForLeaveCancel(Boolean isValidDateForLeaveCancel) {
		this.isValidDateForLeaveCancel = isValidDateForLeaveCancel;
	}

	public String getTransactionRowId() {
		return transactionRowId;
	}

	public void setTransactionRowId(String transactionRowId) {
		this.transactionRowId = transactionRowId;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public List<LeaveApplicationWorkflowDTO> getWorkflowList() {
		return workflowList;
	}

	public void setWorkflowList(List<LeaveApplicationWorkflowDTO> workflowList) {
		this.workflowList = workflowList;
	}

	public String getFromSessionLabelKey() {
		return fromSessionLabelKey;
	}

	public void setFromSessionLabelKey(String fromSessionLabelKey) {
		this.fromSessionLabelKey = fromSessionLabelKey;
	}

	public String getToSessionLabelKey() {
		return toSessionLabelKey;
	}

	public void setToSessionLabelKey(String toSessionLabelKey) {
		this.toSessionLabelKey = toSessionLabelKey;
	}

	public String getSubmittedBy() {
		return submittedBy;
	}

	public void setSubmittedBy(String submittedBy) {
		this.submittedBy = submittedBy;
	}


	public Boolean getAction() {
		return action;
	}


	public void setAction(Boolean action) {
		this.action = action;
	}

	


}
