package com.payasia.common.dto;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataImportLogDTO implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6587311956409074744L;
	private long importLogId;
	private long importHistoryId;
	private long rowNumber;
	private String colName;
	private String failureType;
	private String remarks;
	private boolean fromMessageSource;
	private Object[] postParameter;
	private String errorValue;
	private String errorKey;
	private boolean isImportTypeTextPdfFile;
	private int totalRecords;
	private int totalSuccessRecords;
	private int totalFailedRecords;
	private List<String> invalidEmployeeNumbersList;
	private List<String> validEmployeeNumbersList;
	private List<String> zipFileNamesList;
	
	
	public long getImportLogId() {
		return importLogId;
	}
	public void setImportLogId(long importLogId) {
		this.importLogId = importLogId;
	}
	public long getImportHistoryId() {
		return importHistoryId;
	}
	public void setImportHistoryId(long importHistoryId) {
		this.importHistoryId = importHistoryId;
	}
	public long getRowNumber() {
		return rowNumber;
	}
	public void setRowNumber(long l) {
		this.rowNumber = l;
	}
	public String getColName() {
		return colName;
	}
	public void setColName(String colName) {
		this.colName = colName;
	}
	public String getFailureType() {
		return failureType;
	}
	public void setFailureType(String failureType) {
		this.failureType = failureType;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public boolean isFromMessageSource() {
		return fromMessageSource;
	}
	public void setFromMessageSource(boolean fromMessageSource) {
		this.fromMessageSource = fromMessageSource;
	}
	public Object[] getPostParameter() {
		return postParameter;
	}
	public void setPostParameter(Object[] postParameter) {
		if (postParameter != null) {
			this.postParameter = Arrays.copyOf(postParameter, postParameter.length);
		}
	}
	public String getErrorValue() {
		return errorValue;
	}
	public void setErrorValue(String errorValue) {
		this.errorValue = errorValue;
	}
	public String getErrorKey() {
		return errorKey;
	}
	public void setErrorKey(String errorKey) {
		this.errorKey = errorKey;
	}
	public boolean isImportTypeTextPdfFile() {
		return isImportTypeTextPdfFile;
	}
	public void setImportTypeTextPdfFile(boolean isImportTypeTextPdfFile) {
		this.isImportTypeTextPdfFile = isImportTypeTextPdfFile;
	}
	public int getTotalRecords() {
		return totalRecords;
	}
	public void setTotalRecords(int totalRecords) {
		this.totalRecords = totalRecords;
	}
	public int getTotalSuccessRecords() {
		return totalSuccessRecords;
	}
	public void setTotalSuccessRecords(int totalSuccessRecords) {
		this.totalSuccessRecords = totalSuccessRecords;
	}
	public int getTotalFailedRecords() {
		return totalFailedRecords;
	}
	public void setTotalFailedRecords(int totalFailedRecords) {
		this.totalFailedRecords = totalFailedRecords;
	}
	public List<String> getInvalidEmployeeNumbersList() {
		return invalidEmployeeNumbersList;
	}
	public void setInvalidEmployeeNumbersList(
			List<String> invalidEmployeeNumbersList) {
		this.invalidEmployeeNumbersList = invalidEmployeeNumbersList;
	}
	public List<String> getZipFileNamesList() {
		return zipFileNamesList;
	}
	public void setZipFileNamesList(List<String> zipFileNamesList) {
		this.zipFileNamesList = zipFileNamesList;
	}
	public List<String> getValidEmployeeNumbersList() {
		return validEmployeeNumbersList;
	}
	public void setValidEmployeeNumbersList(List<String> validEmployeeNumbersList) {
		this.validEmployeeNumbersList = validEmployeeNumbersList;
	}
	
	
}
