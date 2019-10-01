package com.payasia.common.dto;

import java.io.Serializable;
import java.util.List;

import com.payasia.common.form.PendingItemsForm;

public class PendingLeaveRes implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8070114506702060997L;
	private List<PendingItemsForm> reviewLeaveList;
	public List<PendingItemsForm> getReviewLeaveList() {
		return reviewLeaveList;
	}
	public void setReviewLeaveList(List<PendingItemsForm> reviewLeaveList) {
		this.reviewLeaveList = reviewLeaveList;
	}
	
	

}
