package com.payasia.common.form;


import java.io.Serializable;
import java.util.List;

public class PendingOTTimesheetResponseForm extends PageResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8840575696189273330L;

	private List<PendingOTTimesheetForm> pendingOTTimesheets;

	public List<PendingOTTimesheetForm> getPendingOTTimesheets() {
		return pendingOTTimesheets;
	}

	public void setPendingOTTimesheets(
			List<PendingOTTimesheetForm> pendingOTTimesheets) {
		this.pendingOTTimesheets = pendingOTTimesheets;
	}





}
