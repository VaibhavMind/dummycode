package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.payasia.common.dto.CompanyDocumentLogDTO;

public class CompanyDocumentCenterForm implements
		Serializable {

	/**
	 * 
	 */
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
	private String xmlString;
	private String tabName;
	/*@JsonIgnore
	private List<CompanyDocumentCenterForm> xmlList;
	@JsonIgnore
	private List<CompanyDocumentCenterForm> companyDocumentCenterFormList;*/
	private List<CompanyDocumentLogDTO> companyDocumentLogs;
	private boolean isUploadTypeTaxPdfFile;
	private CompanyDocumentLogDTO companyDocumentSummaryDTO;
	private boolean fileNameContainsCompName;
	private boolean zipEntriesInvalid;
	private String message;
	private String metaData;

	public boolean isFileNameContainsCompName() {
		return fileNameContainsCompName;
	}

	public void setFileNameContainsCompName(boolean fileNameContainsCompName) {
		this.fileNameContainsCompName = fileNameContainsCompName;
	}

	public List<CompanyDocumentLogDTO> getCompanyDocumentLogs() {
		return companyDocumentLogs;
	}

	public void setCompanyDocumentLogs(
			List<CompanyDocumentLogDTO> companyDocumentLogs) {
		this.companyDocumentLogs = companyDocumentLogs;
	}

	public String getSuccess() {
		return success;
	}

	public void setSuccess(String success) {
		this.success = success;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	private CommonsMultipartFile fileData;

	private String companyDocumentStatus;

	public String getCompanyDocumentStatus() {
		return companyDocumentStatus;
	}

	public void setCompanyDocumentStatus(String companyDocumentStatus) {
		this.companyDocumentStatus = companyDocumentStatus;
	}

	/*public List<CompanyDocumentCenterForm> getCompanyDocumentCenterFormList() {
		return companyDocumentCenterFormList;
	}

	public void setCompanyDocumentCenterFormList(
			List<CompanyDocumentCenterForm> companyDocumentCenterFormList) {
		this.companyDocumentCenterFormList = companyDocumentCenterFormList;
	}*/

	public String getTabName() {
		return tabName;
	}

	public void setTabName(String tabName) {
		this.tabName = tabName;
	}

	/*public List<CompanyDocumentCenterForm> getXmlList() {
		return xmlList;
	}

	public void setXmlList(List<CompanyDocumentCenterForm> xmlList) {
		this.xmlList = xmlList;
	}*/

	public String getXmlString() {
		return xmlString;
	}

	public void setXmlString(String xmlString) {
		this.xmlString = xmlString;
	}

	private Long categoryId;

	private String categoryName;

	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public long getDocId() {
		return docId;
	}

	public void setDocId(long docId) {
		this.docId = docId;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getClaimCategory() {
		return claimCategory;
	}

	public void setClaimCategory(Long claimCategory) {
		this.claimCategory = claimCategory;
	}

	public boolean isUploadTypeTaxPdfFile() {
		return isUploadTypeTaxPdfFile;
	}

	public void setUploadTypeTaxPdfFile(boolean isUploadTypeTaxPdfFile) {
		this.isUploadTypeTaxPdfFile = isUploadTypeTaxPdfFile;
	}

	public CompanyDocumentLogDTO getCompanyDocumentSummaryDTO() {
		return companyDocumentSummaryDTO;
	}

	public void setCompanyDocumentSummaryDTO(
			CompanyDocumentLogDTO companyDocumentSummaryDTO) {
		this.companyDocumentSummaryDTO = companyDocumentSummaryDTO;
	}


	public boolean isZipEntriesInvalid() {
		return zipEntriesInvalid;
	}

	public void setZipEntriesInvalid(boolean zipEntriesInvalid) {
		this.zipEntriesInvalid = zipEntriesInvalid;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMetaData() {
		return metaData;
	}

	public void setMetaData(String metaData) {
		this.metaData = metaData;
	}

	
}
