package com.payasia.common.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class DataImportParametersDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8841320052872490588L;
	List<DataImportKeyValueDTO> empColName;
	String rowNumber;
	boolean isDynamic;
	List<Long> formIds;
	long entityId;
	List<String> tableNames;
	List<HashMap<String, String>> colFormMapList;
	List<String> dynRecordsName;
	String transactionType;
	List<String> dependentsTypeFieldNameList;

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public List<DataImportKeyValueDTO> getEmpColName() {
		return empColName;
	}

	public void setEmpColName(List<DataImportKeyValueDTO> empColName) {
		this.empColName = empColName;
	}

	public String getRowNumber() {
		return rowNumber;
	}

	public void setRowNumber(String rowNumber) {
		this.rowNumber = rowNumber;
	}

	public boolean isDynamic() {
		return isDynamic;
	}

	public void setDynamic(boolean isDynamic) {
		this.isDynamic = isDynamic;
	}

	public List<Long> getFormIds() {
		return formIds;
	}

	public void setFormIds(List<Long> formIds) {
		this.formIds = formIds;
	}

	public long getEntityId() {
		return entityId;
	}

	public void setEntityId(long entityId) {
		this.entityId = entityId;
	}

	public List<String> getTableNames() {
		return tableNames;
	}

	public void setTableNames(List<String> tableNames) {
		this.tableNames = tableNames;
	}

	public List<HashMap<String, String>> getColFormMapList() {
		return colFormMapList;
	}

	public void setColFormMapList(List<HashMap<String, String>> colFormMapList) {
		this.colFormMapList = colFormMapList;
	}

	public List<String> getDynRecordsName() {
		return dynRecordsName;
	}

	public void setDynRecordsName(List<String> dynRecordsName) {
		this.dynRecordsName = dynRecordsName;
	}

	public List<String> getDependentsTypeFieldNameList() {
		return dependentsTypeFieldNameList;
	}

	public void setDependentsTypeFieldNameList(
			List<String> dependentsTypeFieldNameList) {
		this.dependentsTypeFieldNameList = dependentsTypeFieldNameList;
	}

}
