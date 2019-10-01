package com.payasia.common.form;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.payasia.common.dto.LeaveTypeDTO;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LeaveTypeForm implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long leaveTypeId;
	private Long employeeLeaveSchemeTypeId;
	private String name;
	private String code;
	private String accountCode;
	private Boolean visibleOrHidden;
	private Boolean frontEndAppMode;
	private Long frontEndViewModeId;
	private Boolean backEndAppMode;
	private Long backEndViewModeId;
	private Boolean frontEndAppModeEdit;
	private Long frontEndViewModeIdEdit;
	private Boolean backEndAppModeEdit;
	private Long backEndViewModeIdEdit;
	private String status;
	private String instruction;
	private String deletedMsg;
	private Long applicationMode;
	private String applicationModeName;
	private String leaveShemeCount;
	private Long typeId;
	private Long leaveSchemeTypeId;
	private String leaveType;
	private String leaveScheme;
	private LeaveTypeDTO leaveTypeDTO;
	private String leaveTypeSingaporean;
	



	public Boolean getFrontEndAppModeEdit() {
		return frontEndAppModeEdit;
	}

	public void setFrontEndAppModeEdit(Boolean frontEndAppModeEdit) {
		this.frontEndAppModeEdit = frontEndAppModeEdit;
	}

	public Boolean getFrontEndAppMode() {
		return frontEndAppMode;
	}

	public void setFrontEndAppMode(Boolean frontEndAppMode) {
		this.frontEndAppMode = frontEndAppMode;
	}

	public Long getFrontEndViewModeId() {
		return frontEndViewModeId;
	}

	public void setFrontEndViewModeId(Long frontEndViewModeId) {
		this.frontEndViewModeId = frontEndViewModeId;
	}

	public Boolean getBackEndAppMode() {
		return backEndAppMode;
	}

	public void setBackEndAppMode(Boolean backEndAppMode) {
		this.backEndAppMode = backEndAppMode;
	}

	public Long getBackEndViewModeId() {
		return backEndViewModeId;
	}

	public void setBackEndViewModeId(Long backEndViewModeId) {
		this.backEndViewModeId = backEndViewModeId;
	}

	public Long getFrontEndViewModeIdEdit() {
		return frontEndViewModeIdEdit;
	}

	public void setFrontEndViewModeIdEdit(Long frontEndViewModeIdEdit) {
		this.frontEndViewModeIdEdit = frontEndViewModeIdEdit;
	}

	public Boolean getBackEndAppModeEdit() {
		return backEndAppModeEdit;
	}

	public void setBackEndAppModeEdit(Boolean backEndAppModeEdit) {
		this.backEndAppModeEdit = backEndAppModeEdit;
	}

	public Long getBackEndViewModeIdEdit() {
		return backEndViewModeIdEdit;
	}

	public void setBackEndViewModeIdEdit(Long backEndViewModeIdEdit) {
		this.backEndViewModeIdEdit = backEndViewModeIdEdit;
	}

	public LeaveTypeDTO getLeaveTypeDTO() {
		return leaveTypeDTO;
	}

	public void setLeaveTypeDTO(LeaveTypeDTO leaveTypeDTO) {
		this.leaveTypeDTO = leaveTypeDTO;
	}

	public Long getLeaveSchemeTypeId() {
		return leaveSchemeTypeId;
	}

	public void setLeaveSchemeTypeId(Long leaveSchemeTypeId) {
		this.leaveSchemeTypeId = leaveSchemeTypeId;
	}

	public String getLeaveType() {
		return leaveType;
	}

	public void setLeaveType(String leaveType) {
		this.leaveType = leaveType;
	}

	public String getLeaveScheme() {
		return leaveScheme;
	}

	public void setLeaveScheme(String leaveScheme) {
		this.leaveScheme = leaveScheme;
	}

	public Long getEmployeeLeaveSchemeTypeId() {
		return employeeLeaveSchemeTypeId;
	}

	public void setEmployeeLeaveSchemeTypeId(Long employeeLeaveSchemeTypeId) {
		this.employeeLeaveSchemeTypeId = employeeLeaveSchemeTypeId;
	}

	public Long getTypeId() {
		return typeId;
	}

	public void setTypeId(Long typeId) {
		this.typeId = typeId;
	}

	public String getLeaveShemeCount() {
		return leaveShemeCount;
	}

	public void setLeaveShemeCount(String leaveShemeCount) {
		this.leaveShemeCount = leaveShemeCount;
	}

	public String getApplicationModeName() {
		return applicationModeName;
	}

	public void setApplicationModeName(String applicationModeName) {
		this.applicationModeName = applicationModeName;
	}

	public Long getApplicationMode() {
		return applicationMode;
	}

	public void setApplicationMode(Long applicationMode) {
		this.applicationMode = applicationMode;
	}

	public String getName() {
		
		
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getAccountCode() {
		return accountCode;
	}

	public void setAccountCode(String accountCode) {
		this.accountCode = accountCode;
	}

	public String getInstruction() {
		return instruction;
	}

	public void setInstruction(String instruction) {
		this.instruction = instruction;
	}


	public Long getLeaveTypeId() {
		return leaveTypeId;
	}

	public void setLeaveTypeId(Long leaveTypeId) {
		this.leaveTypeId = leaveTypeId;
	}

	public String getDeletedMsg() {
		return deletedMsg;
	}

	public void setDeletedMsg(String deletedMsg) {
		this.deletedMsg = deletedMsg;
	}


	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Boolean getVisibleOrHidden() {
		return visibleOrHidden;
	}

	public void setVisibleOrHidden(Boolean visibleOrHidden) {
		this.visibleOrHidden = visibleOrHidden;
	}

	public String getLeaveTypeSingaporean() {
		return leaveTypeSingaporean;
	}

	public void setLeaveTypeSingaporean(String leaveTypeSingaporean) {
		this.leaveTypeSingaporean = leaveTypeSingaporean;
	}

	


}
