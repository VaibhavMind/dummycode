package com.payasia.common.dto;

import java.io.Serializable;
import java.util.HashMap;

public class ClaimTemplateItemsWSResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5328135393813557053L;
	private Integer totalNoOfReviewers;
	private String applyTo;
	private String claimReviewer1;
	private String claimReviewer2;
	private String claimReviewer3;
	private Long claimReviewer1Id;
	private Long claimReviewer2Id;
	private Long claimReviewer3Id;
	private HashMap<Long, ClaimApplicationItemDTO> claimApplicationItems;
	public Integer getTotalNoOfReviewers() {
		return totalNoOfReviewers;
	}
	public void setTotalNoOfReviewers(Integer totalNoOfReviewers) {
		this.totalNoOfReviewers = totalNoOfReviewers;
	}
	public String getApplyTo() {
		return applyTo;
	}
	public void setApplyTo(String applyTo) {
		this.applyTo = applyTo;
	}
	public String getClaimReviewer1() {
		return claimReviewer1;
	}
	public void setClaimReviewer1(String claimReviewer1) {
		this.claimReviewer1 = claimReviewer1;
	}
	public String getClaimReviewer2() {
		return claimReviewer2;
	}
	public void setClaimReviewer2(String claimReviewer2) {
		this.claimReviewer2 = claimReviewer2;
	}
	public String getClaimReviewer3() {
		return claimReviewer3;
	}
	public void setClaimReviewer3(String claimReviewer3) {
		this.claimReviewer3 = claimReviewer3;
	}
	public Long getClaimReviewer1Id() {
		return claimReviewer1Id;
	}
	public void setClaimReviewer1Id(Long claimReviewer1Id) {
		this.claimReviewer1Id = claimReviewer1Id;
	}
	public Long getClaimReviewer2Id() {
		return claimReviewer2Id;
	}
	public void setClaimReviewer2Id(Long claimReviewer2Id) {
		this.claimReviewer2Id = claimReviewer2Id;
	}
	public Long getClaimReviewer3Id() {
		return claimReviewer3Id;
	}
	public void setClaimReviewer3Id(Long claimReviewer3Id) {
		this.claimReviewer3Id = claimReviewer3Id;
	}
	public HashMap<Long, ClaimApplicationItemDTO> getClaimApplicationItems() {
		return claimApplicationItems;
	}
	public void setClaimApplicationItems(
			HashMap<Long, ClaimApplicationItemDTO> claimApplicationItems) {
		this.claimApplicationItems = claimApplicationItems;
	}
	

}
