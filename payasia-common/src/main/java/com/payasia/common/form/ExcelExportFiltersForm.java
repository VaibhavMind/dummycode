package com.payasia.common.form;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.payasia.common.dto.DataImportKeyValueDTO;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExcelExportFiltersForm implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1264861463475689923L;

	private long exportFilterId;
	
	private long dictionaryId;

	private String dataDictionaryName;
	
	private String equalityOperator;

	private String logicalOperator;

	private String openBracket;
	
	private String closeBracket;
	
	private String value;
	
	private DataImportKeyValueDTO dataImportKeyValueDTO;
	
	private String dataType;
	
	

	
	
	
	
	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getDataDictionaryName() {
		return dataDictionaryName;
	}

	public void setDataDictionaryName(String dataDictionaryName) {
		this.dataDictionaryName = dataDictionaryName;
	}

	public DataImportKeyValueDTO getDataImportKeyValueDTO() {
		return dataImportKeyValueDTO;
	}

	public void setDataImportKeyValueDTO(DataImportKeyValueDTO dataImportKeyValueDTO) {
		this.dataImportKeyValueDTO = dataImportKeyValueDTO;
	}

	public long getDictionaryId() {
		return dictionaryId;
	}

	public void setDictionaryId(long dictionaryId) {
		this.dictionaryId = dictionaryId;
	}

	public long getExportFilterId() {
		return exportFilterId;
	}

	public void setExportFilterId(long exportFilterId) {
		this.exportFilterId = exportFilterId;
	}

	public String getEqualityOperator() {
		return equalityOperator;
	}

	public void setEqualityOperator(String equalityOperator) {
		this.equalityOperator = equalityOperator;
	}

	public String getLogicalOperator() {
		return logicalOperator;
	}

	public void setLogicalOperator(String logicalOperator) {
		this.logicalOperator = logicalOperator;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getOpenBracket() {
		return openBracket;
	}

	public void setOpenBracket(String openBracket) {
		this.openBracket = openBracket;
	}

	public String getCloseBracket() {
		return closeBracket;
	}

	public void setCloseBracket(String closeBracket) {
		this.closeBracket = closeBracket;
	}
	
	
	
}
