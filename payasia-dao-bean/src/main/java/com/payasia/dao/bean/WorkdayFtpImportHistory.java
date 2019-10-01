package com.payasia.dao.bean;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "Workday_FTP_Import_History")
public class WorkdayFtpImportHistory extends UpdatedBaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Workday_FTP_Import_History_ID")
	private long ftpImportHistoryId;

	@Column(name = "Created_Date")
	private Timestamp createdDate;

	@Column(name = "Import_File_Name")
	private String importFileName;

	@Column(name = "Import_Status")
	private boolean importStatus;
	
	@Column(name = "Import_Type")
	private String importType;

	@Column(name = "Total_Emp_Records")
	private int totalEmpRecords;

	@Column(name = "Existing_Emp_Records_Updated")
	private int existingEmpRecordsUpdated;

	@Column(name = "New_Emp_Records_Updated")
	private int newEmpRecordsUpdated;

	@Column(name = "Failed_Remarks")
	private String failedRemarks;

	@ManyToOne
	@JoinColumn(name = "Company_ID")
	private Company company;

	@ManyToOne
	@JoinColumn(name = "Created_By")
	private Employee createdBy;
	
	public WorkdayFtpImportHistory() {}

	public Timestamp getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Timestamp createdDate) {
		this.createdDate = createdDate;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public long getFtpImportHistoryId() {
		return ftpImportHistoryId;
	}

	public void setFtpImportHistoryId(long ftpImportHistoryId) {
		this.ftpImportHistoryId = ftpImportHistoryId;
	}

	public String getImportFileName() {
		return importFileName;
	}

	public void setImportFileName(String importFileName) {
		this.importFileName = importFileName;
	}

	public boolean getImportStatus() {
		return importStatus;
	}

	public void setImportStatus(boolean importStatus) {
		this.importStatus = importStatus;
	}

	public String getImportType() {
		return importType;
	}

	public void setImportType(String importType) {
		this.importType = importType;
	}

	public int getTotalEmpRecords() {
		return totalEmpRecords;
	}

	public void setTotalEmpRecords(int totalEmpRecords) {
		this.totalEmpRecords = totalEmpRecords;
	}

	public int getExistingEmpRecordsUpdated() {
		return existingEmpRecordsUpdated;
	}

	public void setExistingEmpRecordsUpdated(int existingEmpRecordsUpdated) {
		this.existingEmpRecordsUpdated = existingEmpRecordsUpdated;
	}

	public int getNewEmpRecordsUpdated() {
		return newEmpRecordsUpdated;
	}

	public void setNewEmpRecordsUpdated(int newEmpRecordsUpdated) {
		this.newEmpRecordsUpdated = newEmpRecordsUpdated;
	}

	public String getFailedRemarks() {
		return failedRemarks;
	}

	public void setFailedRemarks(String failedRemarks) {
		this.failedRemarks = failedRemarks;
	}

	public Employee getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Employee createdBy) {
		this.createdBy = createdBy;
	}

}
