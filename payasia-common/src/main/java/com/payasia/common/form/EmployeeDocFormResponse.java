package com.payasia.common.form;

import java.util.List;

public class EmployeeDocFormResponse extends PageResponse {

	private List<EmployeeDocumentCenterForm> rows;

	public List<EmployeeDocumentCenterForm> getRows() {
		return rows;
	}

	public void setRows(List<EmployeeDocumentCenterForm> companyDocumentCenterFormList) {
		this.rows = companyDocumentCenterFormList;
	}
	
	
}
