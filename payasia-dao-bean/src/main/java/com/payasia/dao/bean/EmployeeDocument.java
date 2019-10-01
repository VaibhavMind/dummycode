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

/**
 * The persistent class for the Employee_Document database table.
 * 
 */
@Entity
@Table(name = "Employee_Document")
public class EmployeeDocument extends CompanyBaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Emp_Document_ID")
	private long empDocumentId;

	@Column(name = "Description")
	private String description;

	@Column(name = "File_Name")
	private String fileName;

	@Column(name = "File_Type")
	private String fileType;

	@Column(name = "Uploaded_Date")
	private Timestamp uploadedDate;

	@Column(name = "Year")
	private int year;

	 
	@ManyToOne
	@JoinColumn(name = "Employee_ID")
	private Employee employee;

	public EmployeeDocument() {
	}

	public long getEmpDocumentId() {
		return this.empDocumentId;
	}

	public void setEmpDocumentId(long empDocumentId) {
		this.empDocumentId = empDocumentId;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFileName() {
		return this.fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileType() {
		return this.fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public Timestamp getUploadedDate() {
		return this.uploadedDate;
	}

	public void setUploadedDate(Timestamp uploadedDate) {
		this.uploadedDate = uploadedDate;
	}

	public int getYear() {
		return this.year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public Employee getEmployee() {
		return this.employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

}