package com.payasia.common.form;

import java.io.Serializable;
import java.util.Arrays;

import org.springframework.web.multipart.commons.CommonsMultipartFile;


public class DynamicFormTableDocumentDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7970322166557217305L;
	private String documentName;
	private String documentType;
	private String documentDescription;
	private CommonsMultipartFile uploadFile;
	private String uploadedDate;
	private String uploadedBy;
	private boolean visibleToEmployee;
	
	private String tabName;
	private Long tabId;
	private String tableName;
	private String tableFieldType;
	private int seqNo;
	private int version;
	private Long formId;
	private String entityKey;
	
	private Long tableRecordId;
	private String fileName;
	private byte[] attachmentBytes;
	
	private String owner;
	
	public Long getFormId() {
		return formId;
	}
	public void setFormId(Long formId) {
		this.formId = formId;
	}
	public String getDocumentName() {
		return documentName;
	}
	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}
	public String getDocumentType() {
		return documentType;
	}
	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}
	
	public CommonsMultipartFile getUploadFile() {
		return uploadFile;
	}
	public void setUploadFile(CommonsMultipartFile uploadFile) {
		this.uploadFile = uploadFile;
	}
	public String getUploadedDate() {
		return uploadedDate;
	}
	public String getDocumentDescription() {
		return documentDescription;
	}
	public void setDocumentDescription(String documentDescription) {
		this.documentDescription = documentDescription;
	}
	public void setUploadedDate(String uploadedDate) {
		this.uploadedDate = uploadedDate;
	}
	public String getUploadedBy() {
		return uploadedBy;
	}
	public void setUploadedBy(String uploadedBy) {
		this.uploadedBy = uploadedBy;
	}
	public boolean isVisibleToEmployee() {
		return visibleToEmployee;
	}
	public void setVisibleToEmployee(boolean visibleToEmployee) {
		this.visibleToEmployee = visibleToEmployee;
	}
	public String getTabName() {
		return tabName;
	}
	public void setTabName(String tabName) {
		this.tabName = tabName;
	}
	public Long getTabId() {
		return tabId;
	}
	public void setTabId(Long tabId) {
		this.tabId = tabId;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getTableFieldType() {
		return tableFieldType;
	}
	public void setTableFieldType(String tableFieldType) {
		this.tableFieldType = tableFieldType;
	}
	
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	public int getSeqNo() {
		return seqNo;
	}
	public void setSeqNo(int seqNo) {
		this.seqNo = seqNo;
	}
	public String getEntityKey() {
		return entityKey;
	}
	public void setEntityKey(String entityKey) {
		this.entityKey = entityKey;
	}
	public byte[] getAttachmentBytes() {
		return attachmentBytes;
	}
	public void setAttachmentBytes(byte[] attachmentBytes) {
		if (attachmentBytes != null) {
			this.attachmentBytes = Arrays.copyOf(attachmentBytes, attachmentBytes.length);
		}
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public Long getTableRecordId() {
		return tableRecordId;
	}
	public void setTableRecordId(Long tableRecordId) {
		this.tableRecordId = tableRecordId;
	}
	
	
	
	
}
