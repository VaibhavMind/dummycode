package com.payasia.common.dto;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

public class AddLeaveConditionDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4345787790295874709L;
	private Long employeeId;
	private Long leaveStatusId;
	private Timestamp fromDate;
	private Timestamp toDate;
	private String leaveType;
	
	
	private String leaveTypeName;
	private String leaveReviewer1;
	private String leaveReviewer2;
	private String leaveReviewer3;
	private String createdDate;
	private String leaveAppfromDate;
	private String leaveApptoDate;
	private List<String> leaveStatusNames;
	private Long leaveApplicationId;
	private String companyDateFormat;
	
	
	
	
	public List<String> getLeaveStatusNames() {
		return leaveStatusNames;
	}
	public void setLeaveStatusNames(List<String> leaveStatusNames) {
		this.leaveStatusNames = leaveStatusNames;
	}
	public String getLeaveTypeName() {
		return leaveTypeName;
	}
	public void setLeaveTypeName(String leaveTypeName) {
		this.leaveTypeName = leaveTypeName;
	}
	public String getLeaveReviewer1() {
		return leaveReviewer1;
	}
	public void setLeaveReviewer1(String leaveReviewer1) {
		this.leaveReviewer1 = leaveReviewer1;
	}
	public String getLeaveReviewer2() {
		return leaveReviewer2;
	}
	public void setLeaveReviewer2(String leaveReviewer2) {
		this.leaveReviewer2 = leaveReviewer2;
	}
	public String getLeaveReviewer3() {
		return leaveReviewer3;
	}
	public void setLeaveReviewer3(String leaveReviewer3) {
		this.leaveReviewer3 = leaveReviewer3;
	}
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	public String getLeaveAppfromDate() {
		return leaveAppfromDate;
	}
	public void setLeaveAppfromDate(String leaveAppfromDate) {
		this.leaveAppfromDate = leaveAppfromDate;
	}
	public String getLeaveApptoDate() {
		return leaveApptoDate;
	}
	public void setLeaveApptoDate(String leaveApptoDate) {
		this.leaveApptoDate = leaveApptoDate;
	}
	public String getLeaveType() {
		return leaveType;
	}
	public void setLeaveType(String leaveType) {
		this.leaveType = leaveType;
	}
	public Long getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}
	public Long getLeaveStatusId() {
		return leaveStatusId;
	}
	public void setLeaveStatusId(Long leaveStatusId) {
		this.leaveStatusId = leaveStatusId;
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
	public Long getLeaveApplicationId() {
		return leaveApplicationId;
	}
	public void setLeaveApplicationId(Long leaveApplicationId) {
		this.leaveApplicationId = leaveApplicationId;
	}
	public String getCompanyDateFormat() {
		return companyDateFormat;
	}
	public void setCompanyDateFormat(String companyDateFormat) {
		this.companyDateFormat = companyDateFormat;
	}
	
	
		
}
