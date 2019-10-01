package com.payasia.common.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.payasia.common.form.EmployeeLeaveSchemeTypeResponse;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ViewLeaveResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String employeeName;
	private String leaveScheme;
	private String leaveType;
	private String fromDate;
	private String toDate;
	private BigDecimal days;
	private BigDecimal balance;
	private String reason;
	private String remarks;
	private String session1;
	private String session2;
	private List<LeaveApplicationAttachmentDTO> attachmentList;
	private List<LeaveApplicationWorkflowDTO> workflowList;
	private String leaveApplicationStatus;
	private String leaveApplicationCreatedDate;
	private Long leaveApplicationId;
	private Long createById;
	private Boolean isWithdrawn;
	private String leaveReviewer1;
	private String leaveReviewer2;
	private String leaveReviewer3;
	private EmployeeLeaveSchemeTypeResponse employeeLeaveSchemeTypeInfo;
	private int totalNoOfReviewers;
	private boolean isLeaveUnitDays;
	
	public int getTotalNoOfReviewers() {
		return totalNoOfReviewers;
	}
	public void setTotalNoOfReviewers(int totalNoOfReviewers) {
		this.totalNoOfReviewers = totalNoOfReviewers;
	}
	public String getEmployeeName() {
		return employeeName;
	}
	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}
	public String getLeaveScheme() {
		return leaveScheme;
	}
	public void setLeaveScheme(String leaveScheme) {
		this.leaveScheme = leaveScheme;
	}
	public String getLeaveType() {
		return leaveType;
	}
	public void setLeaveType(String leaveType) {
		this.leaveType = leaveType;
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
	public BigDecimal getDays() {
		return days;
	}
	public void setDays(BigDecimal days) {
		this.days = days;
	}
	public BigDecimal getBalance() {
		return balance;
	}
	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public String getSession1() {
		return session1;
	}
	public void setSession1(String session1) {
		this.session1 = session1;
	}
	public String getSession2() {
		return session2;
	}
	public void setSession2(String session2) {
		this.session2 = session2;
	}
	public List<LeaveApplicationAttachmentDTO> getAttachmentList() {
		return attachmentList;
	}
	public void setAttachmentList(List<LeaveApplicationAttachmentDTO> attachmentList) {
		this.attachmentList = attachmentList;
	}
	public List<LeaveApplicationWorkflowDTO> getWorkflowList() {
		return workflowList;
	}
	public void setWorkflowList(List<LeaveApplicationWorkflowDTO> workflowList) {
		this.workflowList = workflowList;
	}
	public String getLeaveApplicationStatus() {
		return leaveApplicationStatus;
	}
	public void setLeaveApplicationStatus(String leaveApplicationStatus) {
		this.leaveApplicationStatus = leaveApplicationStatus;
	}
	public String getLeaveApplicationCreatedDate() {
		return leaveApplicationCreatedDate;
	}
	public void setLeaveApplicationCreatedDate(String leaveApplicationCreatedDate) {
		this.leaveApplicationCreatedDate = leaveApplicationCreatedDate;
	}
	public Long getLeaveApplicationId() {
		return leaveApplicationId;
	}
	public void setLeaveApplicationId(Long leaveApplicationId) {
		this.leaveApplicationId = leaveApplicationId;
	}
	public Long getCreateById() {
		return createById;
	}
	public void setCreateById(Long createById) {
		this.createById = createById;
	}
	public Boolean getIsWithdrawn() {
		return isWithdrawn;
	}
	public void setIsWithdrawn(Boolean isWithdrawn) {
		this.isWithdrawn = isWithdrawn;
	}
	public String getLeaveReviewer1() {
		return leaveReviewer1;
	}
	public void setLeaveReviewer1(String leaveReviewer1) {
		this.leaveReviewer1 = leaveReviewer1;
	}
	public String getLeaveReviewer2() {
		return leaveReviewer2;
	}
	public void setLeaveReviewer2(String leaveReviewer2) {
		this.leaveReviewer2 = leaveReviewer2;
	}
	public String getLeaveReviewer3() {
		return leaveReviewer3;
	}
	public void setLeaveReviewer3(String leaveReviewer3) {
		this.leaveReviewer3 = leaveReviewer3;
	}
	public EmployeeLeaveSchemeTypeResponse getEmployeeLeaveSchemeTypeInfo() {
		return employeeLeaveSchemeTypeInfo;
	}
	public void setEmployeeLeaveSchemeTypeInfo(
			EmployeeLeaveSchemeTypeResponse employeeLeaveSchemeTypeInfo) {
		this.employeeLeaveSchemeTypeInfo = employeeLeaveSchemeTypeInfo;
	}
	public boolean isLeaveUnitDays() {
		return isLeaveUnitDays;
	}
	public void setLeaveUnitDays(boolean isLeaveUnitDays) {
		this.isLeaveUnitDays = isLeaveUnitDays;
	}
	
	
	

}
