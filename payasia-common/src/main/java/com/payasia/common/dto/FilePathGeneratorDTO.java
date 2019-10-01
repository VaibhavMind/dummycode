package com.payasia.common.dto;

import java.io.Serializable;

public class FilePathGeneratorDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	private String downloadPath;
	private String rootDirectoryName;
	private long companyId;
	private String docDirectoryName;
	private String fileName;
	private String year;
	private String employeeNumber;
	private String fileExtension;
	private String eventName;
	private long topicId;
	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getDownloadPath() {
		return downloadPath;
	}

	public void setDownloadPath(String downloadPath) {
		this.downloadPath = downloadPath;
	}

	public String getRootDirectoryName() {
		return rootDirectoryName;
	}

	public void setRootDirectoryName(String rootDirectoryName) {
		this.rootDirectoryName = rootDirectoryName;
	}

	public long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(long companyId) {
		this.companyId = companyId;
	}

	public String getDocDirectoryName() {
		return docDirectoryName;
	}

	public void setDocDirectoryName(String docDirectoryName) {
		this.docDirectoryName = docDirectoryName;
	}

	public String getEmployeeNumber() {
		return employeeNumber;
	}

	public void setEmployeeNumber(String employeeNumber) {
		this.employeeNumber = employeeNumber;
	}

	public String getFileExtension() {
		return fileExtension;
	}

	public void setFileExtension(String fileExtension) {
		this.fileExtension = fileExtension;
	}

	public String getEventName() {
		return eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	public long getTopicId() {
		return topicId;
	}

	public void setTopicId(long topicId) {
		this.topicId = topicId;
	}
}
