package com.payasia.common.form;

import java.util.List;

public class HrisMyRequestFormResponse extends PageResponse {
	
	
	private List<HrisChangeRequestForm> rows;		//  hrisChangeRequestForms

	public List<HrisChangeRequestForm> getRows() {
		return rows;
	}

	public void setRows(List<HrisChangeRequestForm> rows) {
		this.rows = rows;
	}
	
	

}
