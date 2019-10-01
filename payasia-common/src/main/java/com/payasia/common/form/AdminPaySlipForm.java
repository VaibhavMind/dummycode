package com.payasia.common.form;

import java.io.Serializable;
import java.util.Arrays;

public class AdminPaySlipForm implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String employeeNumber;
	private Long payslipMonthId;
	private Integer payslipYear;
	private String paySlipFrequency;
	private Integer payslipPart;
	private Long employeeId;
	private String employeeName;
	private boolean isShortList ;
	private String metaData;
	private Long[] selectedEmployeeIds;

	
	
	public Long getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public String getEmployeeNumber() {
		return employeeNumber;
	}

	public void setEmployeeNumber(String employeeNumber) {
		this.employeeNumber = employeeNumber;
	}

	public Long getPayslipMonthId() {
		return payslipMonthId;
	}

	public void setPayslipMonthId(Long payslipMonthId) {
		this.payslipMonthId = payslipMonthId;
	}

	public Integer getPayslipYear() {
		return payslipYear;
	}

	public void setPayslipYear(Integer payslipYear) {
		this.payslipYear = payslipYear;
	}

	public String getPaySlipFrequency() {
		return paySlipFrequency;
	}

	public void setPaySlipFrequency(String paySlipFrequency) {
		this.paySlipFrequency = paySlipFrequency;
	}

	public Integer getPayslipPart() {
		return payslipPart;
	}

	public void setPayslipPart(Integer payslipPart) {
		this.payslipPart = payslipPart;
	}

	public boolean isShortList() {
		return isShortList;
	}

	public void setShortList(boolean isShortList) {
		this.isShortList = isShortList;
	}

	public String getMetaData() {
		return metaData;
	}

	public void setMetaData(String metaData) {
		this.metaData = metaData;
	}

	public Long[] getSelectedEmployeeIds() {
		return selectedEmployeeIds;
	}

	public void setSelectedEmployeeIds(Long[] selectedEmployeeIds) {
		if (selectedEmployeeIds != null) {
			this.selectedEmployeeIds = Arrays.copyOf(selectedEmployeeIds, selectedEmployeeIds.length);
		}
	}

}
