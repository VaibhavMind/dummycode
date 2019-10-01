package com.payasia.common.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PayslipDTO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1929933474048455169L;
	/**
	 * 
	 */
	
	private String payslipName;
	private Long payslipId;
	private String payslipType;
	private Long monthId;
	private String monthName;
	private Integer year;
	private Integer part;
	private String employeeNumber;
	private Long employeeId;
	private String firstName;
	private String lastName;
	private String email;
	private String middleName;
	private String loginName;
	private String companyName;
	private Boolean action;
	private String fullName;
	private String releaseDate; 
	 
	
	public String getPayslipName() {
		return payslipName;
	}

	public void setPayslipName(String payslipName) {
		this.payslipName = payslipName;
	}

	public String getPayslipType() {
		return payslipType;
	}

	public void setPayslipType(String payslipType) {
		this.payslipType = payslipType;
	}

	public Long getPayslipId() {
		return payslipId;
	}

	public void setPayslipId(Long payslipId) {
		this.payslipId = payslipId;
	}

	

	public Integer getYear() {
		return year;
	}
	

	public void setYear(Integer year) {
		this.year = year;
	}

	public Long getMonthId() {
		return monthId;
	}

	public void setMonthId(Long monthId) {
		this.monthId = monthId;
	}

	public Integer getPart() {
		return part;
	}

	public void setPart(Integer part) {
		this.part = part;
	}

	public String getEmployeeNumber() {
		return employeeNumber;
	}

	public void setEmployeeNumber(String employeeNumber) {
		this.employeeNumber = employeeNumber;
	}

	public Long getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}


	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	
	
	public String getMonthName() {
		return monthName;
	}

	public void setMonthName(String monthName) {
		this.monthName = monthName;
	}

	public Boolean getAction() {
		return action;
	}

	public void setAction(Boolean action) {
		this.action = action;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(String releaseDate) {
		this.releaseDate = releaseDate;
	}

	

}
