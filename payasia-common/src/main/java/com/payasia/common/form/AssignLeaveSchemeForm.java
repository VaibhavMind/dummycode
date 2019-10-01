package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.payasia.common.dto.DataImportLogDTO;
import com.payasia.common.dto.EmployeeLeaveSchemeTypeHistoryDTO;

public class AssignLeaveSchemeForm implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4873906814222319226L;
	private Long employeeLeaveSchemeId;
	private Long employeeId;
	private String employeeName;
	private String employeeNumber;
	private Long leaveSchemeId;
	private String leaveSchemeName;
	private Boolean active;
	private String status;
	private String createdDate;
	private String updatedDate;
	private EmployeeLeaveSchemeTypeHistoryDTO employeeLeaveSchemeTypeHistory;
	private boolean dataValid;
	private List<DataImportLogDTO> dataImportLogDTOs;
	

	private CommonsMultipartFile fileUpload;
	private String fromDate;
	private String toDate;
	private Integer year;
	
	
	public String getFromDate() {
		return fromDate;
	}
	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}
	public EmployeeLeaveSchemeTypeHistoryDTO getEmployeeLeaveSchemeTypeHistory() {
		return employeeLeaveSchemeTypeHistory;
	}
	public void setEmployeeLeaveSchemeTypeHistory(
			EmployeeLeaveSchemeTypeHistoryDTO employeeLeaveSchemeTypeHistory) {
		this.employeeLeaveSchemeTypeHistory = employeeLeaveSchemeTypeHistory;
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
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public Long getEmployeeLeaveSchemeId() {
		return employeeLeaveSchemeId;
	}
	public void setEmployeeLeaveSchemeId(Long employeeLeaveSchemeId) {
		this.employeeLeaveSchemeId = employeeLeaveSchemeId;
	}
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
	public Long getLeaveSchemeId() {
		return leaveSchemeId;
	}
	public void setLeaveSchemeId(Long leaveSchemeId) {
		this.leaveSchemeId = leaveSchemeId;
	}
	public String getLeaveSchemeName() {
		return leaveSchemeName;
	}
	public void setLeaveSchemeName(String leaveSchemeName) {
		this.leaveSchemeName = leaveSchemeName;
	}
	public Boolean getActive() {
		return active;
	}
	public void setActive(Boolean active) {
		this.active = active;
	}
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	public String getUpdatedDate() {
		return updatedDate;
	}
	public void setUpdatedDate(String updatedDate) {
		this.updatedDate = updatedDate;
	}
	public String getEmployeeNumber() {
		return employeeNumber;
	}
	public void setEmployeeNumber(String employeeNumber) {
		this.employeeNumber = employeeNumber;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getToDate() {
		return toDate;
	}
	public void setToDate(String toDate) {
		this.toDate = toDate;
	}
	public Integer getYear() {
		return year;
	}
	public void setYear(Integer year) {
		this.year = year;
	}
	
	
	

}
