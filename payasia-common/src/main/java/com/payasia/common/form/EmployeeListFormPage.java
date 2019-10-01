package com.payasia.common.form;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import com.payasia.common.dto.EmployeeDocumentHistoryDTO;
import com.payasia.common.dto.EmployeeFieldDTO;

 
/**
 * The Class EmployeeListFormPage.
 */
public class EmployeeListFormPage extends PageResponse implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The employee list from. */

	private String empId;
	private String firstName;
	private String middleName;
	private String lastName;
	private String dob;
	private String email;
	private String password;

	private String employeeNumberStatus;
	private Long uniqueEmployeeId;
	private Long dynamicFormRecordId;
	private Long dynamicFormTableRecordId;
	private String mode;
	
	private String deletedMsg;
	private String fileSizeStatus;
	private Integer maxFileSize;
	private String employeeDeleted;
	private String status;
	private String message;
	
	private String hireDate;
	private String confirmationDate;
	private String resignationDate;
	private String originalHireDate;
	private String xmlString;
	private Long tabId;
	
	private byte[] image;
	private String fileType;
	private String employmentStatus;
	
	private String oldValue;
	private String newValue;
	
	private String empLstOldValue;
	private String empLstNewValue;
	
	private String fieldName;
	
	private List<EmployeeDocumentHistoryDTO> empDocHistoryList;
	private List<EmployeeFieldDTO> employees;
	
	

	public String getOldValue() {
		return oldValue;
	}

	public void setOldValue(String oldValue) {
		this.oldValue = oldValue;
	}

	public String getNewValue() {
		return newValue;
	}

	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getEmploymentStatus() {
		return employmentStatus;
	}

	public void setEmploymentStatus(String employmentStatus) {
		this.employmentStatus = employmentStatus;
	}

	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		if (image != null) {
			this.image = Arrays.copyOf(image, image.length);
		}
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public Long getTabId() {
		return tabId;
	}

	public void setTabId(Long tabId) {
		this.tabId = tabId;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getXmlString() {
		return xmlString;
	}

	public void setXmlString(String xmlString) {
		this.xmlString = xmlString;
	}

	public String getHireDate() {
		return hireDate;
	}

	public void setHireDate(String hireDate) {
		this.hireDate = hireDate;
	}

	public String getConfirmationDate() {
		return confirmationDate;
	}

	public void setConfirmationDate(String confirmationDate) {
		this.confirmationDate = confirmationDate;
	}

	public String getResignationDate() {
		return resignationDate;
	}

	public void setResignationDate(String resignationDate) {
		this.resignationDate = resignationDate;
	}

	public String getOriginalHireDate() {
		return originalHireDate;
	}

	public void setOriginalHireDate(String originalHireDate) {
		this.originalHireDate = originalHireDate;
	}

	public String getEmployeeDeleted() {
		return employeeDeleted;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getMaxFileSize() {
		return maxFileSize;
	}

	public void setMaxFileSize(Integer maxFileSize) {
		this.maxFileSize = maxFileSize;
	}

	public String getFileSizeStatus() {
		return fileSizeStatus;
	}

	public void setFileSizeStatus(String fileSizeStatus) {
		this.fileSizeStatus = fileSizeStatus;
	}

	public String getDeletedMsg() {
		return deletedMsg;
	}

	public void setDeletedMsg(String deletedMsg) {
		this.deletedMsg = deletedMsg;
	}

	public Long getDynamicFormRecordId() {
		return dynamicFormRecordId;
	}

	public void setDynamicFormRecordId(Long dynamicFormRecordId) {
		this.dynamicFormRecordId = dynamicFormRecordId;
	}

	public Long getUniqueEmployeeId() {
		return uniqueEmployeeId;
	}

	public void setUniqueEmployeeId(Long uniqueEmployeeId) {
		this.uniqueEmployeeId = uniqueEmployeeId;
	}

	public String getEmployeeNumberStatus() {
		return employeeNumberStatus;
	}

	public void setEmployeeNumberStatus(String employeeNumberStatus) {
		this.employeeNumberStatus = employeeNumberStatus;
	}

	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	private List<EmployeeListForm> employeeListFrom;

	private List<EmployeeListForm> tableDataList;

	public List<EmployeeListForm> getTableDataList() {
		return tableDataList;
	}

	public void setTableDataList(List<EmployeeListForm> tableDataList) {
		this.tableDataList = tableDataList;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/**
	 * Gets the employee list from.
	 * 
	 * @return the employee list from
	 */
	public List<EmployeeListForm> getEmployeeListFrom() {
		return employeeListFrom;
	}

	/**
	 * Sets the employee list from.
	 * 
	 * @param employeeListFrom
	 *            the new employee list from
	 */
	public void setEmployeeListFrom(List<EmployeeListForm> employeeListFrom) {
		this.employeeListFrom = employeeListFrom;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String isEmployeeDeleted() {
		return employeeDeleted;
	}

	public void setEmployeeDeleted(String employeeDeleted) {
		this.employeeDeleted = employeeDeleted;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Long getDynamicFormTableRecordId() {
		return dynamicFormTableRecordId;
	}

	public void setDynamicFormTableRecordId(Long dynamicFormTableRecordId) {
		this.dynamicFormTableRecordId = dynamicFormTableRecordId;
	}

	public List<EmployeeDocumentHistoryDTO> getEmpDocHistoryList() {
		return empDocHistoryList;
	}

	public void setEmpDocHistoryList(List<EmployeeDocumentHistoryDTO> empDocHistoryList) {
		this.empDocHistoryList = empDocHistoryList;
	}

	public List<EmployeeFieldDTO> getEmployees() {
		return employees;
	}

	public void setEmployees(List<EmployeeFieldDTO> employees) {
		this.employees = employees;
	}

	public String getEmpLstOldValue() {
		return empLstOldValue;
	}

	public void setEmpLstOldValue(String empLstOldValue) {
		this.empLstOldValue = empLstOldValue;
	}

	public String getEmpLstNewValue() {
		return empLstNewValue;
	}

	public void setEmpLstNewValue(String empLstNewValue) {
		this.empLstNewValue = empLstNewValue;
	}
	
	
	
	

}
