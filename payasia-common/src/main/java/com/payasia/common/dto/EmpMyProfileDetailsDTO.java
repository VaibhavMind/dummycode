package com.payasia.common.dto;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmpMyProfileDetailsDTO  implements Serializable{

	private String firstName;
	private String middleName;
	private String lastName;
	private String email;
	private String hiredate;
    private String confirmationdate;
	private String terminationdate;
	private String originalhiredate;
	private String yearsofservice;
	private String employmentstatus;
	private List<CustFieldsDTO> listCustomFields;
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getMiddleName() {
		return middleName;
	}
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getHiredate() {
		return hiredate;
	}
	public void setHiredate(String hiredate) {
		this.hiredate = hiredate;
	}
	public String getConfirmationdate() {
		return confirmationdate;
	}
	public void setConfirmationdate(String confirmationdate) {
		this.confirmationdate = confirmationdate;
	}
	public String getTerminationdate() {
		return terminationdate;
	}
	public void setTerminationdate(String terminationdate) {
		this.terminationdate = terminationdate;
	}
	public String getOriginalhiredate() {
		return originalhiredate;
	}
	public void setOriginalhiredate(String originalhiredate) {
		this.originalhiredate = originalhiredate;
	}
	public String getYearsofservice() {
		return yearsofservice;
	}
	public void setYearsofservice(String yearsofservice) {
		this.yearsofservice = yearsofservice;
	}
	public String getEmploymentstatus() {
		return employmentstatus;
	}
	public void setEmploymentstatus(String employmentstatus) {
		this.employmentstatus = employmentstatus;
	}
	public List<CustFieldsDTO> getListCustomFields() {
		return listCustomFields;
	}
	public void setListCustomFields(List<CustFieldsDTO> listCustomFields) {
		this.listCustomFields = listCustomFields;
	}
	
}
