package com.payasia.common.form;

import java.io.Serializable;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

public class EmpDocumentCenterForm implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2433340803604195742L;

	private long employeeDocumentId;

	private String name;
	private String code;
	private String accountCode;
	private String visibleOrHidden;
	private String instruction;
	private Long claimCategory;
	private String docName;
	private String uploadDate;
	private String docType;
	private String docSize;
	private String description;
	private String filename;
	private CommonsMultipartFile fileData;

	private String xmlString;
	private String tabName;

	private Integer year;

	public long getEmployeeDocumentId() {
		return employeeDocumentId;
	}

	public void setEmployeeDocumentId(long employeeDocumentId) {
		this.employeeDocumentId = employeeDocumentId;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public String getTabName() {
		return tabName;
	}

	public void setTabName(String tabName) {
		this.tabName = tabName;
	}

	public String getXmlString() {
		return xmlString;
	}

	public void setXmlString(String xmlString) {
		this.xmlString = xmlString;
	}

	private long categoryId;

	private String categoryName;

	public long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(long categoryId) {
		this.categoryId = categoryId;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public CommonsMultipartFile getFileData() {
		return fileData;
	}

	public void setFileData(CommonsMultipartFile fileData) {
		this.fileData = fileData;
	}

	public String getDocName() {
		return docName;
	}

	public void setDocName(String docName) {
		this.docName = docName;
	}

	public String getUploadDate() {
		return uploadDate;
	}

	public void setUploadDate(String uploadDate) {
		this.uploadDate = uploadDate;
	}

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

	public String getDocSize() {
		return docSize;
	}

	public void setDocSize(String docSize) {
		this.docSize = docSize;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getAccountCode() {
		return accountCode;
	}

	public void setAccountCode(String accountCode) {
		this.accountCode = accountCode;
	}

	public String getVisibleOrHidden() {
		return visibleOrHidden;
	}

	public void setVisibleOrHidden(String visibleOrHidden) {
		this.visibleOrHidden = visibleOrHidden;
	}

	public String getInstruction() {
		return instruction;
	}

	public void setInstruction(String instruction) {
		this.instruction = instruction;
	}

	public Long getClaimCategory() {
		return claimCategory;
	}

	public void setClaimCategory(Long claimCategory) {
		this.claimCategory = claimCategory;
	}

}
