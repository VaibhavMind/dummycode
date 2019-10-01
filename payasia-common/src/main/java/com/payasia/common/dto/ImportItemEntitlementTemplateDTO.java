package com.payasia.common.dto;

import java.sql.Timestamp;

public class ImportItemEntitlementTemplateDTO {
	private String employeeName;
	private String employeeNumber;
	private String claimTemplateName;
	private Long claimTemplateId;
	private String claimTemplateitemName;
	private Long claimTemplateItemId;
	private boolean forfeitAtEndDate;
	private String amount;
	private String reason;
	private Timestamp fromDate;
	private Timestamp toDate;
	private String fromDateString;
	private String toDateString;
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
	public String getClaimTemplateName() {
		return claimTemplateName;
	}
	public void setClaimTemplateName(String claimTemplateName) {
		this.claimTemplateName = claimTemplateName;
	}
	public Long getClaimTemplateId() {
		return claimTemplateId;
	}
	public void setClaimTemplateId(Long claimTemplateId) {
		this.claimTemplateId = claimTemplateId;
	}
	public String getClaimTemplateitemName() {
		return claimTemplateitemName;
	}
	public void setClaimTemplateitemName(String claimTemplateitemName) {
		this.claimTemplateitemName = claimTemplateitemName;
	}
	public Long getClaimTemplateItemId() {
		return claimTemplateItemId;
	}
	public void setClaimTemplateItemId(Long claimTemplateItemId) {
		this.claimTemplateItemId = claimTemplateItemId;
	}
	public boolean isForfeitAtEndDate() {
		return forfeitAtEndDate;
	}
	public void setForfeitAtEndDate(boolean forfeitAtEndDate) {
		this.forfeitAtEndDate = forfeitAtEndDate;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public Timestamp getFromDate() {
		return fromDate;
	}
	public void setFromDate(Timestamp fromDate) {
		this.fromDate = fromDate;
	}
	public Timestamp getToDate() {
		return toDate;
	}
	public void setToDate(Timestamp toDate) {
		this.toDate = toDate;
	}
	public String getFromDateString() {
		return fromDateString;
	}
	public void setFromDateString(String fromDateString) {
		this.fromDateString = fromDateString;
	}
	public String getToDateString() {
		return toDateString;
	}
	public void setToDateString(String toDateString) {
		this.toDateString = toDateString;
	}
	
	
	

}
