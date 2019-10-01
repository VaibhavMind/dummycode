package com.payasia.dao;

import com.payasia.dao.bean.EmailTemplateAttachment;

/**
 * The Interface EmailTemplateAttachmentDAO.
 */
public interface EmailTemplateAttachmentDAO {

	/**
	 * Save EmailTemplateAttachment Object.
	 * 
	 * @param emailTemplateAttachment
	 *            the email template attachment
	 */
	void save(EmailTemplateAttachment emailTemplateAttachment);

	/**
	 * Find EmailTemplateAttachment Object by attachmentId.
	 * 
	 * @param attachmentId
	 *            the attachment id
	 * @return the email template attachment
	 */
	EmailTemplateAttachment findById(long attachmentId);

	/**
	 * Update EmailTemplateAttachment Object.
	 * 
	 * @param emailTemplateAttachment
	 *            the email template attachment
	 */
	void update(EmailTemplateAttachment emailTemplateAttachment);

	/**
	 * Delete EmailTemplateAttachment Object.
	 * 
	 * @param emailTemplateAttachment
	 *            the email template attachment
	 */
	void delete(EmailTemplateAttachment emailTemplateAttachment);
	
	EmailTemplateAttachment findById(long attachmentId,Long companyId);
}
