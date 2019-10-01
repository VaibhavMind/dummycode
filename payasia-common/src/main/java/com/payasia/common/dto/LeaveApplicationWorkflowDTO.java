package com.payasia.common.dto;

import java.io.Serializable;
import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonInclude;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LeaveApplicationWorkflowDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 9220137938949228048L;
	private String employeeInfo;
	private String createdDate;
	private Timestamp createdDateM;
	private String status;
	private String leaveReviewerType;
	private String Remarks;
	private String userRemarks;
	private String leaveAppStatus;
	private Integer order;
	private long emoployeeInfoId;
	private String createdDateMStr;
    private byte[] leaveApplicationReviewerImage;
    
    
	public byte[] getLeaveApplicationReviewerImage() {
		return leaveApplicationReviewerImage;
	}
	public void setLeaveApplicationReviewerImage(byte[] leaveApplicationReviewerImage) {
		this.leaveApplicationReviewerImage = leaveApplicationReviewerImage;
	}
	public String getLeaveReviewerType() {
		return leaveReviewerType;
	}
	public void setLeaveReviewerType(String leaveReviewerType) {
		this.leaveReviewerType = leaveReviewerType;
	}
	public String getCreatedDateMStr() {
		return createdDateMStr;
	}
	public void setCreatedDateMStr(String createdDateMStr) {
		this.createdDateMStr = createdDateMStr;
	}
	public long getEmoployeeInfoId() {
		return emoployeeInfoId;
	}
	public void setEmoployeeInfoId(long emoployeeInfoId) {
		this.emoployeeInfoId = emoployeeInfoId;
	}
	public String getUserRemarks() {
		return userRemarks;
	}
	public void setUserRemarks(String userRemarks) {
		this.userRemarks = userRemarks;
	}
	public String getEmployeeInfo() {
		return employeeInfo;
	}
	public void setEmployeeInfo(String employeeInfo) {
		this.employeeInfo = employeeInfo;
	}
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getRemarks() {
		return Remarks;
	}
	public void setRemarks(String remarks) {
		Remarks = remarks;
	}
	public Integer getOrder() {
		return order;
	}
	public void setOrder(Integer order) {
		this.order = order;
	}
	public Timestamp getCreatedDateM() {
		return createdDateM;
	}
	public void setCreatedDateM(Timestamp createdDateM) {
		this.createdDateM = createdDateM;
	}
	
	
	
	
}
