package com.payasia.common.form;

import java.io.Serializable;

/**
 * The Class CompanyListForm.
 */
public class CompanyListForm implements Serializable{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 7487133273442955818L;

	/** The group id. */
	private Long groupId;
	
	/** The group name. */
	private String groupName;
	
	/** The company id. */
	private Long companyId;
	
	/** The company name. */
	private String companyName;
	
	/** The bank id. */
	private Long bankId;
	
	/** The contact person. */
	private String contactPerson;
	
	/** The contact person phone. */
	private String contactPersonPhone;
	
	/** The company domain. */
	private String companyDomain;
	
	/** The address1. */
	private String address1;
	
	/** The address2. */
	private String address2;
	
	/** The city. */
	private String city;
	
	/** The email. */
	private String email;
	
	/** The phone. */
	private String phone;
	
	/** The fax. */
	private String fax;
	
	/** The zip code. */
	private String zipCode;
	
	/** The state. */
	private String state;
	
	/** The state id. */
	private Long stateId;
	
	/** The country. */
	private String country;
	
	/** The country id. */
	private Long countryId;
	
	/** The financial year id. */
	private long financialYearId;
	

	

	/**
	 * Gets the contact person phone.
	 *
	 * @return the contact person phone
	 */
	public String getContactPersonPhone() {
		return contactPersonPhone;
	}

	/**
	 * Sets the contact person phone.
	 *
	 * @param contactPersonPhone the new contact person phone
	 */
	public void setContactPersonPhone(String contactPersonPhone) {
		this.contactPersonPhone = contactPersonPhone;
	}

	/**
	 * Gets the address1.
	 *
	 * @return the address1
	 */
	public String getAddress1() {
		return address1;
	}

	/**
	 * Sets the address1.
	 *
	 * @param address1 the new address1
	 */
	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	/**
	 * Gets the address2.
	 *
	 * @return the address2
	 */
	public String getAddress2() {
		return address2;
	}

	/**
	 * Sets the address2.
	 *
	 * @param address2 the new address2
	 */
	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	/**
	 * Gets the city.
	 *
	 * @return the city
	 */
	public String getCity() {
		return city;
	}

	/**
	 * Sets the city.
	 *
	 * @param city the new city
	 */
	public void setCity(String city) {
		this.city = city;
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
	 * @param email the new email
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Gets the company name.
	 *
	 * @return the company name
	 */
	public String getCompanyName() {
		return companyName;
	}

	/**
	 * Sets the company name.
	 *
	 * @param companyName the new company name
	 */
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	/**
	 * Gets the contact person.
	 *
	 * @return the contact person
	 */
	public String getContactPerson() {
		return contactPerson;
	}

	/**
	 * Sets the contact person.
	 *
	 * @param contactPerson the new contact person
	 */
	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
	}

	/**
	 * Gets the serialversionuid.
	 *
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
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
	 * @param phone the new phone
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	/**
	 * Gets the fax.
	 *
	 * @return the fax
	 */
	public String getFax() {
		return fax;
	}

	/**
	 * Sets the fax.
	 *
	 * @param fax the new fax
	 */
	public void setFax(String fax) {
		this.fax = fax;
	}

	/**
	 * Sets the company domain.
	 *
	 * @param companyDomain the new company domain
	 */
	public void setCompanyDomain(String companyDomain) {
		this.companyDomain = companyDomain;
	}

	/**
	 * Gets the company domain.
	 *
	 * @return the company domain
	 */
	public String getCompanyDomain() {
		return companyDomain;
	}

	/**
	 * Gets the group name.
	 *
	 * @return the group name
	 */
	public String getGroupName() {
		return groupName;
	}

	/**
	 * Sets the group name.
	 *
	 * @param groupName the new group name
	 */
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	/**
	 * Gets the group id.
	 *
	 * @return the group id
	 */
	public Long getGroupId() {
		return groupId;
	}

	/**
	 * Sets the group id.
	 *
	 * @param groupId the new group id
	 */
	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	/**
	 * Gets the state.
	 *
	 * @return the state
	 */
	public String getState() {
		return state;
	}

	/**
	 * Sets the state.
	 *
	 * @param state the new state
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * Gets the country.
	 *
	 * @return the country
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * Sets the country.
	 *
	 * @param country the new country
	 */
	public void setCountry(String country) {
		this.country = country;
	}

	/**
	 * Gets the zip code.
	 *
	 * @return the zip code
	 */
	public String getZipCode() {
		return zipCode;
	}

	/**
	 * Sets the zip code.
	 *
	 * @param zipCode the new zip code
	 */
	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	/**
	 * Gets the country id.
	 *
	 * @return the country id
	 */
	public Long getCountryId() {
		return countryId;
	}

	/**
	 * Sets the country id.
	 *
	 * @param countryId the new country id
	 */
	public void setCountryId(Long countryId) {
		this.countryId = countryId;
	}

	/**
	 * Gets the state id.
	 *
	 * @return the state id
	 */
	public Long getStateId() {
		return stateId;
	}

	/**
	 * Sets the state id.
	 *
	 * @param stateId the new state id
	 */
	public void setStateId(Long stateId) {
		this.stateId = stateId;
	}

	/**
	 * Gets the financial year id.
	 *
	 * @return the financial year id
	 */
	public long getFinancialYearId() {
		return financialYearId;
	}

	/**
	 * Sets the financial year id.
	 *
	 * @param financialYearId the new financial year id
	 */
	public void setFinancialYearId(long financialYearId) {
		this.financialYearId = financialYearId;
	}

	/**
	 * Gets the company id.
	 *
	 * @return the company id
	 */
	public Long getCompanyId() {
		return companyId;
	}

	/**
	 * Sets the company id.
	 *
	 * @param companyId the new company id
	 */
	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	/**
	 * Gets the bank id.
	 *
	 * @return the bank id
	 */
	public Long getBankId() {
		return bankId;
	}

	/**
	 * Sets the bank id.
	 *
	 * @param bankId the new bank id
	 */
	public void setBankId(Long bankId) {
		this.bankId = bankId;
	}

}
