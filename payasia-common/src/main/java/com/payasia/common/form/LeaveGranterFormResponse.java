package com.payasia.common.form;

import java.util.List;
import java.util.Set;

public class LeaveGranterFormResponse extends PageResponse{
	
	private List<LeaveGranterForm> leaveGranterFormList;
	private Set<LeaveGranterForm> leaveGranterFormSet;
	private List<LeaveTypeForm> leaveTypes; 
	private Boolean Status;
	
	
	
	
	

	public Boolean getStatus() {
		return Status;
	}

	public void setStatus(Boolean status) {
		Status = status;
	}

	public List<LeaveTypeForm> getLeaveTypes() {
		return leaveTypes;
	}

	public void setLeaveTypes(List<LeaveTypeForm> leaveTypes) {
		this.leaveTypes = leaveTypes;
	}

	public List<LeaveGranterForm> getLeaveGranterFormList() {
		return leaveGranterFormList;
	}

	public void setLeaveGranterFormList(List<LeaveGranterForm> leaveGranterFormList) {
		this.leaveGranterFormList = leaveGranterFormList;
	}

	public Set<LeaveGranterForm> getLeaveGranterFormSet() {
		return leaveGranterFormSet;
	}

	public void setLeaveGranterFormSet(Set<LeaveGranterForm> leaveGranterFormSet) {
		this.leaveGranterFormSet = leaveGranterFormSet;
	}

}
