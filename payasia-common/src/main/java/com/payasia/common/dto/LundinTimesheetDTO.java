package com.payasia.common.dto;

import java.io.Serializable;


public class LundinTimesheetDTO implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2087940976732266567L;

	private long timesheetId;

	private long companyId;

	private long employeeId;
	private String employeeNumber;
	private String employeeName;
	private String departmentCode;
	private String departmentDesc;
//	private LundinOTBatchDTO lundinBatchDTO;
	private long lundinBatchId;
	private LundinTimesheetStatusDTO lundinStatusDto;

	public long getTimesheetId() {
		return timesheetId;
	}

	public void setTimesheetId(long timesheetId) {
		this.timesheetId = timesheetId;
	}

	public long getLundinBatchId() {
		return lundinBatchId;
	}

	public void setLundinBatchId(long lundinBatchId) {
		this.lundinBatchId = lundinBatchId;
	}

	public long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(long companyId) {
		this.companyId = companyId;
	}

	public long getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(long employeeId) {
		this.employeeId = employeeId;
	}

//	public LundinOTBatchDTO getLundinBatchDTO() {
//		return lundinBatchDTO;
//	}
//
//	public void setLundinBatchDTO(LundinOTBatchDTO lundinBatchDTO) {
//		this.lundinBatchDTO = lundinBatchDTO;
//	}

	public LundinTimesheetStatusDTO getLundinStatusDto() {
		return lundinStatusDto;
	}

	public void setLundinStatusDto(LundinTimesheetStatusDTO lundinStatusDto) {
		this.lundinStatusDto = lundinStatusDto;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	private String remarks;

	public String getDepartmentCode() {
		return departmentCode;
	}

	public void setDepartmentCode(String departmentCode) {
		this.departmentCode = departmentCode;
	}

	public String getDepartmentDesc() {
		return departmentDesc;
	}

	public void setDepartmentDesc(String departmentDesc) {
		this.departmentDesc = departmentDesc;
	}

	public String getEmployeeNumber() {
		return employeeNumber;
	}

	public void setEmployeeNumber(String employeeNumber) {
		this.employeeNumber = employeeNumber;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	

//	private Set<LundinTimesheetReviewer> lundinTimesheetReviewers;

//	private Set<LundinTimesheetWorkflow> lundinTimesheetWorkflows;

//	private Set<LundinTimesheetDetail> lundinTimesheetDetails;

}
