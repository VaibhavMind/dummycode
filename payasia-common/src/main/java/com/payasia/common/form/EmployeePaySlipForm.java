package com.payasia.common.form;

import java.io.Serializable;

public class EmployeePaySlipForm implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long payslipMonthId;
	private Integer payslipYear;
	private String paySlipFrequency;
	private Integer payslipPart;

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

}
