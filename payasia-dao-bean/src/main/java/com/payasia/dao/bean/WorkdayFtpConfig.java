package com.payasia.dao.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "Workday_FTP_Config")
public class WorkdayFtpConfig extends BaseEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3770185186482693669L;

	@Id
	@GeneratedValue
	@Column(name = "Workday_FTP_Config_ID")
	private Long workdayFtpConfigId;

	@ManyToOne
	@JoinColumn(name = "Company_ID")
	private Company company;

	@Column(name = "FTP_Server_Address")
	private String ftpServerAddress;

	@Column(name = "FTP_Port")
	private Integer ftpPort;

	@Column(name = "Username")
	private String userName;

	@Column(name = "Password")
	private String password;
	
	@Column(name = "DateFormat")
	private String dateFormat;
	
	@ManyToOne
	@JoinColumn(name = "Protocol")
	private AppCodeMaster protocol;
	
	@Column(name = "EncryptionType")
	private String encryptionType;
	
	@Column(name = "PGP_Password")
	private String pgpPassword;

	@Column(name = "Employee_Data_Remote_Path")
	private String employeeDataRemotePath;
	
	@Column(name = "Employee_Data_Move_To_Folder_Path")
	private String employeeDataMoveToFolderPath;
	
	@Column(name = "Employee_Data_Frequency")
	private String employeeDataFrequency;
	
	@Column(name = "Employee_Data_Is_Active")
	private boolean employeeDataIsActive;

	@Column(name = "Payroll_Data_Remote_Path")
	private String payrollDataRemotePath;
	
	@Column(name = "Payroll_Data_Move_To_Folder_Path")
	private String payrollDataMoveToFolderPath;
	
	@Column(name = "Payroll_Data_Frequency")
	private String payrollDataFrequency;
	
	@Column(name = "Payroll_Data_Is_Active")
	private boolean payrollDataIsActive;

	public Long getWorkdayFtpConfigId() {
		return workdayFtpConfigId;
	}

	public void setWorkdayFtpConfigId(Long workdayFtpConfigId) {
		this.workdayFtpConfigId = workdayFtpConfigId;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public String getFtpServerAddress() {
		return ftpServerAddress;
	}

	public void setFtpServerAddress(String ftpServerAddress) {
		this.ftpServerAddress = ftpServerAddress;
	}

	public Integer getFtpPort() {
		return ftpPort;
	}

	public void setFtpPort(Integer ftpPort) {
		this.ftpPort = ftpPort;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	public AppCodeMaster getProtocol() {
		return protocol;
	}

	public void setProtocol(AppCodeMaster protocol) {
		this.protocol = protocol;
	}

	public String getEncryptionType() {
		return encryptionType;
	}

	public void setEncryptionType(String encryptionType) {
		this.encryptionType = encryptionType;
	}

	public String getPgpPassword() {
		return pgpPassword;
	}

	public void setPgpPassword(String pgpPassword) {
		this.pgpPassword = pgpPassword;
	}

	public String getEmployeeDataRemotePath() {
		return employeeDataRemotePath;
	}

	public void setEmployeeDataRemotePath(String employeeDataRemotePath) {
		this.employeeDataRemotePath = employeeDataRemotePath;
	}

	public String getEmployeeDataMoveToFolderPath() {
		return employeeDataMoveToFolderPath;
	}

	public void setEmployeeDataMoveToFolderPath(String employeeDataMoveToFolderPath) {
		this.employeeDataMoveToFolderPath = employeeDataMoveToFolderPath;
	}

	public String getEmployeeDataFrequency() {
		return employeeDataFrequency;
	}

	public void setEmployeeDataFrequency(String employeeDataFrequency) {
		this.employeeDataFrequency = employeeDataFrequency;
	}

	public boolean isEmployeeDataIsActive() {
		return employeeDataIsActive;
	}

	public void setEmployeeDataIsActive(boolean employeeDataIsActive) {
		this.employeeDataIsActive = employeeDataIsActive;
	}

	public String getPayrollDataRemotePath() {
		return payrollDataRemotePath;
	}

	public void setPayrollDataRemotePath(String payrollDataRemotePath) {
		this.payrollDataRemotePath = payrollDataRemotePath;
	}

	public String getPayrollDataMoveToFolderPath() {
		return payrollDataMoveToFolderPath;
	}

	public void setPayrollDataMoveToFolderPath(String payrollDataMoveToFolderPath) {
		this.payrollDataMoveToFolderPath = payrollDataMoveToFolderPath;
	}

	public String getPayrollDataFrequency() {
		return payrollDataFrequency;
	}

	public void setPayrollDataFrequency(String payrollDataFrequency) {
		this.payrollDataFrequency = payrollDataFrequency;
	}

	public boolean isPayrollDataIsActive() {
		return payrollDataIsActive;
	}

	public void setPayrollDataIsActive(boolean payrollDataIsActive) {
		this.payrollDataIsActive = payrollDataIsActive;
	}

}
