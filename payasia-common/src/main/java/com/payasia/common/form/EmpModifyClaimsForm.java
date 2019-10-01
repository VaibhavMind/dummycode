package com.payasia.common.form;

import java.io.Serializable;

public class EmpModifyClaimsForm implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7566091600425756367L;
	private String claimTemplate;
	private String date;
	private String noOfItems;
	private String totalAmount;
	private String empOTScheme;

	public String getEmpOTScheme() {
		return empOTScheme;
	}

	public void setEmpOTScheme(String empOTScheme) {
		this.empOTScheme = empOTScheme;
	}

	public String getClaimTemplate() {
		return claimTemplate;
	}

	public void setClaimTemplate(String claimTemplate) {
		this.claimTemplate = claimTemplate;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getNoOfItems() {
		return noOfItems;
	}

	public void setNoOfItems(String noOfItems) {
		this.noOfItems = noOfItems;
	}

	public String getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}

}
