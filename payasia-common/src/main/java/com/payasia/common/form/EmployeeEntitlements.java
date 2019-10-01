package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

import com.payasia.common.dto.EmployeeEntitlementDTO;

public class EmployeeEntitlements extends PageResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1151141397065913083L;
	
	private List<EmployeeEntitlementDTO> employeeEntitlements;

	public List<EmployeeEntitlementDTO> getEmployeeEntitlements() {
		return employeeEntitlements;
	}

	public void setEmployeeEntitlements(
			List<EmployeeEntitlementDTO> employeeEntitlements) {
		this.employeeEntitlements = employeeEntitlements;
	}

	
	
	

}
