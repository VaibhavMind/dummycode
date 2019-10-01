/**
 * @author abhisheksachdeva
 *
 */
package com.payasia.common.dto;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

/**
 * The Class EmployeeConditionDTO.
 */
public class EmployeeConditionDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3625847092285952195L;

	/** The employee number. */
	private String employeeNumber;
	private String employeeId;
	private Long companyId;
	
	/** The employee name. */
	private String employeeName;
	private String email;
	private String fromDate;

    private String toDate;
	private String roleName;
	private String firstName;
	private String lastName;
	private Long groupId;
	private Boolean status;
	private List<Long> companyIds;
	private EmployeeShortListDTO employeeShortListDTO;
	
	private String middleName;
	private String hireDate;
	private String confirmationDate;
	private String originalHireDate;
	private String resignationDate;
	private String employmentStatus;
	
	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public List<Long> getCompanyIds() {
		return companyIds;
	}
	
	List<BigInteger> shortListEmployeeIds;
	private Boolean employeeShortList;
	

	public void setCompanyIds(List<Long> companyIds) {
		this.companyIds = companyIds;
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

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	

	public EmployeeShortListDTO getEmployeeShortListDTO() {
		return employeeShortListDTO;
	}

	public void setEmployeeShortListDTO(EmployeeShortListDTO employeeShortListDTO) {
		this.employeeShortListDTO = employeeShortListDTO;
	}

	

	public String getFromDate() {
		return fromDate;
	}

	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}

	public String getToDate() {
		return toDate;
	}

	public void setToDate(String toDate) {
		this.toDate = toDate;
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


	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public List<BigInteger> getShortListEmployeeIds() {
		return shortListEmployeeIds;
	}

	public void setShortListEmployeeIds(List<BigInteger> shortListEmployeeIds) {
		this.shortListEmployeeIds = shortListEmployeeIds;
	}

	public Boolean getEmployeeShortList() {
		return employeeShortList;
	}

	public void setEmployeeShortList(Boolean employeeShortList) {
		this.employeeShortList = employeeShortList;
	}

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	public String getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
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

	public String getOriginalHireDate() {
		return originalHireDate;
	}

	public void setOriginalHireDate(String originalHireDate) {
		this.originalHireDate = originalHireDate;
	}

	public String getResignationDate() {
		return resignationDate;
	}

	public void setResignationDate(String resignationDate) {
		this.resignationDate = resignationDate;
	}

	public String getEmploymentStatus() {
		return employmentStatus;
	}

	public void setEmploymentStatus(String employmentStatus) {
		this.employmentStatus = employmentStatus;
	}
	
	

}
