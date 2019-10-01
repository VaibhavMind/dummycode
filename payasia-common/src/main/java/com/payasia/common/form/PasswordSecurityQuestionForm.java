/**
 * @author abhisheksachdeva
 *
 */
package com.payasia.common.form;

import java.io.Serializable;

/**
 * The Class PasswordSecurityQuestionForm.
 */
public class PasswordSecurityQuestionForm implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5288311231317355866L;

	/** The pwd security question id. */
	private Long pwdSecurityQuestionId;

	/** The company id. */
	private Long companyId;

	/** The security question. */
	private String securityQuestion;

	/**
	 * Gets the pwd security question id.
	 * 
	 * @return the pwd security question id
	 */
	public  Long getPwdSecurityQuestionId() {
		return pwdSecurityQuestionId;
	}

	/**
	 * Sets the pwd security question id.
	 * 
	 * @param pwdSecurityQuestionId
	 *            the new pwd security question id
	 */
	public void setPwdSecurityQuestionId(Long pwdSecurityQuestionId) {
		this.pwdSecurityQuestionId = pwdSecurityQuestionId;
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
	 * @param companyId
	 *            the new company id
	 */
	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	/**
	 * Gets the security question.
	 * 
	 * @return the security question
	 */
	public String getSecurityQuestion() {
		return securityQuestion;
	}

	/**
	 * Sets the security question.
	 * 
	 * @param securityQuestion
	 *            the new security question
	 */
	public void setSecurityQuestion(String securityQuestion) {
		this.securityQuestion = securityQuestion;
	}

}
