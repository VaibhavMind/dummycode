package com.payasia.common.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class CoherentShiftDetailReportDTO implements Serializable {
	private static final long serialVersionUID = 3544314311006552093L;
	
	
	
	
	private Long employeeId;
	private String employeeNumber;
	private String employeeName;
	private String firstName;
	private String lastName;
	private String departmentDesc;
	private String departmentCode;
	private String costCentre;
	private String location;
	private Long shiftApplicationId;
	private String updatedDate;
	public String getUpdatedDate() {
		return updatedDate;
	}
	public void setUpdatedDate(String updatedDate) {
		this.updatedDate = updatedDate;
	}
	public Long getShiftApplicationId() {
		return shiftApplicationId;
	}
	public void setShiftApplicationId(Long shiftApplicationId) {
		this.shiftApplicationId = shiftApplicationId;
	}
	public Long getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
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
	public String getDepartmentDesc() {
		return departmentDesc;
	}
	public void setDepartmentDesc(String departmentDesc) {
		this.departmentDesc = departmentDesc;
	}
	public String getDepartmentCode() {
		return departmentCode;
	}
	public void setDepartmentCode(String departmentCode) {
		this.departmentCode = departmentCode;
	}
	public String getCostCentre() {
		return costCentre;
	}
	public void setCostCentre(String costCentre) {
		this.costCentre = costCentre;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public String getShiftType() {
		return shiftType;
	}
	public void setShiftType(String shiftType) {
		this.shiftType = shiftType;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public Integer getNumberOfShift() {
		return numberOfShift;
	}
	public void setNumberOfShift(Integer numberOfShift) {
		this.numberOfShift = numberOfShift;
	}
	public int getTotalShift() {
		return totalShift;
	}
	public void setTotalShift(int totalShift) {
		this.totalShift = totalShift;
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
	public List<LeaveReportCustomDataDTO> getReportCustomDataDTOs() {
		return reportCustomDataDTOs;
	}
	public void setReportCustomDataDTOs(
			List<LeaveReportCustomDataDTO> reportCustomDataDTOs) {
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
	public String getFileBatchName() {
		return fileBatchName;
	}
	public void setFileBatchName(String fileBatchName) {
		this.fileBatchName = fileBatchName;
	}
	public String getFileFromBatchDate() {
		return fileFromBatchDate;
	}
	public void setFileFromBatchDate(String fileFromBatchDate) {
		this.fileFromBatchDate = fileFromBatchDate;
	}
	public String getFileToBatchDate() {
		return fileToBatchDate;
	}
	public void setFileToBatchDate(String fileToBatchDate) {
		this.fileToBatchDate = fileToBatchDate;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	private String month;
	private String shiftType;
	private String companyName;
	private Integer numberOfShift;
	private int totalShift;
	
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
	private List<LeaveReportCustomDataDTO> reportCustomDataDTOs;
	
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
	
	private String fileBatchName;
	private String fileFromBatchDate;
	private String fileToBatchDate;
}
