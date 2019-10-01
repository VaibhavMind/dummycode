package com.payasia.common.form;

import java.io.Serializable;
import java.math.BigDecimal;

public class PayDataCollectionForm implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 38785346698902092L;
	private Long payDataCollectionId;
	private Long employeeId;
	private String employeeName;
	private String employeeNumber;
	private Long payCodeId;
	private String payCode;
	private BigDecimal amount;
	private String fromDate;
	private String toDate;
	

	public Long getPayCodeId() {
		return payCodeId;
	}

	public void setPayCodeId(Long payCodeId) {
		this.payCodeId = payCodeId;
	}

	public String getPayCode() {
		return payCode;
	}

	public void setPayCode(String payCode) {
		this.payCode = payCode;
	}


	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
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

	public String getToDate() {
		return toDate;
	}

	public void setToDate(String toDate) {
		this.toDate = toDate;
	}

	public String getFromDate() {
		return fromDate;
	}

	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}

	public Long getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}

	public Long getPayDataCollectionId() {
		return payDataCollectionId;
	}

	public void setPayDataCollectionId(Long payDataCollectionId) {
		this.payDataCollectionId = payDataCollectionId;
	}


}
