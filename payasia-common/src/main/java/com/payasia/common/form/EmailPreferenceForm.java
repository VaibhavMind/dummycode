/**
 * @author abhisheksachdeva
 *
 */
package com.payasia.common.form;

import java.io.Serializable;

/**
 * The Class EmailPreferenceForm.
 */
public class EmailPreferenceForm implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7402763701339547671L;

	/** The email preference id. */
	private int emailPreferenceId;

	/** The company id. */
	private int companyId;

	/** The system email. */
	private String systemEmail;

	/** The contact email. */
	private String contactEmail;

	/** The logo location. */
	private String logoLocation;

	/** The system sending email. */
	private String systemSendingEmail;
	
	private String companyURL;
	
	

	public String getCompanyURL() {
		return companyURL;
	}

	public void setCompanyURL(String companyURL) {
		this.companyURL = companyURL;
	}

	/**
	 * Gets the email preference id.
	 * 
	 * @return the email preference id
	 */
	public int getEmailPreferenceId() {
		return emailPreferenceId;
	}

	/**
	 * Sets the email preference id.
	 * 
	 * @param emailPreferenceId
	 *            the new email preference id
	 */
	public void setEmailPreferenceId(int emailPreferenceId) {
		this.emailPreferenceId = emailPreferenceId;
	}

	/**
	 * Gets the company id.
	 * 
	 * @return the company id
	 */
	public int getCompanyId() {
		return companyId;
	}

	/**
	 * Sets the company id.
	 * 
	 * @param companyId
	 *            the new company id
	 */
	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}

	/**
	 * Gets the system email.
	 * 
	 * @return the system email
	 */
	public String getSystemEmail() {
		return systemEmail;
	}

	/**
	 * Sets the system email.
	 * 
	 * @param systemEmail
	 *            the new system email
	 */
	public void setSystemEmail(String systemEmail) {
		this.systemEmail = systemEmail;
	}

	/**
	 * Gets the contact email.
	 * 
	 * @return the contact email
	 */
	public String getContactEmail() {
		return contactEmail;
	}

	/**
	 * Sets the contact email.
	 * 
	 * @param contactEmail
	 *            the new contact email
	 */
	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}

	/**
	 * Gets the logo location.
	 * 
	 * @return the logo location
	 */
	public String getLogoLocation() {
		return logoLocation;
	}

	/**
	 * Sets the logo location.
	 * 
	 * @param logoLocation
	 *            the new logo location
	 */
	public void setLogoLocation(String logoLocation) {
		this.logoLocation = logoLocation;
	}

	/**
	 * Gets the system sending email.
	 * 
	 * @return the system sending email
	 */
	public String getSystemSendingEmail() {
		return systemSendingEmail;
	}

	/**
	 * Sets the system sending email.
	 * 
	 * @param systemSendingEmail
	 *            the new system sending email
	 */
	public void setSystemSendingEmail(String systemSendingEmail) {
		this.systemSendingEmail = systemSendingEmail;
	}

}
