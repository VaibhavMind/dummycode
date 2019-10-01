package com.payasia.common.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class PendingLeaveResponse implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5270743385322795576L;
	private String employeeName;
	private String leaveScheme;
	private String leaveType;
	private String fromDate;
	private String toDate;
	private Long fromSessionId;
	private Long toSessionId;
	private BigDecimal days;
	private BigDecimal balance;
	private String frowardTo;
	private String reason;
	private String remarks;
	private boolean canOverride;
	private boolean canReject;
	private boolean canApprove;
	private boolean canForward;
	List<ComboValueDTO> sessionList;
	private int totalNoOfReviewers;
	private String leaveReviewer1;
	private String leaveReviewer2;
	private String leaveReviewer3;
	private List<LeaveApplicationAttachmentDTO> attachmentList;
	private List<LeaveApplicationWorkflowDTO> workflowList;
	private String leaveApplicationStatus;
	private String leaveApplicationCreatedDate;
	private Long leaveApplicationId;
	private Long createById;
	private Long forwardToId;
	private boolean isLeaveUnitDays;
	
	
	public Long getForwardToId() {
		return forwardToId;
	}
	public void setForwardToId(Long forwardToId) {
		this.forwardToId = forwardToId;
	}
	public Long getCreateById() {
		return createById;
	}
	public void setCreateById(Long createById) {
		this.createById = createById;
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
	public String getFrowardTo() {
		return frowardTo;
	}
	public void setFrowardTo(String frowardTo) {
		this.frowardTo = frowardTo;
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
	public boolean isCanOverride() {
		return canOverride;
	}
	public void setCanOverride(boolean canOverride) {
		this.canOverride = canOverride;
	}
	public boolean isCanReject() {
		return canReject;
	}
	public void setCanReject(boolean canReject) {
		this.canReject = canReject;
	}
	public boolean isCanApprove() {
		return canApprove;
	}
	public void setCanApprove(boolean canApprove) {
		this.canApprove = canApprove;
	}
	public boolean isCanForward() {
		return canForward;
	}
	public void setCanForward(boolean canForward) {
		this.canForward = canForward;
	}
	public List<ComboValueDTO> getSessionList() {
		return sessionList;
	}
	public void setSessionList(List<ComboValueDTO> sessionList) {
		this.sessionList = sessionList;
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
	public int getTotalNoOfReviewers() {
		return totalNoOfReviewers;
	}
	public void setTotalNoOfReviewers(int totalNoOfReviewers) {
		this.totalNoOfReviewers = totalNoOfReviewers;
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
	public boolean isLeaveUnitDays() {
		return isLeaveUnitDays;
	}
	public void setLeaveUnitDays(boolean isLeaveUnitDays) {
		this.isLeaveUnitDays = isLeaveUnitDays;
	}
	
	

	
}
