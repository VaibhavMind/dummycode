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
 * The persistent class for the Company_Document database table.
 * 
 */
@Entity
@Table(name = "Company_Document")
public class CompanyDocument extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Document_ID")
	private long documentId;

	@Column(name = "Description")
	private String description;

	@Column(name = "Uploaded_Date")
	private Timestamp uploadedDate;

	@Column(name = "Year")
	private Integer year;

	@Column(name = "Source")
	private String source = "D";

	@Column(name = "Part")
	private Integer part;

	@Column(name = "Released")
	private Boolean released = false;

	 
	@ManyToOne
	@JoinColumn(name = "Month_ID")
	private MonthMaster month;

	 
	@ManyToOne
	@JoinColumn(name = "Company_ID")
	private Company company;

	 
	@ManyToOne
	@JoinColumn(name = "Document_Category_ID")
	private DocumentCategoryMaster documentCategoryMaster;

	 
	@OneToMany(mappedBy = "companyDocument")
	private Set<CompanyDocumentDetail> companyDocumentDetails;

	 
	@OneToMany(mappedBy = "companyDocument")
	private Set<CompanyDocumentShortList> companyDocumentShortLists;

	public CompanyDocument() {
	}

	public long getDocumentId() {
		return this.documentId;
	}

	public void setDocumentId(long documentId) {
		this.documentId = documentId;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Timestamp getUploadedDate() {
		return this.uploadedDate;
	}

	public void setUploadedDate(Timestamp uploadedDate) {
		this.uploadedDate = uploadedDate;
	}

	public Integer getYear() {
		return this.year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public Company getCompany() {
		return this.company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public DocumentCategoryMaster getDocumentCategoryMaster() {
		return this.documentCategoryMaster;
	}

	public void setDocumentCategoryMaster(
			DocumentCategoryMaster documentCategoryMaster) {
		this.documentCategoryMaster = documentCategoryMaster;
	}

	public Set<CompanyDocumentDetail> getCompanyDocumentDetails() {
		return this.companyDocumentDetails;
	}

	public void setCompanyDocumentDetails(
			Set<CompanyDocumentDetail> companyDocumentDetails) {
		this.companyDocumentDetails = companyDocumentDetails;
	}

	public Set<CompanyDocumentShortList> getCompanyDocumentShortLists() {
		return this.companyDocumentShortLists;
	}

	public void setCompanyDocumentShortLists(
			Set<CompanyDocumentShortList> companyDocumentShortLists) {
		this.companyDocumentShortLists = companyDocumentShortLists;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public Integer getPart() {
		return part;
	}

	public void setPart(Integer part) {
		this.part = part;
	}

	public Boolean getReleased() {
		return released;
	}

	public void setReleased(Boolean released) {
		this.released = released;
	}

	public MonthMaster getMonth() {
		return month;
	}

	public void setMonth(MonthMaster month) {
		this.month = month;
	}

}