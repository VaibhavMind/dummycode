package com.payasia.common.dto;

import java.io.Serializable;
import java.sql.Timestamp;

public class EmployeeDocumentConditionDTO implements Serializable{

	private Long companyDocumentId;

	public Long getCompanyDocumentId() {
		return companyDocumentId;
	}

	public void setCompanyDocumentId(Long companyDocumentId) {
		this.companyDocumentId = companyDocumentId;
	}

	private String documentName;

	private String type;

	private String category;

	private Long categoryId;

	private Timestamp uploadedDate;

	private String description;

	private Integer year;

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDocumentName() {
		return documentName;
	}

	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public Timestamp getUploadedDate() {
		return uploadedDate;
	}

	public void setUploadedDate(Timestamp uploadedDate) {
		this.uploadedDate = uploadedDate;
	}

	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

}
