package com.payasia.common.dto;

import java.io.Serializable;

public class GenericIdValueDTO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7061718205768638117L;
	private long id;
	private String value;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}

}
