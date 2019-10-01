package com.payasia.common.dto;

import java.math.BigInteger;
import java.util.List;

public class EmployeeShortListDTO {
	
	
	List<BigInteger> shortListEmployeeIds;
	List<String> shortListEmployeeNumbers;
	
	private Boolean employeeShortList;
	private Boolean status;
	
	private String lundinBatchEndDate;
	
	private Long searchEmployeeId;
	
	private String searchEmployeeNumber;
	
	
	
	public Boolean getEmployeeShortList() {
		return employeeShortList;
	}
	public void setEmployeeShortList(Boolean employeeShortList) {
		this.employeeShortList = employeeShortList;
	}
	public List<BigInteger> getShortListEmployeeIds() {
		return shortListEmployeeIds;
	}
	public void setShortListEmployeeIds(List<BigInteger> shortListEmployeeIds) {
		this.shortListEmployeeIds = shortListEmployeeIds;
	}
	public List<String> getShortListEmployeeNumbers() {
		return shortListEmployeeNumbers;
	}
	public void setShortListEmployeeNumbers(List<String> shortListEmployeeNumbers) {
		this.shortListEmployeeNumbers = shortListEmployeeNumbers;
	}
	public Boolean getStatus() {
		return status;
	}
	public void setStatus(Boolean status) {
		this.status = status;
	}
	public String getLundinBatchEndDate() {
		return lundinBatchEndDate;
	}
	public void setLundinBatchEndDate(String lundinBatchEndDate) {
		this.lundinBatchEndDate = lundinBatchEndDate;
	}
	
	public String getSearchEmployeeNumber() {
		return searchEmployeeNumber;
	}
	public void setSearchEmployeeNumber(String searchEmployeeNumber) {
		this.searchEmployeeNumber = searchEmployeeNumber;
	}
	public Long getSearchEmployeeId() {
		return searchEmployeeId;
	}
	public void setSearchEmployeeId(Long searchEmployeeId) {
		this.searchEmployeeId = searchEmployeeId;
	}
	
	
	
	
	

}
