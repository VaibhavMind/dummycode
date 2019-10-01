package com.payasia.common.dto;

import java.io.Serializable;

public class DataDictionaryDTO implements Serializable {
	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long dataDictionaryId;
	private String dataDictionaryName;
	private String dataType;
	
	
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public Long getDataDictionaryId() {
		return dataDictionaryId;
	}
	public void setDataDictionaryId(Long dataDictionaryId) {
		this.dataDictionaryId = dataDictionaryId;
	}
	public String getDataDictionaryName() {
		return dataDictionaryName;
	}
	public void setDataDictionaryName(String dataDictionaryName) {
		this.dataDictionaryName = dataDictionaryName;
	}
	
	
	

}
