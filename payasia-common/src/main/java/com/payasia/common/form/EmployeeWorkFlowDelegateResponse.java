package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

public class EmployeeWorkFlowDelegateResponse extends PageResponse implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8391681302568802599L;
	private List<WorkFlowDelegateForm> rows;

	public List<WorkFlowDelegateForm> getRows() {
		return rows;
	}

	public void setRows(List<WorkFlowDelegateForm> rows) {
		this.rows = rows;
	}

	
}
