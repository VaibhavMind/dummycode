package com.payasia.common.dto;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

public class CompanyDocumentConditionDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1232762914061395873L;

	private Long companyDocumentId;
	
	private List<Long> companyDocumentIdList;

	private String documentName;

	private String type;

	private String category;
	
	private Long categoryId;

	private Timestamp uploadedDate;
	
	private String Description;
	
	private Integer year;
	
	private String employeeNumber;
	
	private String categoryName;
	
	
	
	
	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getEmployeeNumber() {
		return employeeNumber;
	}

	public void setEmployeeNumber(String employeeNumber) {
		this.employeeNumber = employeeNumber;
	}

	public Long getCompanyDocumentId() {
		return companyDocumentId;
	}

	public void setCompanyDocumentId(Long companyDocumentId) {
		this.companyDocumentId = companyDocumentId;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public String getDescription() {
		return Description;
	}

	public void setDescription(String description) {
		Description = description;
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

	public List<Long> getCompanyDocumentIdList() {
		return companyDocumentIdList;
	}

	public void setCompanyDocumentIdList(List<Long> companyDocumentIdList) {
		this.companyDocumentIdList = companyDocumentIdList;
	}

	

}
