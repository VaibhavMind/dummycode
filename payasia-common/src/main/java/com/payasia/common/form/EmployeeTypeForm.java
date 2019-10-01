package com.payasia.common.form;

import java.io.Serializable;


public class EmployeeTypeForm implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1853737845085650547L;

	private long empTypeId;

	private long companyId;
	
	private String empType;

	private String empTypeDesc;

	public long getEmpTypeId() {
		return empTypeId;
	}

	public void setEmpTypeId(long empTypeId) {
		this.empTypeId = empTypeId;
	}

	public long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(long companyId) {
		this.companyId = companyId;
	}

	public String getEmpType() {
		return empType;
	}

	public void setEmpType(String empType) {
		this.empType = empType;
	}

	public String getEmpTypeDesc() {
		return empTypeDesc;
	}

	public void setEmpTypeDesc(String empTypeDesc) {
		this.empTypeDesc = empTypeDesc;
	}
	
	
}
