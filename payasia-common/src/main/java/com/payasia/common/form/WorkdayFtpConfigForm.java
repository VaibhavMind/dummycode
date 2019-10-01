package com.payasia.common.form;

import java.io.Serializable;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.payasia.common.dto.DateFormatDTO;

public class WorkdayFtpConfigForm implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String ftpServer;
	private int ftpPort;
	private String username;
	private String ftpPassword;
	private String protocol;
	private CommonsMultipartFile publicKey;
	private String encryptionType;
	private CommonsMultipartFile pgpPrivateKey;
	private String pgpPassword;
	private DateFormatDTO dateFormat;
	private WorkdayDataFtpConfigForm employeeDataFtpConfig;
	private WorkdayDataFtpConfigForm payrollTransactionDataFtpConfig;
	
	public String getFtpServer() {
		return ftpServer;
	}
	public void setFtpServer(String ftpServer) {
		this.ftpServer = ftpServer;
	}
	public int getFtpPort() {
		return ftpPort;
	}
	public void setFtpPort(int ftpPort) {
		this.ftpPort = ftpPort;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getFtpPassword() {
		return ftpPassword;
	}
	public void setFtpPassword(String ftpPassword) {
		this.ftpPassword = ftpPassword;
	}
	public String getProtocol() {
		return protocol;
	}
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	public CommonsMultipartFile getPublicKey() {
		return publicKey;
	}
	public void setPublicKey(CommonsMultipartFile publicKey) {
		this.publicKey = publicKey;
	}
	public String getEncryptionType() {
		return encryptionType;
	}
	public void setEncryptionType(String encryptionType) {
		this.encryptionType = encryptionType;
	}
	public CommonsMultipartFile getPgpPrivateKey() {
		return pgpPrivateKey;
	}
	public void setPgpPrivateKey(CommonsMultipartFile pgpPrivateKey) {
		this.pgpPrivateKey = pgpPrivateKey;
	}
	public String getPgpPassword() {
		return pgpPassword;
	}
	public void setPgpPassword(String pgpPassword) {
		this.pgpPassword = pgpPassword;
	}
	public DateFormatDTO getDateFormat() {
		return dateFormat;
	}
	public void setDateFormat(DateFormatDTO dateFormat) {
		this.dateFormat = dateFormat;
	}
	public WorkdayDataFtpConfigForm getEmployeeDataFtpConfig() {
		return employeeDataFtpConfig;
	}
	public void setEmployeeDataFtpConfig(WorkdayDataFtpConfigForm employeeDataFtpConfig) {
		this.employeeDataFtpConfig = employeeDataFtpConfig;
	}
	public WorkdayDataFtpConfigForm getPayrollTransactionDataFtpConfig() {
		return payrollTransactionDataFtpConfig;
	}
	public void setPayrollTransactionDataFtpConfig(WorkdayDataFtpConfigForm payrollTransactionDataFtpConfig) {
		this.payrollTransactionDataFtpConfig = payrollTransactionDataFtpConfig;
	}
}
