package com.payasia.common.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class SelectOptionDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String strKey;
	private Long longKey;
	private String value;
	
	public SelectOptionDTO() {

	}
	
	
	public SelectOptionDTO(String strKey, String value) {
		super();
		this.strKey = strKey;
		this.value = value;
	}


	public String getStrKey() {
		return strKey;
	}

	public void setStrKey(String strKey) {
		this.strKey = strKey;
	}

	public Long getLongKey() {
		return longKey;
	}

	public void setLongKey(Long longKey) {
		this.longKey = longKey;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
