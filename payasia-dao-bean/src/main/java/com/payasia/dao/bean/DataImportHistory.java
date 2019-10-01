package com.payasia.dao.bean;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * The persistent class for the Data_Import_History database table.
 * 
 */
@Entity
@Table(name = "Data_Import_History")
public class DataImportHistory extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Data_Import_History_ID")
	private long dataImportHistoryId;

	@Column(name = "File_Name")
	private String fileName;

	@Column(name = "Import_Date")
	private Timestamp importDate;

	@Column(name = "Status")
	private String status;

	 
	@ManyToOne
	@JoinColumn(name = "Company_ID")
	private Company company;

	 
	@ManyToOne
	@JoinColumn(name = "Entity_ID")
	private EntityMaster entityMaster;

	 
	@OneToMany(mappedBy = "dataImportHistory")
	private Set<DataImportLog> dataImportLogs;

	public DataImportHistory() {
	}

	public long getDataImportHistoryId() {
		return this.dataImportHistoryId;
	}

	public void setDataImportHistoryId(long dataImportHistoryId) {
		this.dataImportHistoryId = dataImportHistoryId;
	}

	public String getFileName() {
		return this.fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Timestamp getImportDate() {
		return this.importDate;
	}

	public void setImportDate(Timestamp importDate) {
		this.importDate = importDate;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Company getCompany() {
		return this.company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public EntityMaster getEntityMaster() {
		return this.entityMaster;
	}

	public void setEntityMaster(EntityMaster entityMaster) {
		this.entityMaster = entityMaster;
	}

	public Set<DataImportLog> getDataImportLogs() {
		return this.dataImportLogs;
	}

	public void setDataImportLogs(Set<DataImportLog> dataImportLogs) {
		this.dataImportLogs = dataImportLogs;
	}

}