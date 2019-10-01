package com.payasia.common.dto;

import java.io.Serializable;

public class SortConditionDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2838522489468053430L;
	private String columnName;
	private String sortOrder;

	public SortConditionDTO() {

	}

	public SortConditionDTO(String colName, String sortOrder) {
		this.columnName = colName;
		this.sortOrder = sortOrder;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getOrderType() {
		return sortOrder;
	}

	public void setOrderType(String orderType) {
		this.sortOrder = orderType;
	}

}
