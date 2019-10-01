package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.payasia.common.dto.DataImportKeyValueDTO;

@JsonInclude(Include.NON_NULL)
public class EmployeeFilterListForm implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3971006911840451659L;
	private String fieldName;
	private Long dataDictionaryId;
	private Long filterId;
	private String equalityOperator;
	private String value;
	private String logicalOperator;
	private String openBracket;
	private String closeBracket;
	private String dataType;
	private String dataDictionaryName;
	
	private DataImportKeyValueDTO dataImportKeyValueDTO;
	
	
	public String getDataDictionaryName() {
		return dataDictionaryName;
	}
	public void setDataDictionaryName(String dataDictionaryName) {
		this.dataDictionaryName = dataDictionaryName;
	}
	List<EmployeeFilterListForm> employeeFilterDataList;
	
	public List<EmployeeFilterListForm> getEmployeeFilterDataList() {
		return employeeFilterDataList;
	}
	public void setEmployeeFilterDataList(
			List<EmployeeFilterListForm> employeeFilterDataList) {
		this.employeeFilterDataList = employeeFilterDataList;
	}
	public Long getFilterId() {
		return filterId;
	}
	public void setFilterId(Long filterId) {
		this.filterId = filterId;
	}
	public String getEqualityOperator() {
		return equalityOperator;
	}
	public void setEqualityOperator(String equalityOperator) {
		this.equalityOperator = equalityOperator;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getLogicalOperator() {
		return logicalOperator;
	}
	public void setLogicalOperator(String logicalOperator) {
		this.logicalOperator = logicalOperator;
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
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public Long getDataDictionaryId() {
		return dataDictionaryId;
	}
	public void setDataDictionaryId(Long dataDictionaryId) {
		this.dataDictionaryId = dataDictionaryId;
	}
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public DataImportKeyValueDTO getDataImportKeyValueDTO() {
		return dataImportKeyValueDTO;
	}
	public void setDataImportKeyValueDTO(DataImportKeyValueDTO dataImportKeyValueDTO) {
		this.dataImportKeyValueDTO = dataImportKeyValueDTO;
	}

}
