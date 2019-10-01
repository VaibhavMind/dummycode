package com.payasia.common.dto;

import java.io.Serializable;
import java.util.List;


public class PayslipConditionDTO implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5833263780190256136L;

	private Long payslipId;

	private Integer part;

	private Integer year;

	private Long companyId;

	private Long employeeId;

	private Long monthMasterId;

	private Long payslipFrequencyId;
	
	private String employeeNumber;
	
	private String payslipType;
	
	private List<String> employeeNumberList;

	public Long getPayslipId() {
		return payslipId;
	}

	public void setPayslipId(Long payslipId) {
		this.payslipId = payslipId;
	}

	

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public Long getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}

	public Long getMonthMasterId() {
		return monthMasterId;
	}

	public void setMonthMasterId(Long monthMasterId) {
		this.monthMasterId = monthMasterId;
	}

	public Long getPayslipFrequencyId() {
		return payslipFrequencyId;
	}

	public void setPayslipFrequencyId(Long payslipFrequencyId) {
		this.payslipFrequencyId = payslipFrequencyId;
	}

	public Integer getPart() {
		return part;
	}

	public void setPart(Integer part) {
		this.part = part;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public String getEmployeeNumber() {
		return employeeNumber;
	}

	public void setEmployeeNumber(String employeeNumber) {
		this.employeeNumber = employeeNumber;
	}

	public String getPayslipType() {
		return payslipType;
	}

	public void setPayslipType(String payslipType) {
		this.payslipType = payslipType;
	}

	public List<String> getEmployeeNumberList() {
		return employeeNumberList;
	}

	public void setEmployeeNumberList(List<String> employeeNumberList) {
		this.employeeNumberList = employeeNumberList;
	}
	
	
	

}
