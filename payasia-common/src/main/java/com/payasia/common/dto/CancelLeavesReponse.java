package com.payasia.common.dto;

import java.io.Serializable;
import java.util.List;

public class CancelLeavesReponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3307445540864908519L;
	
	private List<CancelLeaveApplication> cancelLeaveApplications;

	public List<CancelLeaveApplication> getCancelLeaveApplications() {
		return cancelLeaveApplications;
	}

	public void setCancelLeaveApplications(
			List<CancelLeaveApplication> cancelLeaveApplications) {
		this.cancelLeaveApplications = cancelLeaveApplications;
	}

}
