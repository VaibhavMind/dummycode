package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

public class LeaveTypeResponse extends PageResponse implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7122739778058515560L;
	private List<LeaveTypeForm> rows;

	public List<LeaveTypeForm> getRows() {
		return rows;
	}

	public void setRows(List<LeaveTypeForm> rows) {
		this.rows = rows;
	}

}
