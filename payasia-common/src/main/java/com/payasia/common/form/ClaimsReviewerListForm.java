package com.payasia.common.form;

import java.io.Serializable;

public class ClaimsReviewerListForm implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5910114349618624354L;
	private String template;
	private String claimReviewer1;
	private String claimReviewer2;
	private String claimReviewer3;

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
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

}
