package com.payasia.common.form;

import java.io.Serializable;


/**
 * The Class EmployeeContactUSForm.
 */
public class EmployeeContactUSForm implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3137699706968771374L;

	/** The mail to. */
	private String mailTo;
	
	/** The mail cc. */
	private String mailCc;
	
	/** The mail subject. */
	private String mailSubject;
	
	/** The mail message. */
	private String mailMessage;
	
	/** The mail from. */
	private String mailFrom;
	private String turing;
	private String captchaId;
	
	/**
	 * Gets the mail to.
	 *
	 * @return the mail to
	 */
	public String getMailTo() {
		return mailTo;
	}
	
	/**
	 * Sets the mail to.
	 *
	 * @param mailTo the new mail to
	 */
	public void setMailTo(String mailTo) {
		this.mailTo = mailTo;
	}
	
	/**
	 * Gets the mail cc.
	 *
	 * @return the mail cc
	 */
	public String getMailCc() {
		return mailCc;
	}
	
	/**
	 * Sets the mail cc.
	 *
	 * @param mailCc the new mail cc
	 */
	public void setMailCc(String mailCc) {
		this.mailCc = mailCc;
	}
	
	/**
	 * Gets the mail subject.
	 *
	 * @return the mail subject
	 */
	public String getMailSubject() {
		return mailSubject;
	}
	
	/**
	 * Sets the mail subject.
	 *
	 * @param mailSubject the new mail subject
	 */
	public void setMailSubject(String mailSubject) {
		this.mailSubject = mailSubject;
	}
	
	/**
	 * Gets the mail message.
	 *
	 * @return the mail message
	 */
	public String getMailMessage() {
		return mailMessage;
	}
	
	/**
	 * Sets the mail message.
	 *
	 * @param mailMessage the new mail message
	 */
	public void setMailMessage(String mailMessage) {
		this.mailMessage = mailMessage;
	}
	
	/**
	 * Gets the mail from.
	 *
	 * @return the mail from
	 */
	public String getMailFrom() {
		return mailFrom;
	}
	
	/**
	 * Sets the mail from.
	 *
	 * @param mailFrom the new mail from
	 */
	public void setMailFrom(String mailFrom) {
		this.mailFrom = mailFrom;
	}

	public String getTuring() {
		return turing;
	}

	public void setTuring(String turing) {
		this.turing = turing;
	}

	public String getCaptchaId() {
		return captchaId;
	}

	public void setCaptchaId(String captchaId) {
		this.captchaId = captchaId;
	}
	
}
