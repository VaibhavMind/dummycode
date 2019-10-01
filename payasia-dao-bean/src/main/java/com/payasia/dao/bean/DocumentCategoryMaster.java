package com.payasia.dao.bean;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * The persistent class for the Document_Category_Master database table.
 * 
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Table(name = "Document_Category_Master")
public class DocumentCategoryMaster implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Document_Category_ID")
	private long documentCategoryId;

	@Column(name = "Category_Desc")
	private String categoryDesc;

	@Column(name = "Category_Name")
	private String categoryName;

	
	@Column(name = "Label_Key")
	private String labelKey;

	 
	@OneToMany(mappedBy = "documentCategoryMaster")
	private Set<CompanyDocument> companyDocuments;

	public DocumentCategoryMaster() {
	}

	public long getDocumentCategoryId() {
		return this.documentCategoryId;
	}

	public void setDocumentCategoryId(long documentCategoryId) {
		this.documentCategoryId = documentCategoryId;
	}

	public String getCategoryDesc() {
		return this.categoryDesc;
	}

	public void setCategoryDesc(String categoryDesc) {
		this.categoryDesc = categoryDesc;
	}

	public String getCategoryName() {
		return this.categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public Set<CompanyDocument> getCompanyDocuments() {
		return this.companyDocuments;
	}

	public void setCompanyDocuments(Set<CompanyDocument> companyDocuments) {
		this.companyDocuments = companyDocuments;
	}

	public String getLabelKey() {
		return labelKey;
	}

	public void setLabelKey(String labelKey) {
		this.labelKey = labelKey;
	}

}