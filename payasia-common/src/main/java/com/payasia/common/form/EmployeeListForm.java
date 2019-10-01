package com.payasia.common.form;

import java.io.Serializable;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.payasia.common.dto.EmployeeDynamicFormDTO;

 
/**
 * The Class EmployeeListForm.
 */
@JsonInclude(Include.NON_NULL)
public class EmployeeListForm implements Serializable{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 7487133273442955818L;

	/** The employee id. */
	private long employeeID;

	/** The employee name. */
	private String employeeName;

	/** The employee number. */
	private String employeeNumber;

	/** The first name. */
	private String firstName;

	/** The last name. */
	private String lastName;

	/** The middle name. */
	private String middleName;

	/** The company id. */
	private Integer companyID;

	/** The login name. */
	private String loginName;

	/** The password. */
	private String password;

	/** The dob. */
	private String dob;

	/** The marital status. */
	private String maritalStatus;

	/** The marital date. */
	private String maritalDate;

	/** The cob id. */
	private Integer cobID;

	/** The nationality. */
	private String nationality;

	/** The religion. */
	private String religion;

	/** The race. */
	private String race;

	/** The join date. */
	private String joinDate;

	/** The probation period. */
	private Integer probationPeriod;

	/** The confirmation date. */
	private String confirmationDate;

	/** The status. */
	private boolean status;

	private String statusMsg;

	/** The email. */
	private String email;

	/** The phone. */
	private String phone;

	/** The bank id. */
	private String bankID;

	/** The ques id. */
	private Integer quesID;

	/** The security answer. */
	private String securityAnswer;

	/** The decription. */
	private String decription;

	/** The serial no. */
	private String serialNo;

	/** The format. */
	private String format;

	/** The is active. */
	private String isActive;

	/** The show details. */
	private String showDetails;

	/** The search key. */
	private String searchKey;

	private String country;
	private Long countryId;

	
	private Long cmpID;
	private String companyName;
	private Integer tableRecordId;
	
	private CommonsMultipartFile employeeImage;
	private String originalHireDate;
	private String hireDate;
	private String resignationDate;
	
	private EmployeeDynamicFormDTO employeeDynamicFormDTO;
	
	private String employmentStatus;
	
	private boolean action;
	
	private byte[] empImage;
	
	
	public String getEmploymentStatus() {
		return employmentStatus;
	}

	public void setEmploymentStatus(String employmentStatus) {
		this.employmentStatus = employmentStatus;
	}

	public EmployeeDynamicFormDTO getEmployeeDynamicFormDTO() {
		return employeeDynamicFormDTO;
	}

	public void setEmployeeDynamicFormDTO(
			EmployeeDynamicFormDTO employeeDynamicFormDTO) {
		this.employeeDynamicFormDTO = employeeDynamicFormDTO;
	}

	public String getHireDate() {
		return hireDate;
	}

