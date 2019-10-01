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
 * The persistent class for the Company_Document_Detail database table.
 * 
 */
@Entity
@Table(name = "Company_Document_Detail")
public class CompanyDocumentDetail extends CompanyBaseEntity implements
		Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Company_Document_Detail_ID")
	private long companyDocumentDetailID;

	@Column(name = "File_Name")
	private String fileName;

	@Column(name = "File_Type")
	private String fileType;

	 
	@ManyToOne
	@JoinColumn(name = "Document_ID")
	private CompanyDocument companyDocument;

	public CompanyDocumentDetail() {
	}

	public long getCompanyDocumentDetailID() {
		return this.companyDocumentDetailID;
	}

	public void setCompanyDocumentDetailID(long companyDocumentDetailID) {
		this.companyDocumentDetailID = companyDocumentDetailID;
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

	public CompanyDocument getCompanyDocument() {
		return this.companyDocument;
	}

	public void setCompanyDocument(CompanyDocument companyDocument) {
		this.companyDocument = companyDocument;
	}

}