package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

import com.payasia.common.dto.EmployeeClaimTemplateDataDTO;

public class EmployeeClaimTemplateDataResponse implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7193938732794110347L;

	private List<EmployeeClaimTemplateDataDTO> rows;

	public List<EmployeeClaimTemplateDataDTO> getRows() {
		return rows;
	}

	public void setRows(List<EmployeeClaimTemplateDataDTO> rows) {
		this.rows = rows;
	}
	
	

}
