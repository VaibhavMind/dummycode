package com.payasia.common.form;

import java.io.Serializable;

import com.payasia.common.dto.DataImportKeyValueDTO;


public class GeneralFilterDTO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1264861463475689923L;

	private long filterId;
	
	private long dictionaryId;

	private String dataDictionaryName;
	
	private String equalityOperator;

	private String logicalOperator;

	private String openBracket;
	
	private String closeBracket;
	
	private String value;
	
	private DataImportKeyValueDTO dataImportKeyValueDTO;

	
	
	
	
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



	public long getFilterId() {
		return filterId;
	}

	public void setFilterId(long filterId) {
		this.filterId = filterId;
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
