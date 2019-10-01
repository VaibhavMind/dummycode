package com.payasia.common.dto;

import java.io.Serializable;

public class LeaveReviewFormDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6957923591269701892L;
	private Long leaveApplicationRevId;
	private Long leaveApplicationId;
	private Long forwardToId;
	private String leaveReviewAction;
	private String remarks;
	private boolean canApprove;
	private boolean canForward;
	
	

	public boolean isCanApprove() {
		return canApprove;
	}

	public void setCanApprove(boolean canApprove) {
		this.canApprove = canApprove;
	}

	public boolean isCanForward() {
		return canForward;
	}

	public void setCanForward(boolean canForward) {
		this.canForward = canForward;
	}

	public Long getLeaveApplicationRevId() {
		return leaveApplicationRevId;
	}

	public void setLeaveApplicationRevId(Long leaveApplicationRevId) {
		this.leaveApplicationRevId = leaveApplicationRevId;
	}

	public Long getLeaveApplicationId() {
		return leaveApplicationId;
	}

	public void setLeaveApplicationId(Long leaveApplicationId) {
		this.leaveApplicationId = leaveApplicationId;
	}

	public String getLeaveReviewAction() {
		return leaveReviewAction;
	}

	public void setLeaveReviewAction(String leaveReviewAction) {
		this.leaveReviewAction = leaveReviewAction;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Long getForwardToId() {
		return forwardToId;
	}

	public void setForwardToId(Long forwardToId) {
		this.forwardToId = forwardToId;
	}

}
