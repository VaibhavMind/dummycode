package com.payasia.common.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class PrintTokenDTO  implements Serializable{
	
	private Integer year;
	private Integer part;
	private Integer month;
	private List<Long> employeeIds;
	private Integer employeeCount;
	private String printTokenKey;
	private Date tokenTime;
	private Long loggedInEmployeeId;
	private Long loggedInCompanyId;
	
	
	
	
	public Long getLoggedInEmployeeId() {
		return loggedInEmployeeId;
	}
	public void setLoggedInEmployeeId(Long loggedInEmployeeId) {
		this.loggedInEmployeeId = loggedInEmployeeId;
	}
	public Long getLoggedInCompanyId() {
		return loggedInCompanyId;
	}
	public void setLoggedInCompanyId(Long loggedInCompanyId) {
		this.loggedInCompanyId = loggedInCompanyId;
	}
	public Date getTokenTime() {
		return tokenTime;
	}
	public void setTokenTime(Date tokenTime) {
		this.tokenTime = tokenTime;
	}
	public String getPrintTokenKey() {
		return printTokenKey;
	}
	public void setPrintTokenKey(String printTokenKey) {
		this.printTokenKey = printTokenKey;
	}
	public Integer getEmployeeCount() {
		return employeeCount;
	}
	public void setEmployeeCount(Integer employeeCount) {
		this.employeeCount = employeeCount;
	}
	public Integer getYear() {
		return year;
	}
	public void setYear(Integer year) {
		this.year = year;
	}
	public Integer getPart() {
		return part;
	}
	public void setPart(Integer part) {
		this.part = part;
	}
	public Integer getMonth() {
		return month;
	}
	public void setMonth(Integer month) {
		this.month = month;
	}
	public List<Long> getEmployeeIds() {
		return employeeIds;
	}
	public void setEmployeeIds(List<Long> employeeIds) {
		this.employeeIds = employeeIds;
	}
	
	
	
	
	
	

}
