package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import com.payasia.common.dto.LeaveSchemeProcDTO;

public class LeaveSchemeResponse extends PageResponse implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6853182805444176300L;

	private List<LeaveSchemeForm> rows;

	private Set<LeaveSchemeForm> leaveSchemeSet;
	
	private String messageKey ;
	
	private Boolean isEntitlementChanged;
	private Long leaveSchemetypeId;
	
	private LeaveSchemeProcDTO leaveSchemeProc;
	
	
	
	
	
	

	public LeaveSchemeProcDTO getLeaveSchemeProc() {
		return leaveSchemeProc;
	}

	public void setLeaveSchemeProc(LeaveSchemeProcDTO leaveSchemeProc) {
		this.leaveSchemeProc = leaveSchemeProc;
	}

	public Long getLeaveSchemetypeId() {
		return leaveSchemetypeId;
	}

	public void setLeaveSchemetypeId(Long leaveSchemetypeId) {
		this.leaveSchemetypeId = leaveSchemetypeId;
	}

	public Boolean getIsEntitlementChanged() {
		return isEntitlementChanged;
	}

	public void setIsEntitlementChanged(Boolean isEntitlementChanged) {
		this.isEntitlementChanged = isEntitlementChanged;
	}

	public String getMessageKey() {
		return messageKey;
	}

	public void setMessageKey(String messageKey) {
		this.messageKey = messageKey;
	}

	public List<LeaveSchemeForm> getRows() {
		return rows;
	}

	public void setRows(List<LeaveSchemeForm> rows) {
		this.rows = rows;
	}

	public Set<LeaveSchemeForm> getLeaveSchemeSet() {
		return leaveSchemeSet;
	}

	public void setLeaveSchemeSet(Set<LeaveSchemeForm> leaveSchemeSet) {
		this.leaveSchemeSet = leaveSchemeSet;
	}

}
