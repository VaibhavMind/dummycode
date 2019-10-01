package com.payasia.common.dto;

import java.io.Serializable;
import java.util.List;

public class EmployeeClaimAdjustmentReportDTO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4620364866943221087L;
	private List<EmployeeClaimAdjustmentsHeaderDTO> employeeClaimAdjustmentsHeaderDTOs;
	private List<EmployeeClaimAdjustmentDTO> employeeClaimAdjustmentDTOs;
	
	
	public List<EmployeeClaimAdjustmentDTO> getEmployeeClaimAdjustmentDTOs() {
		return employeeClaimAdjustmentDTOs;
	}
	public void setEmployeeClaimAdjustmentDTOs(
			List<EmployeeClaimAdjustmentDTO> employeeClaimAdjustmentDTOs) {
		this.employeeClaimAdjustmentDTOs = employeeClaimAdjustmentDTOs;
	}
	public List<EmployeeClaimAdjustmentsHeaderDTO> getEmployeeClaimAdjustmentsHeaderDTOs() {
		return employeeClaimAdjustmentsHeaderDTOs;
	}
	public void setEmployeeClaimAdjustmentsHeaderDTOs(
			List<EmployeeClaimAdjustmentsHeaderDTO> employeeClaimAdjustmentsHeaderDTOs) {
		this.employeeClaimAdjustmentsHeaderDTOs = employeeClaimAdjustmentsHeaderDTOs;
	}
	
	
	
	

}
