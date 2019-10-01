package com.payasia.common.dto;

import java.io.Serializable;
import java.util.List;

public class CustomFieldsDTO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 9042006688977973661L;
	private Long customFieldId;
	private String fieldName;
	private String fieldValue;
	private boolean mandatory;
	private Long customFieldType;
	private String fieldDropdownValues;
	
	private String customFieldTypeName;
	List<AppCodeDTO> customFieldList;
	
	
	
	
	
	
	
	public String getFieldValue() {
		return fieldValue;
	}
	public void setFieldValue(String fieldValue) {
		this.fieldValue = fieldValue;
	}
	public String getCustomFieldTypeName() {
		return customFieldTypeName;
	}
	public void setCustomFieldTypeName(String customFieldTypeName) {
		this.customFieldTypeName = customFieldTypeName;
	}
	public List<AppCodeDTO> getCustomFieldList() {
		return customFieldList;
	}
	public void setCustomFieldList(List<AppCodeDTO> customFieldList) {
		this.customFieldList = customFieldList;
	}
	public Long getCustomFieldType() {
		return customFieldType;
	}
	public void setCustomFieldType(Long customFieldType) {
		this.customFieldType = customFieldType;
	}
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public boolean isMandatory() {
		return mandatory;
	}
	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}
	public Long getCustomFieldId() {
		return customFieldId;
	}
	public void setCustomFieldId(Long customFieldId) {
		this.customFieldId = customFieldId;
	}
	public String getFieldDropdownValues() {
		return fieldDropdownValues;
	}
	public void setFieldDropdownValues(String fieldDropdownValues) {
		this.fieldDropdownValues = fieldDropdownValues;
	}

}
