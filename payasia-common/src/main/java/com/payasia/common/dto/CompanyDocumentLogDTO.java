package com.payasia.common.dto;

import java.io.Serializable;
import java.util.List;

public class CompanyDocumentLogDTO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6279031622639167753L;
	private String name;
	private String message;
	
	private int totalRecords;
	private int totalSuccessRecords;
	private int totalFailedRecords;
	private List<String> invalidEmployeeNumbersList;
	private List<String> zipFileNamesList;
	private boolean isUploadTypeTaxPdfFile;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
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
	public boolean isUploadTypeTaxPdfFile() {
		return isUploadTypeTaxPdfFile;
	}
	public void setUploadTypeTaxPdfFile(boolean isUploadTypeTaxPdfFile) {
		this.isUploadTypeTaxPdfFile = isUploadTypeTaxPdfFile;
	}
	
	
	

}
