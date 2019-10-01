package com.payasia.common.dto;

import java.io.Serializable;

public class ViewCancelLeaveResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4123752531801949285L;
	private Integer noOfReviewers;
	private Long leaveApplicationReviewer1Id;
	private Long leaveApplicationReviewer2Id;
	private Long leaveApplicationReviewer3Id;
	private String leaveApplicationReviewer1;
	private String leaveApplicationReviewer2;
	private String leaveApplicationReviewer3;
	private String employeeName;
	private String leaveScheme;
	private String leaveType;
	private String session1;
	private String session2;
	private String fromDate;
	private String toDate;
	private Long leaveApplicationId;
	private boolean isLeaveUnitDays;
	
	
	
	
	public Integer getNoOfReviewers() {
		return noOfReviewers;
	}
	public void setNoOfReviewers(Integer noOfReviewers) {
		this.noOfReviewers = noOfReviewers;
	}
	public Long getLeaveApplicationReviewer1Id() {
		return leaveApplicationReviewer1Id;
	}
	public void setLeaveApplicationReviewer1Id(Long leaveApplicationReviewer1Id) {
		this.leaveApplicationReviewer1Id = leaveApplicationReviewer1Id;
	}
	public Long getLeaveApplicationReviewer2Id() {
		return leaveApplicationReviewer2Id;
	}
	public void setLeaveApplicationReviewer2Id(Long leaveApplicationReviewer2Id) {
		this.leaveApplicationReviewer2Id = leaveApplicationReviewer2Id;
	}
	public Long getLeaveApplicationReviewer3Id() {
		return leaveApplicationReviewer3Id;
	}
	public void setLeaveApplicationReviewer3Id(Long leaveApplicationReviewer3Id) {
		this.leaveApplicationReviewer3Id = leaveApplicationReviewer3Id;
	}
	public String getLeaveApplicationReviewer1() {
		return leaveApplicationReviewer1;
	}
	public void setLeaveApplicationReviewer1(String leaveApplicationReviewer1) {
		this.leaveApplicationReviewer1 = leaveApplicationReviewer1;
	}
	public String getLeaveApplicationReviewer2() {
		return leaveApplicationReviewer2;
	}
	public void setLeaveApplicationReviewer2(String leaveApplicationReviewer2) {
		this.leaveApplicationReviewer2 = leaveApplicationReviewer2;
	}
	public String getLeaveApplicationReviewer3() {
		return leaveApplicationReviewer3;
	}
	public void setLeaveApplicationReviewer3(String leaveApplicationReviewer3) {
		this.leaveApplicationReviewer3 = leaveApplicationReviewer3;
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
	public String getLeaveType() {
		return leaveType;
	}
	public void setLeaveType(String leaveType) {
		this.leaveType = leaveType;
	}
	public String getSession1() {
		return session1;
	}
	public void setSession1(String session1) {
		this.session1 = session1;
	}
	public String getSession2() {
		return session2;
	}
	public void setSession2(String session2) {
		this.session2 = session2;
	}
	public String getFromDate() {
		return fromDate;
	}
	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}
	public String getToDate() {
		return toDate;
	}
	public void setToDate(String toDate) {
		this.toDate = toDate;
	}
	public Long getLeaveApplicationId() {
		return leaveApplicationId;
	}
	public void setLeaveApplicationId(Long leaveApplicationId) {
		this.leaveApplicationId = leaveApplicationId;
	}
	public boolean isLeaveUnitDays() {
		return isLeaveUnitDays;
	}
	public void setLeaveUnitDays(boolean isLeaveUnitDays) {
		this.isLeaveUnitDays = isLeaveUnitDays;
	}
	
	

}
