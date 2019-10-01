package com.payasia.common.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class EmployeeWiseTemplateClaimReportDataDTO implements Serializable {
	private static final long serialVersionUID = 6083504707284690235L;
	private Long employeeId;
	private Long claimItemId;
	private String employeeNo;
	private String excelEmployeeNumber;
	private String employeeName;
	private String firstName;
	private String lastName;
	private String claimTemplateName;
	private List<EmpWiseTemplateClaimedEntitlementDataDTO> empWiseTempClaimedEntitDataList;
	private int serialNum;
	private String claimItemName;
	private String entitlement;
	private String itemEntitlement;
	private String claimed;
	private String entitlementBalance;
	private String serialNumber;
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
	Boolean ClaimBalanceFromOtherCT;
	
	
	
	
	public Long getClaimItemId() {
		return claimItemId;
	}
	public void setClaimItemId(Long claimItemId) {
		this.claimItemId = claimItemId;
	}
	public String getItemEntitlement() {
		return itemEntitlement;
	}
	public void setItemEntitlement(String itemEntitlement) {
		this.itemEntitlement = itemEntitlement;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public String getExcelEmployeeNumber() {
		return excelEmployeeNumber;
	}
	public void setExcelEmployeeNumber(String excelEmployeeNumber) {
		this.excelEmployeeNumber = excelEmployeeNumber;
	}
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	public String getClaimItemName() {
		return claimItemName;
	}
	public void setClaimItemName(String claimItemName) {
		this.claimItemName = claimItemName;
	}
	public String getEntitlement() {
		return entitlement;
	}
	public void setEntitlement(String entitlement) {
		this.entitlement = entitlement;
	}
	public String getClaimed() {
		return claimed;
	}
	public void setClaimed(String claimed) {
		this.claimed = claimed;
	}
	public String getEntitlementBalance() {
		return entitlementBalance;
	}
	public void setEntitlementBalance(String entitlementBalance) {
		this.entitlementBalance = entitlementBalance;
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
	public String getClaimTemplateName() {
		return claimTemplateName;
	}
	public void setClaimTemplateName(String claimTemplateName) {
		this.claimTemplateName = claimTemplateName;
	}
	
	public int getSerialNum() {
		return serialNum;
	}
	public void setSerialNum(int serialNum) {
		this.serialNum = serialNum;
	}
	public List<EmpWiseTemplateClaimedEntitlementDataDTO> getEmpWiseTempClaimedEntitDataList() {
		return empWiseTempClaimedEntitDataList;
	}
	public void setEmpWiseTempClaimedEntitDataList(
			List<EmpWiseTemplateClaimedEntitlementDataDTO> empWiseTempClaimedEntitDataList) {
		this.empWiseTempClaimedEntitDataList = empWiseTempClaimedEntitDataList;
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
	public Boolean getClaimBalanceFromOtherCT() {
		return ClaimBalanceFromOtherCT;
	}
	public void setClaimBalanceFromOtherCT(Boolean claimBalanceFromOtherCT) {
		ClaimBalanceFromOtherCT = claimBalanceFromOtherCT;
	}
	
	
	

}
