package com.payasia.common.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class ClaimDetailsReportDataDTO implements Serializable {
	private static final long serialVersionUID = 6083504707284690235L;
	private int serialNum;
	private Long employeeId;
	private Long categoryId;
	private String categoryName;
	private String employeeNo;
	private String employeeName;
	private String firstName;
	private String lastName;
	private String claimTemplateName;
	private Long claimNumber;
	private String claimDate;
	private String claimItemName;
	private String accountCode;
	private String convClaimAmount;
	private String convClaimAmountCurrency;
	private String amountBeforeTax;
	private String amountBeforeTaxCurrency;
	private String convTaxAmount;
	private String convTaxAmountCurrency;
	private String remarks;
	private String block;
	private String afe;
	private String paid;
	private String amountApplicable;
	private String claimCreatedDate;
	
	private String claimSubmittedDate;
	private String claimApprovedForwardedDate;
	private String claimApprovedForwarded2Date;
	private String claimApprovedDate;
	private String rejectedDate;
	
	private String companyCodes;
	
	private String exchangeRate;
	private String receiptAmount;
	private String receiptAmountCurrency;
	
	
	public String getReceiptAmount() {
		return receiptAmount;
	}
	public void setReceiptAmount(String receiptAmount) {
		this.receiptAmount = receiptAmount;
	}
	
	public String getClaimantName() {
		return claimantName;
	}
	public void setClaimantName(String claimantName) {
		this.claimantName = claimantName;
	}
	private String status;
	private String customFields;
	private String appliedDate;
	private String approvedDate;
	private String claimantName;
	private List<ClaimDetailsReportCustomDataDTO> reportCustomDataDTOs;
	
	private String customFieldHeaderName1 ; 
	private String customFieldValueName1 ;
	private String customFieldHeaderName2 ; 
	private String customFieldValueName2 ;
	private String customFieldHeaderName3 ; 
	private String customFieldValueName3 ;
	private String customFieldHeaderName4 ; 
	private String customFieldValueName4 ;
	private String customFieldHeaderName5 ; 
	private String customFieldValueName5 ;
	private String customFieldHeaderName6 ; 
	private String customFieldValueName6 ;
	private HashMap<String, String> custFieldMap;
	String custField1 ;
	String custField2 ;
	String custField3 ;
	String custField4 ;
	String custField5 ;
	String custField6 ;
	String custField7 ;
	String custField8 ;
	String custField9 ;
	String custField10 ;
	
	
	
	public String getCustomFields() {
		return customFields;
	}
	public void setCustomFields(String customFields) {
		this.customFields = customFields;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public Long getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}
	public String getEmployeeNo() {
		return employeeNo;
	}
	public void setEmployeeNo(String employeeNo) {
		this.employeeNo = employeeNo;
	}
	public String getEmployeeName() {
		return employeeName;
	}
	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}
	public String getClaimTemplateName() {
		return claimTemplateName;
	}
	public void setClaimTemplateName(String claimTemplateName) {
		this.claimTemplateName = claimTemplateName;
	}
	
	public String getClaimDate() {
		return claimDate;
	}
	public void setClaimDate(String claimDate) {
		this.claimDate = claimDate;
	}
	public String getClaimItemName() {
		return claimItemName;
	}
	public void setClaimItemName(String claimItemName) {
		this.claimItemName = claimItemName;
	}
	public String getAccountCode() {
		return accountCode;
	}
	public void setAccountCode(String accountCode) {
		this.accountCode = accountCode;
	}
	public String getConvClaimAmount() {
		return convClaimAmount;
	}
	public void setConvClaimAmount(String convClaimAmount) {
		this.convClaimAmount = convClaimAmount;
	}
	public String getConvClaimAmountCurrency() {
		return convClaimAmountCurrency;
	}
	public void setConvClaimAmountCurrency(String convClaimAmountCurrency) {
		this.convClaimAmountCurrency = convClaimAmountCurrency;
	}
	public String getAmountBeforeTax() {
		return amountBeforeTax;
	}
	public void setAmountBeforeTax(String amountBeforeTax) {
		this.amountBeforeTax = amountBeforeTax;
	}
	public String getConvTaxAmount() {
		return convTaxAmount;
	}
	public void setConvTaxAmount(String convTaxAmount) {
		this.convTaxAmount = convTaxAmount;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getAmountBeforeTaxCurrency() {
		return amountBeforeTaxCurrency;
	}
	public void setAmountBeforeTaxCurrency(String amountBeforeTaxCurrency) {
		this.amountBeforeTaxCurrency = amountBeforeTaxCurrency;
	}
	public String getConvTaxAmountCurrency() {
		return convTaxAmountCurrency;
	}
	public void setConvTaxAmountCurrency(String convTaxAmountCurrency) {
		this.convTaxAmountCurrency = convTaxAmountCurrency;
	}
	public String getAppliedDate() {
		return appliedDate;
	}
	public void setAppliedDate(String appliedDate) {
		this.appliedDate = appliedDate;
	}
	public String getApprovedDate() {
		return approvedDate;
	}
	public void setApprovedDate(String approvedDate) {
		this.approvedDate = approvedDate;
	}
	public int getSerialNum() {
		return serialNum;
	}
	public void setSerialNum(int serialNum) {
		this.serialNum = serialNum;
	}
	public Long getClaimNumber() {
		return claimNumber;
	}
	public void setClaimNumber(Long claimNumber) {
		this.claimNumber = claimNumber;
	}
	public List<ClaimDetailsReportCustomDataDTO> getReportCustomDataDTOs() {
		return reportCustomDataDTOs;
	}
	public void setReportCustomDataDTOs(List<ClaimDetailsReportCustomDataDTO> reportCustomDataDTOs) {
		this.reportCustomDataDTOs = reportCustomDataDTOs;
	}
	public String getCustomFieldHeaderName1() {
		return customFieldHeaderName1;
	}
	public void setCustomFieldHeaderName1(String customFieldHeaderName1) {
		this.customFieldHeaderName1 = customFieldHeaderName1;
	}
	public String getCustomFieldValueName1() {
		return customFieldValueName1;
	}
	public void setCustomFieldValueName1(String customFieldValueName1) {
		this.customFieldValueName1 = customFieldValueName1;
	}
	public String getCustomFieldHeaderName2() {
		return customFieldHeaderName2;
	}
	public void setCustomFieldHeaderName2(String customFieldHeaderName2) {
		this.customFieldHeaderName2 = customFieldHeaderName2;
	}
	public String getCustomFieldValueName2() {
		return customFieldValueName2;
	}
	public void setCustomFieldValueName2(String customFieldValueName2) {
		this.customFieldValueName2 = customFieldValueName2;
	}
	public String getCustomFieldHeaderName3() {
		return customFieldHeaderName3;
	}
	public void setCustomFieldHeaderName3(String customFieldHeaderName3) {
		this.customFieldHeaderName3 = customFieldHeaderName3;
	}
	public String getCustomFieldValueName3() {
		return customFieldValueName3;
	}
	public void setCustomFieldValueName3(String customFieldValueName3) {
		this.customFieldValueName3 = customFieldValueName3;
	}
	public String getCustomFieldHeaderName4() {
		return customFieldHeaderName4;
	}
	public void setCustomFieldHeaderName4(String customFieldHeaderName4) {
		this.customFieldHeaderName4 = customFieldHeaderName4;
	}
	public String getCustomFieldValueName4() {
		return customFieldValueName4;
	}
	public void setCustomFieldValueName4(String customFieldValueName4) {
		this.customFieldValueName4 = customFieldValueName4;
	}
	public String getCustomFieldHeaderName5() {
		return customFieldHeaderName5;
	}
	public void setCustomFieldHeaderName5(String customFieldHeaderName5) {
		this.customFieldHeaderName5 = customFieldHeaderName5;
	}
	public String getCustomFieldValueName5() {
		return customFieldValueName5;
	}
	public void setCustomFieldValueName5(String customFieldValueName5) {
		this.customFieldValueName5 = customFieldValueName5;
	}
	public String getCustomFieldHeaderName6() {
		return customFieldHeaderName6;
	}
	public void setCustomFieldHeaderName6(String customFieldHeaderName6) {
		this.customFieldHeaderName6 = customFieldHeaderName6;
	}
	public String getCustomFieldValueName6() {
		return customFieldValueName6;
	}
	public void setCustomFieldValueName6(String customFieldValueName6) {
		this.customFieldValueName6 = customFieldValueName6;
	}
	public HashMap<String, String> getCustFieldMap() {
		return custFieldMap;
	}
	public void setCustFieldMap(HashMap<String, String> custFieldMap) {
		this.custFieldMap = custFieldMap;
	}
	public String getCustField1() {
		return custField1;
	}
	public void setCustField1(String custField1) {
		this.custField1 = custField1;
	}
	public String getCustField2() {
		return custField2;
	}
	public void setCustField2(String custField2) {
		this.custField2 = custField2;
	}
	public String getCustField3() {
		return custField3;
	}
	public void setCustField3(String custField3) {
		this.custField3 = custField3;
	}
	public String getCustField4() {
		return custField4;
	}
	public void setCustField4(String custField4) {
		this.custField4 = custField4;
	}
	public String getCustField5() {
		return custField5;
	}
	public void setCustField5(String custField5) {
		this.custField5 = custField5;
	}
	public String getCustField6() {
		return custField6;
	}
	public void setCustField6(String custField6) {
		this.custField6 = custField6;
	}
	public String getCustField7() {
		return custField7;
	}
	public void setCustField7(String custField7) {
		this.custField7 = custField7;
	}
	public String getCustField8() {
		return custField8;
	}
	public void setCustField8(String custField8) {
		this.custField8 = custField8;
	}
	public String getCustField9() {
		return custField9;
	}
	public void setCustField9(String custField9) {
		this.custField9 = custField9;
	}
	public String getCustField10() {
		return custField10;
	}
	public void setCustField10(String custField10) {
		this.custField10 = custField10;
	}
	public String getBlock() {
		return block;
	}
	public void setBlock(String block) {
		this.block = block;
	}
	public String getAfe() {
		return afe;
	}
	public void setAfe(String afe) {
		this.afe = afe;
	}
	
	public Long getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public String getPaid() {
		return paid;
	}
	public void setPaid(String paid) {
		this.paid = paid;
	}
	public String getAmountApplicable() {
		return amountApplicable;
	}
	public void setAmountApplicable(String amountApplicable) {
		this.amountApplicable = amountApplicable;
	}
	
	public String getClaimCreatedDate() {
		return claimCreatedDate;
	}
	public void setClaimCreatedDate(String claimCreatedDate) {
		this.claimCreatedDate = claimCreatedDate;
	}
	
	public String getCompanyCodes() {
		return companyCodes;
	}
	public void setCompanyCodes(String companyCodes) {
		this.companyCodes = companyCodes;
	}
	public String getExchangeRate() {
		return exchangeRate;
	}
	public void setExchangeRate(String exchangeRate) {
		this.exchangeRate = exchangeRate;
	}
	
	public String getReceiptAmountCurrency() {
		return receiptAmountCurrency;
	}
	public void setReceiptAmountCurrency(String receiptAmountCurrency) {
		this.receiptAmountCurrency = receiptAmountCurrency;
	}
	public String getClaimSubmittedDate() {
		return claimSubmittedDate;
	}
	public void setClaimSubmittedDate(String claimSubmittedDate) {
		this.claimSubmittedDate = claimSubmittedDate;
	}
	public String getClaimApprovedForwardedDate() {
		return claimApprovedForwardedDate;
	}
	public void setClaimApprovedForwardedDate(String claimApprovedForwardedDate) {
		this.claimApprovedForwardedDate = claimApprovedForwardedDate;
	}
	public String getClaimApprovedForwarded2Date() {
		return claimApprovedForwarded2Date;
	}
	public void setClaimApprovedForwarded2Date(String claimApprovedForwarded2Date) {
		this.claimApprovedForwarded2Date = claimApprovedForwarded2Date;
	}
	public String getClaimApprovedDate() {
		return claimApprovedDate;
	}
	public void setClaimApprovedDate(String claimApprovedDate) {
		this.claimApprovedDate = claimApprovedDate;
	}
	public String getRejectedDate() {
		return rejectedDate;
	}
	public void setRejectedDate(String rejectedDate) {
		this.rejectedDate = rejectedDate;
	}


}
