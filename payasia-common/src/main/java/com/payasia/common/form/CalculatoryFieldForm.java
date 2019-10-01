package com.payasia.common.form;

import java.io.Serializable;

public class CalculatoryFieldForm implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String fieldName;
	private Long dictionaryId;

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public Long getDictionaryId() {
		return dictionaryId;
	}

	public void setDictionaryId(Long dictionaryId) {
		this.dictionaryId = dictionaryId;
	}

}
