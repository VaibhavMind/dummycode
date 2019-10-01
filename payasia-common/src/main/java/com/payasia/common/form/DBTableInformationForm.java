package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DBTableInformationForm implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7473800505593354412L;
	private long dbId;
	private long dataDictionaryId;
	private String dbFileHeaders;
	private String dbDataTypeField;
	private String requiredField;
	private String dbDefaultValue;
	private String dbFields;
	private String description;
	private String sample;
	private String templateName;
	private String catagory;
	private String dbTable;
	private String select;
	private String dataType;
	private String required;
	private Integer length;
	private String xlFeild;
	private String dataDictName;
	private Long entityId;
	private Integer maxLength;
	private Integer minLength;
	private Integer precision;
	private Integer scale;
	private Long sectionId;
	private String sectionName;
	private String sectionWithTableName;
	private String sectionIdGroup;
	private List<ExcelExportFiltersForm> excelExportFilterList;
	private String fieldCount;
	private String tabName;
	private String label;
	private String dataDictionaryName;
	private String excelExpFieldEntityName;
	private String dataDictionaryNameConst;
	private Long formId;
	
	
	public Long getEntityId() {
		return entityId;
	}

	public void setEntityId(Long entityId) {
		this.entityId = entityId;
	}

	public String getDataDictName() {
		return dataDictName;
	}

	public void setDataDictName(String dataDictName) {
		this.dataDictName = dataDictName;
	}

	public String getFieldCount() {
		return fieldCount;
	}

	public void setFieldCount(String fieldCount) {
		this.fieldCount = fieldCount;
	}

	public String getTabName() {
		return tabName;
	}

	public void setTabName(String tabName) {
		this.tabName = tabName;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getDataDictionaryName() {
		return dataDictionaryName;
	}

	public void setDataDictionaryName(String dataDictionaryName) {
		this.dataDictionaryName = dataDictionaryName;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getSectionIdGroup() {
		return sectionIdGroup;
	}

	public void setSectionIdGroup(String sectionIdGroup) {
		this.sectionIdGroup = sectionIdGroup;
	}

	public List<ExcelExportFiltersForm> getExcelExportFilterList() {
		return excelExportFilterList;
	}

	public void setExcelExportFilterList(
			List<ExcelExportFiltersForm> excelExportFilterList) {
		this.excelExportFilterList = excelExportFilterList;
	}

	public String getSelect() {
		return select;
	}

	public void setSelect(String select) {
		this.select = select;
	}

	public long getDataDictionaryId() {
		return dataDictionaryId;
	}

	public void setDataDictionaryId(long dataDictionaryId) {
		this.dataDictionaryId = dataDictionaryId;
	}

	public Integer getLength() {
		return length;
	}

	public long getDbId() {
		return dbId;
	}

	public void setDbId(long dbId) {
		this.dbId = dbId;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getRequired() {
		return required;
	}

	public void setRequired(String required) {
		this.required = required;
	}


	public String getXlFeild() {
		return xlFeild;
	}

	public void setXlFeild(String xlFeild) {
		this.xlFeild = xlFeild;
	}


	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public String getCatagory() {
		return catagory;
	}

	public void setCatagory(String catagory) {
		this.catagory = catagory;
	}

	public String getDbTable() {
		return dbTable;
	}

	public void setDbTable(String dbTable) {
		this.dbTable = dbTable;
	}

	public String getDbFields() {
		return dbFields;
	}

	public void setDbFields(String dbFields) {
		this.dbFields = dbFields;
	}

	public String getDbFileHeaders() {
		return dbFileHeaders;
	}

	public void setDbFileHeaders(String dbFileHeaders) {
		this.dbFileHeaders = dbFileHeaders;
	}

	public String getDbDataTypeField() {
		return dbDataTypeField;
	}

	public void setDbDataTypeField(String dbDataTypeField) {
		this.dbDataTypeField = dbDataTypeField;
	}

	public String getDbDefaultValue() {
		return dbDefaultValue;
	}

	public void setDbDefaultValue(String dbDefaultValue) {
		this.dbDefaultValue = dbDefaultValue;
	}

	public String getRequiredField() {
		return requiredField;
	}

	public void setRequiredField(String requiredField) {
		this.requiredField = requiredField;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSample() {
		return sample;
	}

	public void setSample(String sample) {
		this.sample = sample;
	}

	public Integer getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(Integer maxLength) {
		this.maxLength = maxLength;
	}

	public Integer getMinLength() {
		return minLength;
	}

	public void setMinLength(Integer minLength) {
		this.minLength = minLength;
	}

	public Integer getPrecision() {
		return precision;
	}

	public void setPrecision(Integer precision) {
		this.precision = precision;
	}

	public Integer getScale() {
		return scale;
	}

	public void setScale(Integer scale) {
		this.scale = scale;
	}

	public void setLength(Integer length) {
		this.length = length;
	}

	public Long getSectionId() {
		return sectionId;
	}

	public void setSectionId(Long sectionId) {
		this.sectionId = sectionId;
	}

	public String getExcelExpFieldEntityName() {
		return excelExpFieldEntityName;
	}

	public void setExcelExpFieldEntityName(String excelExpFieldEntityName) {
		this.excelExpFieldEntityName = excelExpFieldEntityName;
	}

	public String getSectionName() {
		return sectionName;
	}

	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}

	public String getSectionWithTableName() {
		return sectionWithTableName;
	}

	public void setSectionWithTableName(String sectionWithTableName) {
		this.sectionWithTableName = sectionWithTableName;
	}

	public String getDataDictionaryNameConst() {
		return dataDictionaryNameConst;
	}

	public void setDataDictionaryNameConst(String dataDictionaryNameConst) {
		this.dataDictionaryNameConst = dataDictionaryNameConst;
	}

	public Long getFormId() {
		return formId;
	}

	public void setFormId(Long formId) {
		this.formId = formId;
	}

	
	

}
