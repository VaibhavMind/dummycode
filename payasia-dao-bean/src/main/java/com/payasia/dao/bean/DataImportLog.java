package com.payasia.dao.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * The persistent class for the Data_Import_Log database table.
 * 
 */
@Entity
@Table(name = "Data_Import_Log")
public class DataImportLog extends CompanyBaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Data_Import_Log_ID")
	private long dataImportLogId;

	@Column(name = "Column_Name")
	private String columnName;

	@Column(name = "Failure_Type")
	private String failureType;

	@Column(name = "Remarks")
	private String remarks;

	@Column(name = "Row_Number")
	private long rowNumber;

	 
	@ManyToOne
	@JoinColumn(name = "Data_Import_History_ID")
	private DataImportHistory dataImportHistory;

	public DataImportLog() {
	}

	public long getDataImportLogId() {
		return this.dataImportLogId;
	}

	public void setDataImportLogId(long dataImportLogId) {
		this.dataImportLogId = dataImportLogId;
	}

	public String getColumnName() {
		return this.columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getFailureType() {
		return this.failureType;
	}

	public void setFailureType(String failureType) {
		this.failureType = failureType;
	}

	public String getRemarks() {
		return this.remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public long getRowNumber() {
		return this.rowNumber;
	}

	public void setRowNumber(long rowNumber) {
		this.rowNumber = rowNumber;
	}

	public DataImportHistory getDataImportHistory() {
		return this.dataImportHistory;
	}

	public void setDataImportHistory(DataImportHistory dataImportHistory) {
		this.dataImportHistory = dataImportHistory;
	}

}