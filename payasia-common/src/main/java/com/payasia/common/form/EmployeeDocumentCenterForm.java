package com.payasia.common.form;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class EmployeeDocumentCenterForm implements Serializable {

	private static final long serialVersionUID = 566662041608917958L;
	private long docId;
	private String year;
	private Long claimCategory;
	private String docName;
	private String uploadDate;
	private String docType;
	private String description;
	private String filename;
	private String category;
	private String success;
	private String tabName;
	private boolean isUploadTypeTaxPdfFile;
	private boolean fileNameContainsCompName;
	private String message;
	private boolean action;
	
	public long getDocId() {
		return docId;
	}
	public void setDocId(long docId) {
		this.docId = docId;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public Long getClaimCategory() {
		return claimCategory;
	}
	public void setClaimCategory(Long claimCategory) {
		this.claimCategory = claimCategory;
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
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getSuccess() {
		return success;
	}
	public void setSuccess(String success) {
		this.success = success;
	}
	public String getTabName() {
		return tabName;
	}
	public void setTabName(String tabName) {
		this.tabName = tabName;
	}
	public boolean isUploadTypeTaxPdfFile() {
		return isUploadTypeTaxPdfFile;
	}
	public void setUploadTypeTaxPdfFile(boolean isUploadTypeTaxPdfFile) {
		this.isUploadTypeTaxPdfFile = isUploadTypeTaxPdfFile;
	}
	public boolean isFileNameContainsCompName() {
		return fileNameContainsCompName;
	}
	public void setFileNameContainsCompName(boolean fileNameContainsCompName) {
		this.fileNameContainsCompName = fileNameContainsCompName;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public boolean isAction() {
		return action;
	}
	public void setAction(boolean action) {
		this.action = action;
	}
	
	
	
}
