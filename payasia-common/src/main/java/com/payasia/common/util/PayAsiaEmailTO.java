/*
 * author vivek jain
 */
package com.payasia.common.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * The Class PayAsiaEmailTO.
 */
public class PayAsiaEmailTO implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The mail to. */
	private List<String> mailTo = new ArrayList<String>();
	
	/** The mail cc. */
	private List<String> mailCc = new ArrayList<String>();
	
	/** The mail bcc. */
	private List<String> mailBcc = new ArrayList<String>();
	
	/** The mail attachments. */
	private List<PayAsiaAttachmentUtils> mailAttachments = new ArrayList<PayAsiaAttachmentUtils>();

	/** The mail from. */
	private String mailFrom;
	
	/** The mail text. */
	private String mailText;
	
	/** The mail subject. */
	private String mailSubject;
	
	/**
	 * Gets the mail attachments.
	 *
	 * @return the mail attachments
	 */
	public List<PayAsiaAttachmentUtils> getMailAttachments() {
		return mailAttachments;
	}
	
	/**
	 * Adds the attachment.
	 *
	 * @param attachment the attachment
	 */
	public void addAttachment(PayAsiaAttachmentUtils attachment) {
		mailAttachments.add(attachment);
	}

	
	/**
	 * Gets the mail to.
	 *
	 * @return the mail to
	 */
	public List<String> getMailTo() {
		return mailTo;
	}

	/**
	 * Gets the mail cc.
	 *
	 * @return the mail cc
	 */
	public List<String> getMailCc() {
		return mailCc;
	}

	/**
	 * Gets the mail bcc.
	 *
	 * @return the mail bcc
	 */
	public List<String> getMailBcc() {
		return mailBcc;
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

	/**
	 * Gets the mail text.
	 *
	 * @return the mail text
	 */
	public String getMailText() {
		return mailText;
	}

	/**
	 * Sets the mail text.
	 *
	 * @param mailText the new mail text
	 */
	public void setMailText(String mailText) {
		this.mailText = mailText;
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
	 * Adds the mail to.
	 *
	 * @param to the to
	 */
	public void addMailTo(String to) {
		mailTo.add(to);
	}

	/**
	 * Adds the main cc.
	 *
	 * @param cc the cc
	 */
	public void addMainCc(String cc) {
		mailCc.add(cc);
	}

	/**
	 * Adds the mail b cc.
	 *
	 * @param bcc the bcc
	 */
	public void addMailBCc(String bcc) {
		mailBcc.add(bcc);
	}

}
