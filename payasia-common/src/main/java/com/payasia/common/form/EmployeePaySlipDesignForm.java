package com.payasia.common.form;

import java.io.Serializable;

public class EmployeePaySlipDesignForm implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5483286358042299943L;
	private String customField;

	public String getCustomField() {
		return customField;
	}

	public void setCustomField(String customField) {
		this.customField = customField;
	}

}
