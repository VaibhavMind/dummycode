package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

public class CalculatoryFieldFormResponse extends PageResponse implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3138064830972960460L;
	private List<CalculatoryFieldForm> calculatoryFieldList;

	public List<CalculatoryFieldForm> getCalculatoryFieldList() {
		return calculatoryFieldList;
	}

	public void setCalculatoryFieldList(
			List<CalculatoryFieldForm> calculatoryFieldList) {
		this.calculatoryFieldList = calculatoryFieldList;
	}

}
