package com.payasia.common.dto;

import java.io.Serializable;

public class CalendarCodeValueDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7112230244381978955L;
	private long codeValId;
	private String code;
	private String value;
	private long valueId;

	public long getCodeValId() {
		return codeValId;
	}

	public void setCodeValId(long codeValId) {
		this.codeValId = codeValId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public long getValueId() {
		return valueId;
	}

	public void setValueId(long valueId) {
		this.valueId = valueId;
	}

}
