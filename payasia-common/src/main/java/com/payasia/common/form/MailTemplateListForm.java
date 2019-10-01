/**
 * @author abhisheksachdeva
 *
 */
package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

import com.payasia.common.dto.EmailAttachmentDTO;


/**
 * The Class MailTemplateListForm.
 */
public class MailTemplateListForm implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -487916817298352770L;

	/** The template id. */
	private long templateId;

	/** The category. */
	private String category;
	
	/** The category id. */
	private long categoryId;
	
	/** The subCategoryName. */
	private String subCategoryName;
	
	/** The subCategoryId */
	private long subCategoryId;

	public String getSubCategoryName() {
		return subCategoryName;
	}

	public void setSubCategoryName(String subCategoryName) {
		this.subCategoryName = subCategoryName;
	}

	public long getSubCategoryId() {
		return subCategoryId;
	}

	public void setSubCategoryId(long subCategoryId) {
		this.subCategoryId = subCategoryId;
	}

	/** The name. */
	private String name;

	/** The subject. */
	private String subject;

	/** The configure. */
	private String configure;

	/** The fixed to. */
	private String fixedTo;

	/** The fixed cc. */
	private String fixedCc;

	/** The email body. */
	private String emailBody;

	/** The attachment list. */
	private List<EmailAttachmentDTO> attachmentList;
	
	

	/**
	 * Gets the attachment list.
	 *
	 * @return the attachment list
	 */
	public List<EmailAttachmentDTO> getAttachmentList() {
		return attachmentList;
	}

	/**
	 * Sets the attachment list.
	 *
	 * @param attachmentList the new attachment list
	 */
	public void setAttachmentList(List<EmailAttachmentDTO> attachmentList) {
		this.attachmentList = attachmentList;
	}

	/**
	 * Gets the category id.
	 *
	 * @return the category id
	 */
	public long getCategoryId() {
		return categoryId;
	}

	/**
	 * Sets the category id.
	 *
	 * @param categoryId the new category id
	 */
	public void setCategoryId(long categoryId) {
		this.categoryId = categoryId;
	}

	/**
	 * Gets the configure.
	 * 
	 * @return the configure
	 */
	public String getConfigure() {
		return configure;
	}

	/**
	 * Sets the configure.
	 * 
	 * @param configure
	 *            the new configure
	 */
	public void setConfigure(String configure) {
		this.configure = configure;
	}

	/**
	 * Gets the category.
	 * 
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * Sets the category.
	 * 
	 * @param category
	 *            the new category
	 */
	public void setCategory(String category) {
		this.category = category;
	}

	/**
	 * Gets the name.
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 * 
	 * @param name
	 *            the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the subject.
	 * 
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * Sets the subject.
	 * 
	 * @param subject
	 *            the new subject
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * Gets the fixed to.
	 * 
	 * @return the fixed to
	 */
	public String getFixedTo() {
		return fixedTo;
	}

	/**
	 * Sets the fixed to.
	 * 
	 * @param fixedTo
	 *            the new fixed to
	 */
	public void setFixedTo(String fixedTo) {
		this.fixedTo = fixedTo;
	}

	/**
	 * Gets the fixed cc.
	 * 
	 * @return the fixed cc
	 */
	public String getFixedCc() {
		return fixedCc;
	}

	/**
	 * Sets the fixed cc.
	 * 
	 * @param fixedCc
	 *            the new fixed cc
	 */
	public void setFixedCc(String fixedCc) {
		this.fixedCc = fixedCc;
	}

	/**
	 * Gets the email body.
	 * 
	 * @return the email body
	 */
	public String getEmailBody() {
		return emailBody;
	}

	/**
	 * Sets the email body.
	 * 
	 * @param emailBody
	 *            the new email body
	 */
	public void setEmailBody(String emailBody) {
		this.emailBody = emailBody;
	}

	/**
	 * Gets the template id.
	 * 
	 * @return the template id
	 */
	public long getTemplateId() {
		return templateId;
	}

	/**
	 * Sets the template id.
	 * 
	 * @param templateId
	 *            the new template id
	 */
	public void setTemplateId(long templateId) {
		this.templateId = templateId;
	}

}