	public void setHireDate(String hireDate) {
		this.hireDate = hireDate;
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

	

	public String getStatusMsg() {
		return statusMsg;
	}

	public void setStatusMsg(String statusMsg) {
		this.statusMsg = statusMsg;
	}

	private String deletedMsg;

	public String getDeletedMsg() {
		return deletedMsg;
	}

	public void setDeletedMsg(String deletedMsg) {
		this.deletedMsg = deletedMsg;
	}

	

	public CommonsMultipartFile getEmployeeImage() {
		return employeeImage;
	}

	public void setEmployeeImage(CommonsMultipartFile employeeImage) {
		this.employeeImage = employeeImage;
	}

	

	public Integer getTableRecordId() {
		return tableRecordId;
	}

	public void setTableRecordId(Integer tableRecordId) {
		this.tableRecordId = tableRecordId;
	}

	

	public Long getCmpID() {
		return cmpID;
	}

	public void setCmpID(Long cmpID) {
		this.cmpID = cmpID;
	}

	
	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public Long getCountryId() {
		return countryId;
	}

	public void setCountryId(Long countryId) {
		this.countryId = countryId;
	}

	/**
	 * Gets the bank id.
	 * 
	 * @return the bank id
	 */
	public String getBankID() {
		return bankID;
	}

	/**
	 * Sets the bank id.
	 * 
	 * @param bankID
	 *            the new bank id
	 */
	public void setBankID(String bankID) {
		this.bankID = bankID;
	}

	/**
	 * Gets the marital date.
	 * 
	 * @return the marital date
	 */
	public String getMaritalDate() {
		return maritalDate;
	}

	/**
	 * Sets the marital date.
	 * 
	 * @param maritalDate
	 *            the new marital date
	 */
	public void setMaritalDate(String maritalDate) {
		this.maritalDate = maritalDate;
	}

	/**
	 * Gets the cob id.
	 * 
	 * @return the cob id
	 */
	public Integer getCobID() {
		return cobID;
	}

	/**
	 * Sets the cob id.
	 * 
	 * @param cobID
	 *            the new cob id
	 */
	public void setCobID(Integer cobID) {
		this.cobID = cobID;
	}

	/**
	 * Gets the nationality.
	 * 
	 * @return the nationality
	 */
	public String getNationality() {
		return nationality;
	}

	/**
	 * Sets the nationality.
	 * 
	 * @param nationality
	 *            the new nationality
	 */
	public void setNationality(String nationality) {
		this.nationality = nationality;
	}

	/**
	 * Gets the religion.
	 * 
	 * @return the religion
	 */
	public String getReligion() {
		return religion;
	}

	/**
	 * Sets the religion.
	 * 
	 * @param religion
	 *            the new religion
	 */
	public void setReligion(String religion) {
		this.religion = religion;
	}

	/**
	 * Gets the race.
	 * 
	 * @return the race
	 */
	public String getRace() {
		return race;
	}

	/**
	 * Sets the race.
	 * 
	 * @param race
	 *            the new race
	 */
	public void setRace(String race) {
		this.race = race;
	}

	/**
	 * Gets the probation period.
	 * 
	 * @return the probation period
	 */
	public Integer getProbationPeriod() {
		return probationPeriod;
	}

	/**
	 * Sets the probation period.
	 * 
	 * @param probationPeriod
	 *            the new probation period
	 */
	public void setProbationPeriod(Integer probationPeriod) {
		this.probationPeriod = probationPeriod;
	}

	/**
	 * Gets the confirmation date.
	 * 
	 * @return the confirmation date
	 */
	public String getConfirmationDate() {
		return confirmationDate;
	}

	/**
	 * Sets the confirmation date.
	 * 
	 * @param confirmationDate
	 *            the new confirmation date
	 */
	public void setConfirmationDate(String confirmationDate) {
		this.confirmationDate = confirmationDate;
	}

	/**
	 * Gets the email.
	 * 
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Sets the email.
	 * 
	 * @param email
	 *            the new email
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Gets the ques id.
	 * 
	 * @return the ques id
	 */
	public Integer getQuesID() {
		return quesID;
	}

	/**
	 * Sets the ques id.
	 * 
	 * @param quesID
	 *            the new ques id
	 */
	public void setQuesID(Integer quesID) {
		this.quesID = quesID;
	}

	/**
	 * Gets the security answer.
	 * 
	 * @return the security answer
	 */
	public String getSecurityAnswer() {
		return securityAnswer;
	}

	/**
	 * Sets the security answer.
	 * 
	 * @param securityAnswer
	 *            the new security answer
	 */
	public void setSecurityAnswer(String securityAnswer) {
		this.securityAnswer = securityAnswer;
	}

	/**
	 * Gets the company id.
	 * 
	 * @return the company id
	 */
	public Integer getCompanyID() {
		return companyID;
	}

	/**
	 * Sets the company id.
	 * 
	 * @param companyID
	 *            the new company id
	 */
	public void setCompanyID(Integer companyID) {
		this.companyID = companyID;
	}

	/**
	 * Gets the middle name.
	 * 
	 * @return the middle name
	 */
	public String getMiddleName() {
		return middleName;
	}

	/**
	 * Sets the middle name.
	 * 
	 * @param middleName
	 *            the new middle name
	 */
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	/**
	 * Gets the search key.
	 * 
	 * @return the search key
	 */
	public String getSearchKey() {
		return searchKey;
	}

	/**
	 * Sets the search key.
	 * 
	 * @param searchKey
	 *            the new search key
	 */
	public void setSearchKey(String searchKey) {
		this.searchKey = searchKey;
	}

	/**
	 * Gets the first name.
	 * 
	 * @return the first name
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * Sets the first name.
	 * 
	 * @param firstName
	 *            the new first name
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * Gets the last name.
	 * 
	 * @return the last name
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * Sets the last name.
	 * 
	 * @param lastName
	 *            the new last name
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * Gets the login name.
	 * 
	 * @return the login name
	 */
	public String getLoginName() {
		return loginName;
	}

	/**
	 * Sets the login name.
	 * 
	 * @param loginName
	 *            the new login name
	 */
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	/**
	 * Gets the password.
	 * 
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Sets the password.
	 * 
	 * @param password
	 *            the new password
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Gets the dob.
	 * 
	 * @return the dob
	 */
	public String getDob() {
		return dob;
	}

	/**
	 * Sets the dob.
	 * 
	 * @param dob
	 *            the new dob
	 */
	public void setDob(String dob) {
		this.dob = dob;
	}

	/**
	 * Gets the marital status.
	 * 
	 * @return the marital status
	 */
	public String getMaritalStatus() {
		return maritalStatus;
	}

	/**
	 * Sets the marital status.
	 * 
	 * @param maritalStatus
	 *            the new marital status
	 */
	public void setMaritalStatus(String maritalStatus) {
		this.maritalStatus = maritalStatus;
	}

	/**
	 * Gets the employee id.
	 * 
	 * @return the employee id
	 */
	public long getEmployeeID() {
		return employeeID;
	}

	/**
	 * Sets the employee id.
	 * 
	 * @param employeeID
	 *            the new employee id
	 */
	public void setEmployeeID(long employeeID) {
		this.employeeID = employeeID;
	}

	/**
	 * Gets the decription.
	 * 
	 * @return the decription
	 */
	public String getDecription() {
		return decription;
	}

	/**
	 * Sets the decription.
	 * 
	 * @param decription
	 *            the new decription
	 */
	public void setDecription(String decription) {
		this.decription = decription;
	}

	/**
	 * Gets the serial no.
	 * 
	 * @return the serial no
	 */
	public String getSerialNo() {
		return serialNo;
	}

	/**
	 * Sets the serial no.
	 * 
	 * @param serialNo
	 *            the new serial no
	 */
	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}

