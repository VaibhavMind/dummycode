package com.payasia.common.form;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.payasia.common.dto.DataImportLogDTO;
import com.payasia.common.dto.EmployeeLeaveSchemeTypeHistoryDTO;
import com.payasia.common.dto.LeaveCalendarDTO;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LeaveBalanceSummaryForm {
    private long employeeLogInId;
	private int year;
	private String employeeNumber;
	private String employeeName;
	private Long employeeId;
	private String leaveScheme;
	private Long leaveSchemeId;
	private String leaveType;
	private Long leaveTypeId;
	private Long employeeLeaveSchemeId;
	private String responseString;
    private EmployeeLeaveSchemeTypeHistoryDTO employeeLeaveSchemeTypeHistory;
	
	private String holidayDate;
	private String holidayDesc;
	private int month;
	private int dayOfMonth;
	private String dayOfMonthStr;
	private String dayOfWeek;
	private HashMap<String, ArrayList<Long>> dateValueMap;
	private HashMap<String, ArrayList<LeaveCalendarDTO>> dateValueMapLeave;
	
	private Long applyToId;
	private String reason;
	private String leaveCC;
	private Long leaveApplicationId;
	private Boolean forfeitAtEndDate;
	private Long employeeLeaveSchemeTypeHistoryId;
	
	private boolean dataValid;
	private List<DataImportLogDTO> dataImportLogDTOs;
	private CommonsMultipartFile fileUpload;
	private boolean sameExcelField = false;
	private List<String> colName;
	private List<String> duplicateColNames;
	private List<HashMap<String, String>> importedData;
	private Long employeeLeaveSchemeTypeId;
	
	
	

	public long getEmployeeLogInId() {
		return employeeLogInId;
	}

	public void setEmployeeLogInId(long employeeLogInId) {
		this.employeeLogInId = employeeLogInId;
	}

	public boolean isSameExcelField() {
		return sameExcelField;
	}

	public void setSameExcelField(boolean sameExcelField) {
		this.sameExcelField = sameExcelField;
	}

	public List<String> getColName() {
		return colName;
	}

	public void setColName(List<String> colName) {
		this.colName = colName;
	}

	public List<String> getDuplicateColNames() {
		return duplicateColNames;
	}

	public void setDuplicateColNames(List<String> duplicateColNames) {
		this.duplicateColNames = duplicateColNames;
	}

	public List<HashMap<String, String>> getImportedData() {
		return importedData;
	}

	public void setImportedData(List<HashMap<String, String>> importedData) {
		this.importedData = importedData;
	}

	public Long getEmployeeLeaveSchemeTypeHistoryId() {
		return employeeLeaveSchemeTypeHistoryId;
	}

	public void setEmployeeLeaveSchemeTypeHistoryId(
			Long employeeLeaveSchemeTypeHistoryId) {
		this.employeeLeaveSchemeTypeHistoryId = employeeLeaveSchemeTypeHistoryId;
	}

	public Boolean getForfeitAtEndDate() {
		return forfeitAtEndDate;
	}

	public void setForfeitAtEndDate(Boolean forfeitAtEndDate) {
		this.forfeitAtEndDate = forfeitAtEndDate;
	}

	public Long getLeaveApplicationId() {
		return leaveApplicationId;
	}

	public void setLeaveApplicationId(Long leaveApplicationId) {
		this.leaveApplicationId = leaveApplicationId;
	}

	public Long getApplyToId() {
		return applyToId;
	}

	public void setApplyToId(Long applyToId) {
		this.applyToId = applyToId;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getLeaveCC() {
		return leaveCC;
	}

	public void setLeaveCC(String leaveCC) {
		this.leaveCC = leaveCC;
	}

	public String getLeaveType() {
		return leaveType;
	}

	public void setLeaveType(String leaveType) {
		this.leaveType = leaveType;
	}

	public Long getLeaveTypeId() {
		return leaveTypeId;
	}

	public void setLeaveTypeId(Long leaveTypeId) {
		this.leaveTypeId = leaveTypeId;
	}

	
	public String getHolidayDate() {
		return holidayDate;
	}

	public void setHolidayDate(String holidayDate) {
		this.holidayDate = holidayDate;
	}

	public String getHolidayDesc() {
		return holidayDesc;
	}

	public void setHolidayDesc(String holidayDesc) {
		this.holidayDesc = holidayDesc;
	}



	public String getDayOfWeek() {
		return dayOfWeek;
	}

	public void setDayOfWeek(String dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}

	public String getResponseString() {
		return responseString;
	}

	public void setResponseString(String responseString) {
		this.responseString = responseString;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public String getLeaveScheme() {
		return leaveScheme;
	}

	public void setLeaveScheme(String leaveScheme) {
		this.leaveScheme = leaveScheme;
	}

	public Long getEmployeeLeaveSchemeId() {
		return employeeLeaveSchemeId;
	}

	public void setEmployeeLeaveSchemeId(Long employeeLeaveSchemeId) {
		this.employeeLeaveSchemeId = employeeLeaveSchemeId;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public String getEmployeeNumber() {
		return employeeNumber;
	}

	public void setEmployeeNumber(String employeeNumber) {
		this.employeeNumber = employeeNumber;
	}

	public EmployeeLeaveSchemeTypeHistoryDTO getEmployeeLeaveSchemeTypeHistory() {
		return employeeLeaveSchemeTypeHistory;
	}

	public void setEmployeeLeaveSchemeTypeHistory(
			EmployeeLeaveSchemeTypeHistoryDTO employeeLeaveSchemeTypeHistory) {
		this.employeeLeaveSchemeTypeHistory = employeeLeaveSchemeTypeHistory;
	}

	public Long getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getDayOfMonth() {
		return dayOfMonth;
	}

	public void setDayOfMonth(int dayOfMonth) {
		this.dayOfMonth = dayOfMonth;
	}

	public HashMap<String, ArrayList<Long>> getDateValueMap() {
		return dateValueMap;
	}

	public void setDateValueMap(HashMap<String, ArrayList<Long>> dateValueMap) {
		this.dateValueMap = dateValueMap;
	}

	public Long getLeaveSchemeId() {
		return leaveSchemeId;
	}

	public void setLeaveSchemeId(Long leaveSchemeId) {
		this.leaveSchemeId = leaveSchemeId;
	}

	public boolean isDataValid() {
		return dataValid;
	}

	public void setDataValid(boolean dataValid) {
		this.dataValid = dataValid;
	}

	public List<DataImportLogDTO> getDataImportLogDTOs() {
		return dataImportLogDTOs;
	}

	public void setDataImportLogDTOs(List<DataImportLogDTO> dataImportLogDTOs) {
		this.dataImportLogDTOs = dataImportLogDTOs;
	}

	public CommonsMultipartFile getFileUpload() {
		return fileUpload;
	}

	public void setFileUpload(CommonsMultipartFile fileUpload) {
		this.fileUpload = fileUpload;
	}

	public String getDayOfMonthStr() {
		return dayOfMonthStr;
	}

	public void setDayOfMonthStr(String dayOfMonthStr) {
		this.dayOfMonthStr = dayOfMonthStr;
	}

	public Long getEmployeeLeaveSchemeTypeId() {
		return employeeLeaveSchemeTypeId;
	}

	public void setEmployeeLeaveSchemeTypeId(Long employeeLeaveSchemeTypeId) {
		this.employeeLeaveSchemeTypeId = employeeLeaveSchemeTypeId;
	}

	public HashMap<String, ArrayList<LeaveCalendarDTO>> getDateValueMapLeave() {
		return dateValueMapLeave;
	}

	public void setDateValueMapLeave(HashMap<String, ArrayList<LeaveCalendarDTO>> dateValueMapLeave) {
		this.dateValueMapLeave = dateValueMapLeave;
	}




}
