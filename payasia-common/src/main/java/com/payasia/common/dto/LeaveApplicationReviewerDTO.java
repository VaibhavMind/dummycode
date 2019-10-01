package com.payasia.common.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LeaveApplicationReviewerDTO implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2467254787184179587L;
	private Long leaveApplicationReviewer1Id;
	private Long leaveApplicationReviewer2Id;
	private Long leaveApplicationReviewer3Id;
	private String leaveApplicationReviewer1;
	private String leaveApplicationReviewer2;
	private String leaveApplicationReviewer3;
	private String ruleValue;
	
	
	private byte[] leaveApplicationReviewer1Image;
	private byte[] leaveApplicationReviewer2Image;
	private byte[] leaveApplicationReviewer3Image;
	
	public String getRuleValue() {
		return ruleValue;
	}
	public void setRuleValue(String ruleValue) {
		this.ruleValue = ruleValue;
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
	public byte[] getLeaveApplicationReviewer1Image() {
		return leaveApplicationReviewer1Image;
	}
	public void setLeaveApplicationReviewer1Image(byte[] leaveApplicationReviewer1Image) {
		this.leaveApplicationReviewer1Image = leaveApplicationReviewer1Image;
	}
	public byte[] getLeaveApplicationReviewer2Image() {
		return leaveApplicationReviewer2Image;
	}
	public void setLeaveApplicationReviewer2Image(byte[] leaveApplicationReviewer2Image) {
		this.leaveApplicationReviewer2Image = leaveApplicationReviewer2Image;
	}
	public byte[] getLeaveApplicationReviewer3Image() {
		return leaveApplicationReviewer3Image;
	}
	public void setLeaveApplicationReviewer3Image(byte[] leaveApplicationReviewer3Image) {
		this.leaveApplicationReviewer3Image = leaveApplicationReviewer3Image;
	}
	
	
	

}