	/**
	 * Gets the format.
	 * 
	 * @return the format
	 */
	public String getFormat() {
		return format;
	}

	/**
	 * Sets the format.
	 * 
	 * @param format
	 *            the new format
	 */
	public void setFormat(String format) {
		this.format = format;
	}

	/**
	 * Gets the checks if is active.
	 * 
	 * @return the checks if is active
	 */
	public String getIsActive() {
		return isActive;
	}

	/**
	 * Sets the checks if is active.
	 * 
	 * @param isActive
	 *            the new checks if is active
	 */
	public void setIsActive(String isActive) {
		this.isActive = isActive;
	}

	/**
	 * Gets the show details.
	 * 
	 * @return the show details
	 */
	public String getShowDetails() {
		return showDetails;
	}

	/**
	 * Sets the show details.
	 * 
	 * @param showDetails
	 *            the new show details
	 */
	public void setShowDetails(String showDetails) {
		this.showDetails = showDetails;
	}

	/**
	 * Gets the employee name.
	 * 
	 * @return the employee name
	 */
	public String getEmployeeName() {
		return employeeName;
	}

	/**
	 * Sets the employee name.
	 * 
	 * @param employeeName
	 *            the new employee name
	 */
	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	/**
	 * Gets the employee number.
	 * 
	 * @return the employee number
	 */
	public String getEmployeeNumber() {
		return employeeNumber;
	}

	/**
	 * Sets the employee number.
	 * 
	 * @param employeeNumber
	 *            the new employee number
	 */
	public void setEmployeeNumber(String employeeNumber) {
		this.employeeNumber = employeeNumber;
	}

	/**
	 * Gets the join date.
	 * 
	 * @return the join date
	 */
	public String getJoinDate() {
		return joinDate;
	}

	/**
	 * Sets the join date.
	 * 
	 * @param joinDate
	 *            the new join date
	 */
	public void setJoinDate(String joinDate) {
		this.joinDate = joinDate;
	}

	/**
	 * Checks if is status.
	 * 
	 * @return true, if is status
	 */
	public boolean isStatus() {
		return status;
	}

	/**
	 * Sets the status.
	 * 
	 * @param status
	 *            the new status
	 */
	public void setStatus(boolean status) {
		this.status = status;
	}

	/**
	 * Gets the phone.
	 * 
	 * @return the phone
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * Sets the phone.
	 * 
	 * @param phone
	 *            the new phone
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	/**
	 * Gets the serialversionuid.
	 * 
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public boolean isAction() {
		return action;
	}

	public void setAction(boolean action) {
		this.action = action;
	}

	public byte[] getEmpImage() {
		return empImage;
	}

	public void setEmpImage(byte[] empImage) {
		this.empImage = empImage;
	}

}
