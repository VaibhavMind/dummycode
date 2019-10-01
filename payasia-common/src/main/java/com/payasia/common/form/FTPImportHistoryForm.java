package com.payasia.common.form;

import java.io.Serializable;

public class FTPImportHistoryForm implements Serializable{
	
	private static final long serialVersionUID = 1L;

	private long ftpImpHisId;
	private long ftpImportHistoryId;
	private String createdDate;
	private String importFileName;
	private String importStatus;
	private int totalEmpRecords;
	private int totalExistingEmpRecords;
	private int totalNewHireEmpRecords;
	private String failedRemarks;
	private String importData;
	private long companyId;

	public long getFtpImportHistoryId() {
		return ftpImportHistoryId;
	}
	public void setFtpImportHistoryId(long ftpImportHistoryId) {
		this.ftpImportHistoryId = ftpImportHistoryId;
	}
	public long getFtpImpHisId() {
		return ftpImpHisId;
	}

	public void setFtpImpHisId(long ftpImpHisId) {
		this.ftpImpHisId = ftpImpHisId;
	}

	
	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public String getImportFileName() {
		return importFileName;
	}

	public void setImportFileName(String importFileName) {
		this.importFileName = importFileName;
	}

	 

	public String getImportStatus() {
		return importStatus;
	}

	public void setImportStatus(String importStatus) {
		this.importStatus = importStatus;
	}

	public int getTotalEmpRecords() {
		return totalEmpRecords;
	}

	public void setTotalEmpRecords(int totalEmpRecords) {
		this.totalEmpRecords = totalEmpRecords;
	}

	public String getFailedRemarks() {
		return failedRemarks;
	}

	public void setFailedRemarks(String failedRemarks) {
		this.failedRemarks = failedRemarks;
	}

	public String getImportData() {
		return importData;
	}

	public void setImportData(String importData) {
		this.importData = importData;
	}

	public long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(long companyId) {
		this.companyId = companyId;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public int getTotalExistingEmpRecords() {
		return totalExistingEmpRecords;
	}

	public void setTotalExistingEmpRecords(int totalExistingEmpRecords) {
		this.totalExistingEmpRecords = totalExistingEmpRecords;
	}

	public int getTotalNewHireEmpRecords() {
		return totalNewHireEmpRecords;
	}

	public void setTotalNewHireEmpRecords(int totalNewHireEmpRecords) {
		this.totalNewHireEmpRecords = totalNewHireEmpRecords;
	}

}