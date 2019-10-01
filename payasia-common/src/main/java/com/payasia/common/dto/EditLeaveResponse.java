package com.payasia.common.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.list.LazyList;

import com.payasia.common.form.EmployeeLeaveSchemeTypeResponse;
import com.payasia.common.form.LeaveTypeForm;

public class EditLeaveResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 786620403240613218L;
	private Long employeeLeaveSchemeId;
	private String leaveSchemName;
	private List<LeaveTypeForm> employeeLeaveSchemeTypes;
	private List<ComboValueDTO> sessionList;
	private String reason;
	private String remarks;
	private Long fromSessionId;
	private Long toSessionId;
	private String fromSession;
	private String toSession;
	private BigDecimal leaveBalance;
	private BigDecimal noOfDays;
	private Long applyToId;
	private String applyTo;
	private String applyToEmail;
	private String leaveReviewer1;
	private String leaveReviewer2;
	private Long leaveReviewer2Id;
	private String leaveReviewer3;
	private Long leaveReviewer3Id;
	private List<LeaveApplicationAttachmentDTO> attachmentList;
	private List<LeaveApplicationWorkflowDTO> workflowList;
	private String fromDate;
	private String toDate;
	private Long employeeLeaveSchemeTypeId;
	private String emailCC;
	private String instruction;
	private List<LeaveCustomFieldDTO> customFieldDTOList = LazyList.decorate(
			new ArrayList<LeaveCustomFieldDTO>(),
			FactoryUtils.instantiateFactory(LeaveCustomFieldDTO.class));
	private boolean isLeaveUnitDays;

	private EmployeeLeaveSchemeTypeResponse employeeLeaveSchemeTypeInfo;

	public Long getEmployeeLeaveSchemeId() {
		return employeeLeaveSchemeId;
	}

	public void setEmployeeLeaveSchemeId(Long employeeLeaveSchemeId) {
		this.employeeLeaveSchemeId = employeeLeaveSchemeId;
	}

	public String getLeaveSchemName() {
		return leaveSchemName;
	}

	public void setLeaveSchemName(String leaveSchemName) {
		this.leaveSchemName = leaveSchemName;
	}

	public List<LeaveTypeForm> getEmployeeLeaveSchemeTypes() {
		return employeeLeaveSchemeTypes;
	}

	public void setEmployeeLeaveSchemeTypes(
			List<LeaveTypeForm> employeeLeaveSchemeTypes) {
		this.employeeLeaveSchemeTypes = employeeLeaveSchemeTypes;
	}

	public List<ComboValueDTO> getSessionList() {
		return sessionList;
	}

	public void setSessionList(List<ComboValueDTO> sessionList) {
		this.sessionList = sessionList;
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

	public BigDecimal getLeaveBalance() {
		return leaveBalance;
	}

	public void setLeaveBalance(BigDecimal leaveBalance) {
		this.leaveBalance = leaveBalance;
	}

	public BigDecimal getNoOfDays() {
		return noOfDays;
	}

	public void setNoOfDays(BigDecimal noOfDays) {
		this.noOfDays = noOfDays;
	}

	public Long getApplyToId() {
		return applyToId;
	}

	public void setApplyToId(Long applyToId) {
		this.applyToId = applyToId;
	}

	public String getApplyTo() {
		return applyTo;
	}

	public void setApplyTo(String applyTo) {
		this.applyTo = applyTo;
	}

	public String getApplyToEmail() {
		return applyToEmail;
	}

	public void setApplyToEmail(String applyToEmail) {
		this.applyToEmail = applyToEmail;
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

	public Long getLeaveReviewer2Id() {
		return leaveReviewer2Id;
	}

	public void setLeaveReviewer2Id(Long leaveReviewer2Id) {
		this.leaveReviewer2Id = leaveReviewer2Id;
	}

	public String getLeaveReviewer3() {
		return leaveReviewer3;
	}

	public void setLeaveReviewer3(String leaveReviewer3) {
		this.leaveReviewer3 = leaveReviewer3;
	}

	public Long getLeaveReviewer3Id() {
		return leaveReviewer3Id;
	}

	public void setLeaveReviewer3Id(Long leaveReviewer3Id) {
		this.leaveReviewer3Id = leaveReviewer3Id;
	}

	public List<LeaveApplicationAttachmentDTO> getAttachmentList() {
		return attachmentList;
	}

	public void setAttachmentList(
			List<LeaveApplicationAttachmentDTO> attachmentList) {
		this.attachmentList = attachmentList;
	}

	public List<LeaveApplicationWorkflowDTO> getWorkflowList() {
		return workflowList;
	}

	public void setWorkflowList(List<LeaveApplicationWorkflowDTO> workflowList) {
		this.workflowList = workflowList;
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

	public Long getEmployeeLeaveSchemeTypeId() {
		return employeeLeaveSchemeTypeId;
	}

	public void setEmployeeLeaveSchemeTypeId(Long employeeLeaveSchemeTypeId) {
		this.employeeLeaveSchemeTypeId = employeeLeaveSchemeTypeId;
	}

	public List<LeaveCustomFieldDTO> getCustomFieldDTOList() {
		return customFieldDTOList;
	}

	public void setCustomFieldDTOList(
			List<LeaveCustomFieldDTO> customFieldDTOList) {
		this.customFieldDTOList = customFieldDTOList;
	}

	public EmployeeLeaveSchemeTypeResponse getEmployeeLeaveSchemeTypeInfo() {
		return employeeLeaveSchemeTypeInfo;
	}

	public void setEmployeeLeaveSchemeTypeInfo(
			EmployeeLeaveSchemeTypeResponse employeeLeaveSchemeTypeInfo) {
		this.employeeLeaveSchemeTypeInfo = employeeLeaveSchemeTypeInfo;
	}

	public String getEmailCC() {
		return emailCC;
	}

	public void setEmailCC(String emailCC) {
		this.emailCC = emailCC;
	}

	public String getInstruction() {
		return instruction;
	}

	public void setInstruction(String instruction) {
		this.instruction = instruction;
	}
	
	public boolean isLeaveUnitDays() {
		return isLeaveUnitDays;
	}

	public void setLeaveUnitDays(boolean isLeaveUnitDays) {
		this.isLeaveUnitDays = isLeaveUnitDays;
	}

}
