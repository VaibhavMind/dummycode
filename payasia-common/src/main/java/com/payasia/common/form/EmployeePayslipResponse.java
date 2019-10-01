package com.payasia.common.form;

import java.util.List;

import com.payasia.common.dto.PayslipDTO;

public class EmployeePayslipResponse extends PageResponse {

	private List<PayslipDTO> rows;
	
	public List<PayslipDTO> getRows() {
		return rows;
	}

	public void setRows(List<PayslipDTO> rows) {
		this.rows = rows;
	}

}
