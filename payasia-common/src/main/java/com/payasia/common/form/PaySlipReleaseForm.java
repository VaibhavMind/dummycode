package com.payasia.common.form;

import java.io.Serializable;


public class PaySlipReleaseForm implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long companyPayslipReleaseId;
	private String name;
	private Long payslipMonthId;
	private String monthName;
	private Integer payslipYear;
	private boolean release;
	private Integer payslipPart;
	private Long employeeId;
	private String employeeName;
	private String emailTo;
	private boolean sendMail;
	private String scheduleDate;
	private String scheduleTime;

	
	
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


	public Integer getPayslipPart() {
		return payslipPart;
	}

	public void setPayslipPart(Integer payslipPart) {
		this.payslipPart = payslipPart;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	
	

	public Long getCompanyPayslipReleaseId() {
		return companyPayslipReleaseId;
	}

	public void setCompanyPayslipReleaseId(Long companyPayslipReleaseId) {
		this.companyPayslipReleaseId = companyPayslipReleaseId;
	}

	public String getMonthName() {
		return monthName;
	}

	public void setMonthName(String monthName) {
		this.monthName = monthName;
	}

	public boolean isRelease() {
		return release;
	}

	public void setRelease(boolean release) {
		this.release = release;
	}

	public String getEmailTo() {
		return emailTo;
	}

	public void setEmailTo(String emailTo) {
		this.emailTo = emailTo;
	}

	public boolean isSendMail() {
		return sendMail;
	}

	public void setSendMail(boolean sendMail) {
		this.sendMail = sendMail;
	}



	public String getScheduleDate() {
		return scheduleDate;
	}

	public void setScheduleDate(String scheduleDate) {
		this.scheduleDate = scheduleDate;
	}

	public String getScheduleTime() {
		return scheduleTime;
	}

	public void setScheduleTime(String scheduleTime) {
		this.scheduleTime = scheduleTime;
	}

}
