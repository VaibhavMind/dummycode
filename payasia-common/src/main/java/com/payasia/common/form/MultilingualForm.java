package com.payasia.common.form;

import java.io.Serializable;

public class MultilingualForm implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5395108508498744367L;
	private long entityId;
	private long languageId;
	private long dataDictionaryId;
	private long companyId;
	private String label;
	private String labelValue;
	private String columnName;
	private String dataDictName;
	private String description;
	private String fieldType;

	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	public String getDataDictName() {
		return dataDictName;
	}
	public void setDataDictName(String dataDictName) {
		this.dataDictName = dataDictName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public long getDataDictionaryId() {
		return dataDictionaryId;
	}
	public void setDataDictionaryId(long dataDictionaryId) {
		this.dataDictionaryId = dataDictionaryId;
	}
	public long getCompanyId() {
		return companyId;
	}
	public void setCompanyId(long companyId) {
		this.companyId = companyId;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public long getEntityId() {
		return entityId;
	}
	public void setEntityId(long entityId) {
		this.entityId = entityId;
	}
	public long getLanguageId() {
		return languageId;
	}
	public void setLanguageId(long languageId) {
		this.languageId = languageId;
	}
	public String getFieldType() {
		return fieldType;
	}
	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}
	public String getLabelValue() {
		return labelValue;
	}
	public void setLabelValue(String labelValue) {
		this.labelValue = labelValue;
	}
}
