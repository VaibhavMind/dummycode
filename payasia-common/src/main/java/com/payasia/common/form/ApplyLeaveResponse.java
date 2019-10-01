package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

import com.payasia.common.dto.ComboValueDTO;

public class ApplyLeaveResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long employeeLeaveSchemeId;
	private String leaveSchemName;
	private List<LeaveTypeForm> employeeLeaveSchemeTypes;
	private List<ComboValueDTO> sessionList;
	private boolean isLeaveUnitDays;

	
	
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
	public boolean isLeaveUnitDays() {
		return isLeaveUnitDays;
	}
	public void setLeaveUnitDays(boolean isLeaveUnitDays) {
		this.isLeaveUnitDays = isLeaveUnitDays;
	}
	
	
	
	
	
	
	

}
