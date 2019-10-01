package com.payasia.common.dto;

import java.io.Serializable;

public class ColumnPropertyDTO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3336694363123897557L;

	private String columnType;
	
	private Integer columnLength;
	
	private int columnNullable;

	public String getColumnType() {
		return columnType;
	}

	public void setColumnType(String columnType) {
		this.columnType = columnType;
	}

	public Integer getColumnLength() {
		return columnLength;
	}

	public void setColumnLength(Integer columnLength) {
		this.columnLength = columnLength;
	}

	public int getColumnNullable() {
		return columnNullable;
	}

	public void setColumnNullable(int columnNullable) {
		this.columnNullable = columnNullable;
	}
	
	
}
