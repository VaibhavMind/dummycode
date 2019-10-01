package com.payasia.common.dto;


public class DataImportHistoryDTO {

	
	private long dataImportHistoryId;

	private String fileName;

	private String importDate;

	private String status;

	public long getDataImportHistoryId() {
		return dataImportHistoryId;
	}

	public void setDataImportHistoryId(long dataImportHistoryId) {
		this.dataImportHistoryId = dataImportHistoryId;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getImportDate() {
		return importDate;
	}

	public void setImportDate(String importDate) {
		this.importDate = importDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	
}
