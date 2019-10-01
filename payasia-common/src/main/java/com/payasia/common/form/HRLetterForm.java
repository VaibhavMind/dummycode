package com.payasia.common.form;

import java.io.Serializable;

//import org.codehaus.jackson.annotate.JsonIgnore;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * The Class HRLetterForm.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
public class HRLetterForm implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2385109778792492828L;

	/** The letter id. */
	private long letterId;
	private String  employeeNumber;
	private long employeeId;
	/** The company id. */
	private long companyId;
	
	private String encodedBodyString;
	
	private Boolean action;

	private boolean isSaveInDocumentCenter;
	
	public String getEmployeeNumber() {
		return employeeNumber;
	}

	public void setEmployeeNumber(String employeeNumber) {
		this.employeeNumber = employeeNumber;
	}

	public long getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(long employeeId) {
		this.employeeId = employeeId;
	}

	/** The letter name. */
	private String letterName;

	/** The letter description. */
	
	
	private String letterDescription;

	private String subject;
	
	private String body;
	
	/** The active. */
	private boolean active;
	
	/** The active. */
	private String isActive;

	/** The hr letter section list. */

	private String shortlist;
	

	/**
	 * Gets the letter id.
	 * 
	 * @return the letter id
	 */
	public long getLetterId() {
		return letterId;
	}

	/**
	 * Sets the letter id.
	 * 
	 * @param letterId
	 *            the new letter id
	 */
	public void setLetterId(long letterId) {
		this.letterId = letterId;
	}

	/**
	 * Gets the company id.
	 * 
	 * @return the company id
	 */
	public long getCompanyId() {
		return companyId;
	}

	/**
	 * Sets the company id.
	 * 
	 * @param companyId
	 *            the new company id
	 */
	public void setCompanyId(long companyId) {
		this.companyId = companyId;
	}

	/**
	 * Gets the letter name.
	 * 
	 * @return the letter name
	 */
	public String getLetterName() {
		return letterName;
	}

	/**
	 * Sets the letter name.
	 * 
	 * @param letterName
	 *            the new letter name
	 */
	public void setLetterName(String letterName) {
		this.letterName = letterName;
	}

	/**
	 * Gets the letter description.
	 * 
	 * @return the letter description
	 */
	public String getLetterDescription() {
		return letterDescription;
	}

	/**
	 * Sets the letter description.
	 * 
	 * @param letterDescription
	 *            the new letter description
	 */
	public void setLetterDescription(String letterDescription) {
		this.letterDescription = letterDescription;
	}


	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getIsActive() {
		return isActive;
	}
	
	public void setIsActive(String isActive) {
		this.isActive = isActive;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	
	public String getShortlist() {
		return shortlist;
	}
	
	public void setShortlist(String shortlist) {
		this.shortlist = shortlist;
	}

	public String getEncodedBodyString() {
		return encodedBodyString;
	}

	public void setEncodedBodyString(String encodedBodyString) {
		this.encodedBodyString = encodedBodyString;
	}

	public Boolean getAction() {
		return action;
	}

	public void setAction(Boolean action) {
		this.action = action;
	}

	public boolean isSaveInDocumentCenter() {
		return isSaveInDocumentCenter;
	}

	public void setSaveInDocumentCenter(boolean isSaveInDocumentCenter) {
		this.isSaveInDocumentCenter = isSaveInDocumentCenter;
	}


	

}
