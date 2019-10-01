package com.payasia.common.dto;

import java.io.Serializable;
import java.util.Date;

public class DateCodeDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2122935776464228655L;
	private Date date;
	private String code;
	private int rowNumber;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public int getRowNumber() {
		return rowNumber;
	}

	public void setRowNumber(int rowNumber) {
		this.rowNumber = rowNumber;
	}

}
