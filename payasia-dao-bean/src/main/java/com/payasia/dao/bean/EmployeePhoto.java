package com.payasia.dao.bean;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Arrays;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * The persistent class for the Employee_Photo database table.
 * 
 */
@Entity
@Table(name = "Employee_Photo")
public class EmployeePhoto extends CompanyBaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Employee_ID")
	private long employee_ID;

	@Lob()
	@Column(name = "Photo")
	private byte[] photo;

	@Column(name = "Uploaded_Date")
	private Timestamp uploadedDate;

	@Column(name = "File_Name")
	private String fileName;

	@Column(name = "File_Type")
	private String fileType;

	@ManyToOne
	@JoinColumn(name = "Employee_ID", insertable = false, updatable = false)
	private Employee employee;

	public EmployeePhoto() {
	}

	public long getEmployee_ID() {
		return this.employee_ID;
	}

	public void setEmployee_ID(long employee_ID) {
		this.employee_ID = employee_ID;
	}

	public byte[] getPhoto() {
		return this.photo;
	}

	public void setPhoto(byte[] photo) {
		if (photo != null) {
			this.photo = Arrays.copyOf(photo, photo.length);
		}
	}

	public Timestamp getUploadedDate() {
		return this.uploadedDate;
	}

	public void setUploadedDate(Timestamp uploadedDate) {
		this.uploadedDate = uploadedDate;
	}

	public Employee getEmployee() {
		return this.employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

}